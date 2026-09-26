package dev.aurefs.dodo.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class MonthWeeksTest {

    private val september = YearMonth.of(2026, 9)

    @Test
    fun everyWeekHasSevenCells() {
        val weeks = monthWeeks(september, DayOfWeek.MONDAY)

        assertTrue(weeks.all { it.size == 7 })
    }

    @Test
    fun containsEveryDayOnceInOrder() {
        val days = monthWeeks(september, DayOfWeek.MONDAY).flatten().filterNotNull()

        assertEquals((1..30).map { september.atDay(it) }, days)
    }

    @Test
    fun firstDayLandsOnItsWeekdayColumn() {
        val mondayFirst = monthWeeks(september, DayOfWeek.MONDAY)
        val sundayFirst = monthWeeks(september, DayOfWeek.SUNDAY)

        assertEquals(LocalDate.of(2026, 9, 1), mondayFirst[0][1])
        assertNull(mondayFirst[0][0])
        assertEquals(LocalDate.of(2026, 9, 1), sundayFirst[0][2])
    }

    @Test
    fun monthStartingOnFirstDayOfWeekHasNoLeadingBlanks() {
        val june = YearMonth.of(2026, 6)

        assertEquals(june.atDay(1), monthWeeks(june, DayOfWeek.MONDAY)[0][0])
    }
}
