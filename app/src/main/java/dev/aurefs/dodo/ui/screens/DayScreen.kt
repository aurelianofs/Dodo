package dev.aurefs.dodo.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import dev.aurefs.dodo.data.EntryWithDoable
import dev.aurefs.dodo.domain.Scoring
import dev.aurefs.dodo.ui.navigation.ScreenLayout
import dev.aurefs.dodo.ui.theme.ScoreNegative
import dev.aurefs.dodo.ui.theme.ScorePositive

@Composable
fun DayScreen(
    entries: List<EntryWithDoable>,
    emptyText: String,
    onDoneChange: (EntryWithDoable, Boolean) -> Unit
) {
    ScreenLayout { padding ->
        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(emptyText)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item(key = "score") {
                    ScoreHeader(score = Scoring.dayScore(entries))
                    HorizontalDivider()
                }
                items(entries, key = { it.entry.id }) { item ->
                    DayEntryRow(
                        item = item,
                        onDoneChange = { done -> onDoneChange(item, done) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreHeader(score: Int) {
    ListItem(
        headlineContent = { Text("Score") },
        trailingContent = {
            Text(
                text = if (score > 0) "+$score" else score.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = when {
                    score > 0 -> ScorePositive
                    score < 0 -> ScoreNegative
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    )
}

@Composable
private fun DayEntryRow(
    item: EntryWithDoable,
    onDoneChange: (Boolean) -> Unit
) {
    ListItem(
        modifier = Modifier.toggleable(
            value = item.entry.done,
            role = Role.Checkbox,
            onValueChange = onDoneChange
        ),
        leadingContent = {
            Checkbox(checked = item.entry.done, onCheckedChange = null)
        },
        headlineContent = {
            Text(
                text = "${item.doable.title} (merit: ${item.doable.meritLevel}, cost: ${item.doable.costLevel})",
                textDecoration = if (item.entry.done) TextDecoration.LineThrough else null,
                color = if (item.entry.done) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    )
}
