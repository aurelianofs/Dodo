package com.example.dodo.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.dodo.ui.DoableViewModel
import com.example.dodo.ui.screens.DoableListScreen

@Composable
fun DodoNavDisplay() {
    val backStack = rememberNavBackStack(Routes.List)
    val doableViewModel: DoableViewModel = viewModel()

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Routes.Index> {
                Text("Index")
            }
            entry<Routes.List> {
                DoableListScreen(
                    viewModel = doableViewModel,
                    onAddClick = { backStack.add(Routes.Edit()) },
                    onEditClick = { id -> backStack.add(Routes.Edit(doableId = id)) },
                    onCalendarClick = { backStack.add(Routes.Calendar) }
                )
            }
            entry<Routes.Edit> { key ->
                Text("Edit screen (doableId=${key.doableId})")
            }
            entry<Routes.Calendar> {
                Text("Calendar screen")
            }
            entry<Routes.DayDetail> { key ->
                Text("Day detail (date=${key.date})")
            }
        }
    )
}