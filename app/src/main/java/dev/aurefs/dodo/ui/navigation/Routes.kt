package dev.aurefs.dodo.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

interface RouteLayoutConfig {
    val title: String
    val hasBottomBar: Boolean
    val hasBackButton: Boolean
}

sealed interface Routes : NavKey, RouteLayoutConfig {

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
    data object Index : TopLevel {
        override val title get() = "Today"
    }

    @Serializable
    data object List : TopLevel {
        override val title get() = "Doables"
    }

    @Serializable
    data class Edit(val doableId: Int? = null) : Flow {
        override val title get() = if (doableId == null) "New Doable" else "Edit Doable"
    }

    @Serializable
    data object Calendar : TopLevel {
        override val title get() = "Calendar"
    }

    @Serializable
    data class DayDetail(val date: String) : Detail {
        override val title get() = date
    }
}

val topLevelRoutes: kotlin.collections.List<Routes.TopLevel> = listOf(Routes.Index, Routes.List, Routes.Calendar)
