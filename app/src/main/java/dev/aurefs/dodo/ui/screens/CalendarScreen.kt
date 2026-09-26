package dev.aurefs.dodo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.aurefs.dodo.ui.DoableViewModel
import dev.aurefs.dodo.ui.navigation.ScreenLayout
import dev.aurefs.dodo.ui.theme.ScoreNegative
import dev.aurefs.dodo.ui.theme.ScorePositive
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: DoableViewModel,
    onDayClick: (LocalDate) -> Unit
) {
    val today by viewModel.currentDate.collectAsState()
    var month by rememberSaveable { mutableStateOf(YearMonth.from(today)) }
    val scores by remember(month) { viewModel.scoresFor(month) }.collectAsState(initial = emptyMap())

    ScreenLayout { padding ->
        MonthCalendar(
            month = month,
            scores = scores,
            today = today,
            onMonthChange = { month = it },
            onDayClick = onDayClick,
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun MonthCalendar(
    month: YearMonth,
    scores: Map<LocalDate, Int>,
    today: LocalDate,
    onMonthChange: (YearMonth) -> Unit,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    firstDayOfWeek: DayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        MonthHeader(
            month = month,
            canGoForward = month < YearMonth.from(today),
            onMonthChange = onMonthChange
        )
        WeekdayHeader(firstDayOfWeek)
        monthWeeks(month, firstDayOfWeek).forEach { week ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                week.forEach { date ->
                    Box(modifier = Modifier.weight(1f)) {
                        if (date != null) {
                            DayCell(
                                date = date,
                                score = scores[date],
                                isToday = date == today,
                                onClick = { onDayClick(date) }
                            )
                        }
                    }
                }
            }
        }
    }
}

fun monthWeeks(month: YearMonth, firstDayOfWeek: DayOfWeek): List<List<LocalDate?>> {
    val leadingBlanks = (month.atDay(1).dayOfWeek.value - firstDayOfWeek.value + 7) % 7
    val cells = List<LocalDate?>(leadingBlanks) { null } + (1..month.lengthOfMonth()).map { month.atDay(it) }
    return cells.chunked(7).map { week -> week + List(7 - week.size) { null } }
}

@Composable
private fun MonthHeader(
    month: YearMonth,
    canGoForward: Boolean,
    onMonthChange: (YearMonth) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onMonthChange(month.minusMonths(1)) }) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
        }
        Text(
            text = month.format(DateTimeFormatter.ofPattern("LLLL yyyy")),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { onMonthChange(month.plusMonths(1)) }, enabled = canGoForward) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month")
        }
    }
}

@Composable
private fun WeekdayHeader(firstDayOfWeek: DayOfWeek) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        (0L until 7L).map { firstDayOfWeek.plus(it) }.forEach { day ->
            Text(
                text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    score: Int?,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    val (background, content) = when {
        score == null -> Color.Transparent to MaterialTheme.colorScheme.onSurfaceVariant
        score > 0 -> ScorePositive to Color.White
        score < 0 -> ScoreNegative to Color.White
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = shape,
        color = background,
        contentColor = content,
        modifier = Modifier
            .aspectRatio(1f)
            .then(
                if (isToday) Modifier.border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary), shape)
                else Modifier
            )
            .then(if (score != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = date.dayOfMonth.toString(), style = MaterialTheme.typography.labelMedium)
            if (score != null) {
                Spacer(Modifier.padding(top = 2.dp))
                Text(
                    text = if (score > 0) "+$score" else score.toString(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
