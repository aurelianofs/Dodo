package dev.aurefs.dodo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "doables"
)
data class Doable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val meritLevel: Int = 1,
    val costLevel: Int = 1,
    val archived: Boolean = false
) {
    init {
        require(meritLevel in 1..5) { "meritLevel must be between 1 and 5 (was $meritLevel)" }
        require(costLevel in 1..5) { "costLevel must be between 1 and 5 (was $costLevel)" }
    }
}
