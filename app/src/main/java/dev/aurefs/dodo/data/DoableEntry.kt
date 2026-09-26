package dev.aurefs.dodo.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDate

@Entity(
    tableName = "doable_entries",
    foreignKeys = [
        ForeignKey(
            entity = Doable::class,
            parentColumns = ["id"],
            childColumns = ["doableId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("date"),
        Index(value = ["doableId", "date"], unique = true)
    ]
)
data class DoableEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val doableId: Int,
    val date: LocalDate,
    val done: Boolean = false
)

data class EntryWithDoable(
    @Embedded val entry: DoableEntry,
    @Relation(parentColumn = "doableId", entityColumn = "id")
    val doable: Doable
)
