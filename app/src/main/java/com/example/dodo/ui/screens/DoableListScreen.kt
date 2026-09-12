package com.example.dodo.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dodo.data.Doable
import com.example.dodo.ui.DoableViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoableListScreen(
    viewModel: DoableViewModel,
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit
) {
    val doables by viewModel.doables.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dodo") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Doable")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(doables, key = { it.id }) { doable ->
                DoableListRow(
                    doable = doable,
                    onClick = { onEditClick(doable.id) },
                    onDelete = { viewModel.deleteDoable(doable) }
                )
            }
        }
    }
}

@Composable
private fun DoableListRow(
    doable: Doable,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(doable.title) },
        supportingContent = { Text("Merit: ${doable.merit}   Cost: ${doable.cost}") },
        trailingContent = {
            TextButton(onClick = onDelete) { Text("Delete") }
        }
    )
}