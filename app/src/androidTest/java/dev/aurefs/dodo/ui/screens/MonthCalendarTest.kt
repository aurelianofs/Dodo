package dev.aurefs.dodo.ui.screens

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class MonthCalendarTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val today = LocalDate.of(2026, 9, 26)
    private val currentMonth = YearMonth.from(today)

    private fun setCalendar(
        month: YearMonth = currentMonth,
        scores: Map<LocalDate, Int> = emptyMap(),
        onMonthChange: (YearMonth) -> Unit = {},
        onDayClick: (LocalDate) -> Unit = {}
    ) {
        composeRule.setContent {
            MonthCalendar(
                month = month,
                scores = scores,
                today = today,
                onMonthChange = onMonthChange,
                onDayClick = onDayClick,
                firstDayOfWeek = DayOfWeek.MONDAY
            )
        }
    }

    @Test
    fun showsScoresWithSign() {
        setCalendar(scores = mapOf(today.minusDays(1) to 21, today.minusDays(2) to -40))

        composeRule.onNodeWithText("+21").assertExists()
        composeRule.onNodeWithText("-40").assertExists()
    }

    @Test
    fun trackedDaysOpenTheirDetail() {
        val tracked = today.minusDays(1)
        var clicked: LocalDate? = null
        setCalendar(scores = mapOf(tracked to 21), onDayClick = { clicked = it })

        composeRule.onNodeWithText("+21").performClick()

        assertEquals(tracked, clicked)
    }

    @Test
    fun untrackedDaysAreNotClickable() {
        setCalendar(scores = mapOf(today to 5))

        composeRule.onNodeWithText("3", useUnmergedTree = false).assertHasNoClickAction()
        composeRule.onNodeWithText("26").assertHasClickAction()
    }

    @Test
    fun cannotGoPastTheCurrentMonth() {
        setCalendar()

        composeRule.onNodeWithContentDescription("Next month").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("Previous month").assertIsEnabled()
    }

    @Test
    fun arrowsChangeTheMonth() {
        var month: YearMonth? = null
        setCalendar(month = currentMonth.minusMonths(2), onMonthChange = { month = it })

        composeRule.onNodeWithContentDescription("Next month").assertIsEnabled().performClick()
        assertEquals(currentMonth.minusMonths(1), month)

        composeRule.onNodeWithContentDescription("Previous month").performClick()
        assertEquals(currentMonth.minusMonths(3), month)
    }
}
