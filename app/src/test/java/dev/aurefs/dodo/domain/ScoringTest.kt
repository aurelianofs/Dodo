package dev.aurefs.dodo.domain

import dev.aurefs.dodo.data.Doable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ScoringTest {

    private fun item(merit: Int, cost: Int, done: Boolean) =
        DayItem(Doable(title = "test", merit = merit, cost = cost), done)

    @Test
    fun emptyDayScoresZero() {
        assertEquals(0, Scoring.dayScore(emptyList()))
    }

    @Test
    fun nothingDoneScoresMinusAllCosts() {
        val items = listOf(item(merit = 5, cost = 2, done = false), item(merit = 1, cost = 4, done = false))
        val expected = -(Scoring.costPoints(2) + Scoring.costPoints(4))
        assertEquals(expected, Scoring.dayScore(items))
    }

    @Test
    fun everythingDoneScoresAllMerits() {
        val items = listOf(item(merit = 5, cost = 2, done = true), item(merit = 1, cost = 4, done = true))
        val expected = Scoring.meritPoints(5) + Scoring.meritPoints(1)
        assertEquals(expected, Scoring.dayScore(items))
    }

    @Test
    fun mixedDayAddsMeritsAndSubtractsCosts() {
        val items = listOf(item(merit = 3, cost = 1, done = true), item(merit = 2, cost = 5, done = false))
        val expected = Scoring.meritPoints(3) - Scoring.costPoints(5)
        assertEquals(expected, Scoring.dayScore(items))
    }

    @Test
    fun checkingAnItemRaisesScoreByItsMeritPlusCost() {
        val before = listOf(item(merit = 4, cost = 3, done = false))
        val after = listOf(item(merit = 4, cost = 3, done = true))
        val delta = Scoring.dayScore(after) - Scoring.dayScore(before)
        assertEquals(Scoring.meritPoints(4) + Scoring.costPoints(3), delta)
    }

    @Test
    fun pointsGrowWithLevel() {
        for (level in Scoring.MIN_LEVEL until Scoring.MAX_LEVEL) {
            assertTrue(Scoring.meritPoints(level) < Scoring.meritPoints(level + 1))
            assertTrue(Scoring.costPoints(level) < Scoring.costPoints(level + 1))
        }
    }

    @Test
    fun outOfRangeLevelsAreRejected() {
        assertThrows(IllegalArgumentException::class.java) { Scoring.meritPoints(0) }
        assertThrows(IllegalArgumentException::class.java) { Scoring.costPoints(6) }
    }
}
