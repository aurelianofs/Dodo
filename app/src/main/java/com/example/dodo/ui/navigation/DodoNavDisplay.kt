package com.example.dodo.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.dodo.ui.DoableViewModel
import com.example.dodo.ui.screens.DoableListScreen

@Composable
fun DodoNavDisplay() {
    val topLevelBackStack = remember { TopLevelBackStack<Routes>(Routes.Index) }
    val doableViewModel: DoableViewModel = viewModel()

    Scaffold(
        bottomBar = {
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
    ) { padding ->
        NavDisplay(
            modifier = Modifier.padding(padding),
            backStack = topLevelBackStack.backStack,
            onBack = { topLevelBackStack.removeLast() },
            entryProvider = entryProvider {
                entry<Routes.Index> {
                    Text("Index")
                }
                entry<Routes.List> {
                    DoableListScreen(
                        viewModel = doableViewModel,
                        onAddClick = { topLevelBackStack.add(Routes.Edit()) },
                        onEditClick = { id -> topLevelBackStack.add(Routes.Edit(doableId = id)) }
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
}