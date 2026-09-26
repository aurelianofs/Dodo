package dev.aurefs.dodo.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.aurefs.dodo.ui.DoableViewModel
import java.time.LocalDate

@Composable
fun DayDetailScreen(date: LocalDate, viewModel: DoableViewModel) {
    val entries by remember(date) { viewModel.entriesFor(date) }.collectAsState(initial = emptyList())

    DayScreen(
        entries = entries,
        emptyText = "Nothing was tracked on this day.",
        onDoneChange = viewModel::setDone
    )
}
