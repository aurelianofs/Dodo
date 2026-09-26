package dev.aurefs.dodo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DoableEntryDao {

    @Transaction
    @Query("SELECT * FROM doable_entries WHERE date = :date ORDER BY doableId DESC")
    fun getEntriesForDate(date: LocalDate): Flow<List<EntryWithDoable>>

    @Transaction
    @Query("SELECT * FROM doable_entries WHERE date BETWEEN :startDate AND :endDate")
    fun getEntriesBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<EntryWithDoable>>

    @Query("SELECT MAX(date) FROM doable_entries")
    suspend fun getLatestDate(): LocalDate?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEntries(entries: List<DoableEntry>)

    @Query("SELECT id FROM doables WHERE archived = 0")
    suspend fun getActiveDoableIds(): List<Int>

    @Query("UPDATE doable_entries SET done = :done WHERE doableId = :doableId AND date = :date")
    suspend fun setDone(doableId: Int, date: LocalDate, done: Boolean)

    @Transaction
    suspend fun ensureDaysUpTo(today: LocalDate) {
        val start = getLatestDate()?.plusDays(1) ?: today
        if (start > today) return
        val doableIds = getActiveDoableIds()
        val entries = generateSequence(start) { it.plusDays(1) }
            .takeWhile { it <= today }
            .flatMap { date -> doableIds.map { DoableEntry(doableId = it, date = date) } }
            .toList()
        insertEntries(entries)
    }
}
