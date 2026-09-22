package com.example.dodo.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

interface Layout {
    val hasBottomBar: Boolean
    val hasBackButton: Boolean
}

sealed interface Routes : NavKey, Layout {

    // Tab roots
    sealed interface TopLevel : Routes {
        override val hasBottomBar get() = true
        override val hasBackButton get() = false
    }

    // Drill-down inside a tab
    sealed interface Detail : Routes {
        override val hasBottomBar get() = true
        override val hasBackButton get() = true
    }

    // Focused tasks like editors and forms
    sealed interface Flow : Routes {
        override val hasBottomBar get() = false
        override val hasBackButton get() = true
    }

    // No app chrome at all
    sealed interface Immersive : Routes {
        override val hasBottomBar get() = false
        override val hasBackButton get() = false
    }

    @Serializable
    data object Index : TopLevel

    @Serializable
    data object List : TopLevel

    @Serializable
    data class Edit(val doableId: Int? = null) : Flow

    @Serializable
    data object Calendar : TopLevel

    @Serializable
    data class DayDetail(val date: String) : Detail
}

val topLevelRoutes: kotlin.collections.List<Routes.TopLevel> = listOf(Routes.Index, Routes.List, Routes.Calendar)
