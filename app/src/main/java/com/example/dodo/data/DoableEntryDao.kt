package com.example.dodo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DoableEntryDao {

    @Query("SELECT * FROM doable_entries WHERE date = :date")
    fun getEntriesForDate(date: LocalDate): Flow<List<DoableEntry>>

    @Query("SELECT * FROM doable_entries WHERE date BETWEEN :startDate AND :endDate")
    fun getEntriesBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<DoableEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEntry(entry: DoableEntry)
}