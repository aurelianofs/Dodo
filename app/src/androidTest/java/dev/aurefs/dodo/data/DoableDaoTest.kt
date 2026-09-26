package dev.aurefs.dodo.data

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class DoableDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var doableDao: DoableDao
    private lateinit var entryDao: DoableEntryDao

    private val today = LocalDate.of(2026, 9, 25)
    private val yesterday = today.minusDays(1)

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        doableDao = db.doableDao()
        entryDao = db.doableEntryDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private suspend fun insertDoable(title: String = "test"): Doable {
        val id = doableDao.insertDoable(Doable(title = title)).toInt()
        return doableDao.getDoableById(id)!!
    }

    @Test
    fun removeDoableWithOnlyTodaysEntryDeletesIt() = runBlocking {
        val doable = insertDoable()
        entryDao.insertEntries(listOf(DoableEntry(doableId = doable.id, date = today)))

        doableDao.removeDoable(doable, today)

        assertNull(doableDao.getDoableById(doable.id))
        assertTrue(entryDao.getEntriesForDate(today).first().isEmpty())
    }

    @Test
    fun removeDoableWithHistoryArchivesItAndDropsTodaysEntry() = runBlocking {
        val doable = insertDoable()
        entryDao.insertEntries(
            listOf(
                DoableEntry(doableId = doable.id, date = yesterday, done = true),
                DoableEntry(doableId = doable.id, date = today)
            )
        )

        doableDao.removeDoable(doable, today)

        assertTrue(doableDao.getDoableById(doable.id)!!.archived)
        assertTrue(doableDao.getActiveDoables().first().isEmpty())
        assertTrue(entryDao.getEntriesForDate(today).first().isEmpty())
        assertEquals(1, entryDao.getEntriesForDate(yesterday).first().size)
    }

    @Test
    fun hardDeletingADoableWithEntriesIsRejected() {
        runBlocking {
            val doable = insertDoable()
            entryDao.insertEntries(listOf(DoableEntry(doableId = doable.id, date = yesterday)))

            assertThrows(SQLiteConstraintException::class.java) {
                runBlocking { doableDao.deleteDoable(doable) }
            }
        }
    }

    @Test
    fun insertEntriesIgnoresDuplicates() = runBlocking {
        val doable = insertDoable()
        entryDao.insertEntries(listOf(DoableEntry(doableId = doable.id, date = today, done = true)))
        entryDao.insertEntries(listOf(DoableEntry(doableId = doable.id, date = today)))

        val entries = entryDao.getEntriesForDate(today).first()
        assertEquals(1, entries.size)
        assertTrue(entries.single().entry.done)
    }

    @Test
    fun entriesComeWithTheirCurrentDoable() = runBlocking {
        val doable = insertDoable("Piano")
        entryDao.insertEntries(listOf(DoableEntry(doableId = doable.id, date = yesterday)))
        doableDao.updateDoable(doable.copy(meritLevel = 4))

        val entry = entryDao.getEntriesForDate(yesterday).first().single()
        assertEquals("Piano", entry.doable.title)
        assertEquals(4, entry.doable.meritLevel)
    }

    @Test
    fun latestDateAndSetDone() = runBlocking {
        assertNull(entryDao.getLatestDate())

        val doable = insertDoable()
        entryDao.insertEntries(
            listOf(
                DoableEntry(doableId = doable.id, date = yesterday),
                DoableEntry(doableId = doable.id, date = today)
            )
        )
        assertEquals(today, entryDao.getLatestDate())

        entryDao.setDone(doable.id, yesterday, true)
        assertTrue(entryDao.getEntriesForDate(yesterday).first().single().entry.done)
    }

    @Test
    fun entriesBetweenIsInclusive() = runBlocking {
        val doable = insertDoable()
        val dates = (0L..4L).map { today.minusDays(it) }
        entryDao.insertEntries(dates.map { DoableEntry(doableId = doable.id, date = it) })

        val entries = entryDao.getEntriesBetween(today.minusDays(3), today.minusDays(1)).first()
        assertEquals(setOf(today.minusDays(3), today.minusDays(2), today.minusDays(1)), entries.map { it.entry.date }.toSet())
    }
}
