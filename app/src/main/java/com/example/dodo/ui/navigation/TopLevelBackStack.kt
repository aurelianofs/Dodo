package com.example.dodo.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavKey

class TopLevelBackStack<T : NavKey>(startKey: T) {

    private val topLevelStacks = linkedMapOf(
        startKey to mutableStateListOf(startKey)
    )

    var topLevelKey by mutableStateOf(startKey)
        private set

    val backStack = mutableStateListOf(startKey)

    private fun sync() {
        backStack.clear()
        backStack.addAll(topLevelStacks.values.flatten())
    }

    fun switchTopLevel(key: T) {
        val stack = topLevelStacks.remove(key) ?: mutableStateListOf(key)
        topLevelStacks[key] = stack
        topLevelKey = key
        sync()
    }

    fun add(key: T) {
        topLevelStacks[topLevelKey]?.add(key)
        sync()
    }

    fun removeLast() {
        val currentStack = topLevelStacks[topLevelKey] ?: return
        if (currentStack.size > 1) {
            currentStack.removeAt(currentStack.lastIndex)
        }
        sync()
    }
}