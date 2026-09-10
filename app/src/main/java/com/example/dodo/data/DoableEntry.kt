package com.example.dodo.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "doable_entries",
    foreignKeys = [
        ForeignKey(
            entity = Doable::class,
            parentColumns = ["id"],
            childColumns = ["doableId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("doableId"), Index("date")]
)
data class DoableEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val doableId: Int,
    val date: LocalDate,
    val status: EntryStatus
)
