package dev.aurefs.dodo.domain

import dev.aurefs.dodo.data.Doable

data class DayItem(val doable: Doable, val done: Boolean)

object Scoring {

    const val MIN_LEVEL = 1
    const val MAX_LEVEL = 5

    private val meritPointsByLevel = intArrayOf(1, 3, 9, 20, 50)
    private val costPointsByLevel = intArrayOf(0, 5, 10, 20, 35)

    fun meritPoints(level: Int): Int = pointsFor(meritPointsByLevel, level)

    fun costPoints(level: Int): Int = pointsFor(costPointsByLevel, level)

    fun dayScore(items: List<DayItem>): Int = items.sumOf { item ->
        if (item.done) meritPoints(item.doable.merit) else -costPoints(item.doable.cost)
    }

    private fun pointsFor(table: IntArray, level: Int): Int {
        require(level in MIN_LEVEL..MAX_LEVEL) { "level must be between $MIN_LEVEL and $MAX_LEVEL (was $level)" }
        return table[level - MIN_LEVEL]
    }
}
