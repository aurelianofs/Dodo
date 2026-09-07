package com.example.dodo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dodo.data.Doable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoableScreen(viewModel: DoableViewModel) {
    val doables by viewModel.doables.collectAsState()
    var newTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Dodo") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("New item") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    viewModel.addDoable(newTitle)
                    newTitle = ""
                }) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(doables, key = { it.id }) { doable ->
                    DoableRow(
                        doable = doable,
                        onDelete = { viewModel.deleteDoable(doable) }
                    )
                }
            }
        }
    }
}

@Composable
fun DoableRow(doable: Doable, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = doable.title, modifier = Modifier.weight(1f))
        TextButton(onClick = onDelete) {
            Text("Delete")
        }
    }
}