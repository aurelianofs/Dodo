package dev.aurefs.dodo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DoableDao {
    @Query("SELECT * FROM doables WHERE archived = 0 ORDER BY id DESC")
    fun getActiveDoables(): Flow<List<Doable>>

    @Query("SELECT * FROM doables WHERE id = :id")
    suspend fun getDoableById(id: Int): Doable?

    @Insert
    suspend fun insertDoable(doable: Doable): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEntry(entry: DoableEntry)

    @Transaction
    suspend fun insertDoableForDay(doable: Doable, date: LocalDate) {
        val id = insertDoable(doable).toInt()
        insertEntry(DoableEntry(doableId = id, date = date))
    }

    @Update
    suspend fun updateDoable(doable: Doable)

    @Delete
    suspend fun deleteDoable(doable: Doable)

    @Query("UPDATE doables SET archived = 1 WHERE id = :id")
    suspend fun archiveDoable(id: Int)

    @Query("DELETE FROM doable_entries WHERE doableId = :doableId AND date = :date")
    suspend fun deleteEntry(doableId: Int, date: LocalDate)

    @Query("SELECT COUNT(*) FROM doable_entries WHERE doableId = :doableId AND date < :today")
    suspend fun countPastEntries(doableId: Int, today: LocalDate): Int

    @Transaction
    suspend fun removeDoable(doable: Doable, today: LocalDate) {
        deleteEntry(doable.id, today)
        if (countPastEntries(doable.id, today) == 0) {
            deleteDoable(doable)
        } else {
            archiveDoable(doable.id)
        }
    }
}
