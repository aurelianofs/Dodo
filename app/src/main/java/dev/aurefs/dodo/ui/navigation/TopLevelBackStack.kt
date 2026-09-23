package dev.aurefs.dodo.ui.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey

// I chose a single back stack architecture for the Tab Switching
// I also chose a strict hierarchical back button logic over chronological
class TopLevelBackStack<T : NavKey>(private val fixedStartKey: T) {

    val backStack = mutableStateListOf(fixedStartKey)

    val topLevelKey: T
        get() = backStack.first()

    fun switchTopLevel(key: T) {
        backStack.clear()
        backStack.add(key)
    }

    fun add(key: T) {
        backStack.add(key)
    }

    fun handleBack(): BackOutcome {
        return when {
            backStack.size > 1 -> {
                backStack.removeAt(backStack.lastIndex)
                BackOutcome.Handled
            }
            topLevelKey != fixedStartKey -> {
                switchTopLevel(fixedStartKey)
                BackOutcome.Handled
            }
            else -> BackOutcome.ConfirmExit
        }
    }

    enum class BackOutcome { Handled, ConfirmExit }
}
