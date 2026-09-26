package dev.aurefs.dodo.ui.screens

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.aurefs.dodo.data.Doable
import dev.aurefs.dodo.data.DoableEntry
import dev.aurefs.dodo.data.EntryWithDoable
import dev.aurefs.dodo.domain.Scoring
import dev.aurefs.dodo.ui.navigation.LocalRouteChrome
import dev.aurefs.dodo.ui.navigation.RouteChrome
import dev.aurefs.dodo.ui.navigation.Routes
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class DayScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val date = LocalDate.of(2026, 9, 20)

    private fun entry(id: Int, title: String, merit: Int, cost: Int, done: Boolean) = EntryWithDoable(
        DoableEntry(id = id, doableId = id, date = date, done = done),
        Doable(id = id, title = title, meritLevel = merit, costLevel = cost)
    )

    private fun setDayScreen(
        entries: List<EntryWithDoable>,
        onDoneChange: (EntryWithDoable, Boolean) -> Unit = { _, _ -> }
    ) {
        composeRule.setContent {
            CompositionLocalProvider(LocalRouteChrome provides RouteChrome(Routes.DayDetail(date.toString())) {}) {
                DayScreen(entries = entries, emptyText = "Empty day", onDoneChange = onDoneChange)
            }
        }
    }

    @Test
    fun showsTheDayScore() {
        val entries = listOf(
            entry(1, "Piano", merit = 4, cost = 2, done = true),
            entry(2, "Pill", merit = 1, cost = 5, done = false)
        )
        setDayScreen(entries)

        val expected = Scoring.meritPoints(4) - Scoring.costPoints(5)
        composeRule.onNodeWithText(expected.toString()).assertExists()
    }

    @Test
    fun positiveScoreHasAPlusSign() {
        setDayScreen(listOf(entry(1, "Piano", merit = 4, cost = 2, done = true)))

        composeRule.onNodeWithText("+${Scoring.meritPoints(4)}").assertExists()
    }

    @Test
    fun rowsReflectDoneState() {
        setDayScreen(
            listOf(
                entry(1, "Piano", merit = 4, cost = 2, done = true),
                entry(2, "Pill", merit = 1, cost = 5, done = false)
            )
        )

        composeRule.onNodeWithText("Piano", substring = true).assertIsOn()
        composeRule.onNodeWithText("Pill", substring = true).assertIsOff()
    }

    @Test
    fun tappingARowTogglesItsEntry() {
        val pill = entry(2, "Pill", merit = 1, cost = 5, done = false)
        var toggled: Pair<EntryWithDoable, Boolean>? = null
        setDayScreen(listOf(pill)) { item, done -> toggled = item to done }

        composeRule.onNodeWithText("Pill", substring = true).performClick()

        assertEquals(pill to true, toggled)
    }

    @Test
    fun showsEmptyText() {
        setDayScreen(emptyList())

        composeRule.onNodeWithText("Empty day").assertExists()
    }

    @Test
    fun titleShowsTheDate() {
        setDayScreen(emptyList())

        composeRule.onNodeWithText(Routes.DayDetail(date.toString()).title).assertExists()
    }
}
