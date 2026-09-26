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
    @Query("SELECT * FROM doable_entries WHERE date = :date")
    fun getEntriesForDate(date: LocalDate): Flow<List<EntryWithDoable>>

    @Transaction
    @Query("SELECT * FROM doable_entries WHERE date BETWEEN :startDate AND :endDate")
    fun getEntriesBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<EntryWithDoable>>

    @Query("SELECT MAX(date) FROM doable_entries")
    suspend fun getLatestDate(): LocalDate?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEntries(entries: List<DoableEntry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEntry(entry: DoableEntry)

    @Query("UPDATE doable_entries SET done = :done WHERE doableId = :doableId AND date = :date")
    suspend fun setDone(doableId: Int, date: LocalDate, done: Boolean)
}
