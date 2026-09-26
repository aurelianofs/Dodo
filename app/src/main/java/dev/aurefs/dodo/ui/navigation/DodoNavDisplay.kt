package dev.aurefs.dodo.ui.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.aurefs.dodo.ui.DoableViewModel
import dev.aurefs.dodo.ui.screens.CalendarScreen
import dev.aurefs.dodo.ui.screens.DayDetailScreen
import dev.aurefs.dodo.ui.screens.DoableEditScreen
import dev.aurefs.dodo.ui.screens.DoableListScreen
import dev.aurefs.dodo.ui.screens.IndexScreen
import java.time.LocalDate

@Composable
fun DodoNavDisplay() {
    val topLevelBackStack = remember { TopLevelBackStack<Routes>(Routes.Index) }
    val doableViewModel: DoableViewModel = viewModel()
    val activity = LocalActivity.current
    var showExitConfirmation by remember { mutableStateOf(false) }

    LifecycleResumeEffect(Unit) {
        doableViewModel.refreshDate()
        onPauseOrDispose { }
    }

    val currentRoute = topLevelBackStack.backStack.last()
    val showBottomBar = currentRoute.hasBottomBar

    val onBack: () -> Unit = {
        when (topLevelBackStack.handleBack()) {
            TopLevelBackStack.BackOutcome.Handled -> Unit
            TopLevelBackStack.BackOutcome.ConfirmExit -> showExitConfirmation = true
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = topLevelBackStack.topLevelKey == Routes.Index,
                        onClick = { topLevelBackStack.switchTopLevel(Routes.Index) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Today") },
                        label = { Text("Today") }
                    )
                    NavigationBarItem(
                        selected = topLevelBackStack.topLevelKey == Routes.List,
                        onClick = { topLevelBackStack.switchTopLevel(Routes.List) },
                        icon = { Icon(Icons.Default.Checklist, contentDescription = "List") },
                        label = { Text("Doables") }
                    )
                    NavigationBarItem(
                        selected = topLevelBackStack.topLevelKey == Routes.Calendar,
                        onClick = { topLevelBackStack.switchTopLevel(Routes.Calendar) },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar") },
                        label = { Text("Calendar") }
                    )
                }
            }
        }
    ) { padding ->
        NavDisplay(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
            backStack = topLevelBackStack.backStack,
            onBack = onBack,
            entryProvider = entryProvider {
                routeEntry<Routes.Index>(onBack) {
                    IndexScreen(viewModel = doableViewModel)
                }
                routeEntry<Routes.List>(onBack) {
                    DoableListScreen(
                        viewModel = doableViewModel,
                        onAddClick = { topLevelBackStack.add(Routes.Edit()) },
                        onEditClick = { id -> topLevelBackStack.add(Routes.Edit(doableId = id)) }
                    )
                }
                routeEntry<Routes.Edit>(onBack) { key ->
                    DoableEditScreen(
                        doableId = key.doableId,
                        viewModel = doableViewModel,
                        onSaved = onBack
                    )
                }
                routeEntry<Routes.Calendar>(onBack) {
                    CalendarScreen(
                        viewModel = doableViewModel,
                        onDayClick = { date ->
                            if (date == doableViewModel.currentDate.value) {
                                topLevelBackStack.switchTopLevel(Routes.Index)
                            } else {
                                topLevelBackStack.add(Routes.DayDetail(date.toString()))
                            }
                        }
                    )
                }
                routeEntry<Routes.DayDetail>(onBack) { key ->
                    DayDetailScreen(date = LocalDate.parse(key.date), viewModel = doableViewModel)
                }
            }
        )
    }

    if (showExitConfirmation) {
        AlertDialog(
            onDismissRequest = { showExitConfirmation = false },
            title = { Text("Exit Dodo?") },
            text = { Text("Are you sure you want to close the app?") },
            confirmButton = {
                TextButton(onClick = { activity?.finish() }) {
                    Text("Exit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
