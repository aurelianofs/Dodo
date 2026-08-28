package com.example.dodo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DoableDao {

    @Query("SELECT * FROM doables ORDER BY id DESC")
    fun getAllDoables(): Flow<List<Doable>>

    @Insert
    suspend fun insertDoable(doable: Doable)

    @Update
    suspend fun updateDoable(doable: Doable)

    @Delete
    suspend fun deleteDoable(doable: Doable)
}