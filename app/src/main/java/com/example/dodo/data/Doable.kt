package com.example.dodo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "doables"
)
data class Doable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val merit: Int = 1,
    val cost: Int = 1
) {
    init {
        require(merit in 1..5) { "merit must be between 1 and 5 (was $merit)" }
        require(cost in 1..5) { "cost must be between 1 and 5 (was $cost)" }
    }
}