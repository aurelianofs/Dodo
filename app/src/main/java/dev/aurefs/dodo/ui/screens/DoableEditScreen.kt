package dev.aurefs.dodo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import dev.aurefs.dodo.ui.DoableViewModel
import dev.aurefs.dodo.ui.navigation.ScreenLayout

@Composable
fun DoableEditScreen(
    doableId: Int?,
    viewModel: DoableViewModel,
    onSaved: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var merit by remember { mutableIntStateOf(1) }
    var cost by remember { mutableIntStateOf(1) }
    var notFound by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val canSave = title.isNotBlank() && !notFound

    LaunchedEffect(doableId) {
        if (doableId == null) {
            focusRequester.requestFocus()
            return@LaunchedEffect
        }
        val existing = viewModel.findDoable(doableId)
        if (existing == null) {
            notFound = true
        } else {
            title = existing.title
            merit = existing.meritLevel
            cost = existing.costLevel
        }
    }

    ScreenLayout(
        actions = {
            TextButton(
                onClick = {
                    viewModel.saveDoable(doableId, title, merit, cost)
                    onSaved()
                },
                enabled = canSave
            ) {
                Text("Save")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (notFound) {
                Text("This doable could not be found.")
            } else {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
                ScaleSelector(
                    label = "Merit",
                    value = merit,
                    onValueChange = { merit = it }
                )
                ScaleSelector(
                    label = "Cost",
                    value = cost,
                    onValueChange = { cost = it }
                )
            }
        }
    }
}

@Composable
private fun ScaleSelector(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    val options = 1..5
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEach { rating ->
                SegmentedButton(
                    selected = value == rating,
                    onClick = { onValueChange(rating) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = rating - 1,
                        count = options.count()
                    )
                ) {
                    Text(rating.toString())
                }
            }
        }
    }
}
