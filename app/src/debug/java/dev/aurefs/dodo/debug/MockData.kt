package dev.aurefs.dodo.debug

import androidx.room.withTransaction
import dev.aurefs.dodo.data.AppDatabase
import dev.aurefs.dodo.data.Doable
import dev.aurefs.dodo.data.DoableEntry
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import kotlin.random.Random

object MockData {

    private const val DAYS = 60

    private data class Sample(val doable: Doable, val doneChance: Double)

    private val samples = listOf(
        Sample(Doable(title = "Take pill", meritLevel = 1, costLevel = 5), 0.9),
        Sample(Doable(title = "Brush teeth", meritLevel = 2, costLevel = 4), 0.85),
        Sample(Doable(title = "Wash dishes", meritLevel = 3, costLevel = 2), 0.6),
        Sample(Doable(title = "Piano practice", meritLevel = 4, costLevel = 2), 0.5),
        Sample(Doable(title = "Workout", meritLevel = 4, costLevel = 2), 0.45),
        Sample(Doable(title = "Duolingo", meritLevel = 2, costLevel = 1), 0.7)
    )

    suspend fun seed(db: AppDatabase, reset: Boolean, today: LocalDate = LocalDate.now()): String {
        if (reset) {
            db.clearAllTables()
        } else if (db.doableDao().getActiveDoables().first().isNotEmpty() || db.doableEntryDao().getLatestDate() != null) {
            return "Database is not empty. Add --ez reset true to clear it first."
        }

        db.withTransaction {
            val entries = samples.flatMap { sample ->
                val id = db.doableDao().insertDoable(sample.doable).toInt()
                (DAYS - 1 downTo 0).map { daysAgo ->
                    val date = today.minusDays(daysAgo.toLong())
                    DoableEntry(
                        doableId = id,
                        date = date,
                        done = date < today && Random.nextDouble() < sample.doneChance
                    )
                }
            }
            db.doableEntryDao().insertEntries(entries)
        }
        return "Seeded ${samples.size} doables over $DAYS days."
    }
}
