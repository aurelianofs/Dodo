package dev.aurefs.dodo.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import dev.aurefs.dodo.ui.DoableViewModel
import dev.aurefs.dodo.ui.TodayDoable
import dev.aurefs.dodo.ui.navigation.ScreenLayout

@Composable
fun IndexScreen(viewModel: DoableViewModel) {
    val items by viewModel.todayDoables.collectAsState()

    ScreenLayout { padding ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No doables yet. Add some in the Doables tab.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(items, key = { it.doable.id }) { item ->
                    TodayDoableRow(
                        item = item,
                        onDoneChange = { done -> viewModel.setDone(item, done) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayDoableRow(
    item: TodayDoable,
    onDoneChange: (Boolean) -> Unit
) {
    ListItem(
        modifier = Modifier.toggleable(
            value = item.isDone,
            role = Role.Checkbox,
            onValueChange = onDoneChange
        ),
        leadingContent = {
            Checkbox(checked = item.isDone, onCheckedChange = null)
        },
        headlineContent = {
            Text(
                text = "${item.doable.title} (merit: ${item.doable.meritLevel}, cost: ${item.doable.costLevel})",
                textDecoration = if (item.isDone) TextDecoration.LineThrough else null,
                color = if (item.isDone) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    )
}
