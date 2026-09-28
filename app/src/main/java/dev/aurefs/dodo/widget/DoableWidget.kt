package dev.aurefs.dodo.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.aurefs.dodo.MainActivity
import dev.aurefs.dodo.data.AppDatabase
import dev.aurefs.dodo.data.EntryWithDoable
import dev.aurefs.dodo.domain.Scoring
import dev.aurefs.dodo.ui.theme.ScoreNegative
import dev.aurefs.dodo.ui.theme.ScorePositive
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class DoableWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryDao = AppDatabase.getDatabase(context).doableEntryDao()
        val today = LocalDate.now()
        entryDao.ensureDaysUpTo(today)
        val todayEntries = entryDao.getEntriesForDate(today)
        val initial = todayEntries.first()

        provideContent {
            val entries by todayEntries.collectAsState(initial = initial)
            GlanceTheme {
                WidgetContent(entries)
            }
        }
    }
}

@Composable
private fun WidgetContent(entries: List<EntryWithDoable>) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.widgetBackground)
            .cornerRadius(16.dp)
            .padding(12.dp)
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                modifier = GlanceModifier.defaultWeight()
            )
            if (entries.isNotEmpty()) {
                val score = Scoring.dayScore(entries)
                val scoreColor = when {
                    score > 0 -> ColorProvider(day = ScorePositive, night = ScorePositive)
                    score < 0 -> ColorProvider(day = ScoreNegative, night = ScoreNegative)
                    else -> GlanceTheme.colors.onSurface
                }
                Text(
                    text = if (score > 0) "+$score" else score.toString(),
                    style = TextStyle(color = scoreColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                )
            }
        }

        if (entries.isEmpty()) {
            Text(
                text = "No doables yet",
                style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant)
            )
        } else {
            LazyColumn {
                items(entries, itemId = { it.entry.id.toLong() }) { item ->
                    CheckBox(
                        checked = item.entry.done,
                        onCheckedChange = actionRunCallback<ToggleEntryAction>(
                            actionParametersOf(
                                ToggleEntryAction.doableIdKey to item.entry.doableId,
                                ToggleEntryAction.epochDayKey to item.entry.date.toEpochDay(),
                                ToggleEntryAction.doneKey to !item.entry.done
                            )
                        ),
                        text = item.doable.title,
                        style = TextStyle(color = GlanceTheme.colors.onSurface),
                        modifier = GlanceModifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
