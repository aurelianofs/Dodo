package dev.aurefs.dodo.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class DayCreationTest {

    private lateinit var db: AppDatabase
    private lateinit var doableDao: DoableDao
    private lateinit var entryDao: DoableEntryDao

    private val today = LocalDate.of(2026, 9, 26)

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

    private suspend fun insertDoable(title: String = "test"): Int =
        doableDao.insertDoable(Doable(title = title)).toInt()

    private suspend fun entriesOn(date: LocalDate) = entryDao.getEntriesForDate(date).first()

    @Test
    fun firstRunCreatesOnlyToday() = runBlocking {
        val a = insertDoable()
        val b = insertDoable()

        entryDao.ensureDaysUpTo(today)

        assertEquals(setOf(a, b), entriesOn(today).map { it.doable.id }.toSet())
        assertTrue(entriesOn(today).none { it.entry.done })
        assertTrue(entriesOn(today.minusDays(1)).isEmpty())
    }

    @Test
    fun firstRunWithoutDoablesCreatesNothing() = runBlocking {
        entryDao.ensureDaysUpTo(today)

        assertNull(entryDao.getLatestDate())
    }

    @Test
    fun skippedDaysAreBackFilledAsNotDone() = runBlocking {
        val id = insertDoable()
        val lastUsed = today.minusDays(3)
        entryDao.ensureDaysUpTo(lastUsed)
        entryDao.setDone(id, lastUsed, true)

        entryDao.ensureDaysUpTo(today)

        assertTrue(entriesOn(lastUsed).single().entry.done)
        for (offset in 0L..2L) {
            val entries = entriesOn(today.minusDays(offset))
            assertEquals(1, entries.size)
            assertTrue(!entries.single().entry.done)
        }
    }

    @Test
    fun runningTwiceChangesNothing() = runBlocking {
        val id = insertDoable()
        entryDao.ensureDaysUpTo(today)
        entryDao.setDone(id, today, true)

        entryDao.ensureDaysUpTo(today)

        assertTrue(entriesOn(today).single().entry.done)
    }

    @Test
    fun clockSetBackwardsChangesNothing() = runBlocking {
        insertDoable()
        entryDao.ensureDaysUpTo(today)

        entryDao.ensureDaysUpTo(today.minusDays(5))

        assertEquals(today, entryDao.getLatestDate())
        assertTrue(entriesOn(today.minusDays(5)).isEmpty())
    }

    @Test
    fun archivedDoablesAreNotBackFilled() = runBlocking {
        val kept = insertDoable("kept")
        val archived = insertDoable("archived")
        entryDao.ensureDaysUpTo(today.minusDays(2))
        doableDao.archiveDoable(archived)

        entryDao.ensureDaysUpTo(today)

        assertEquals(listOf(kept), entriesOn(today).map { it.doable.id })
        assertEquals(listOf(kept), entriesOn(today.minusDays(1)).map { it.doable.id })
    }

    @Test
    fun addingADoableCreatesItsEntryForTheDay() = runBlocking {
        val existing = insertDoable()
        entryDao.ensureDaysUpTo(today)

        doableDao.insertDoableForDay(Doable(title = "new"), today)

        val titles = entriesOn(today).map { it.doable.title }
        assertEquals(2, titles.size)
        assertTrue("new" in titles)
        assertTrue(entriesOn(today).any { it.doable.id == existing })
    }
}
