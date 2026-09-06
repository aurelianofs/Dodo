package com.example.dodo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doables")
data class Doable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String
)