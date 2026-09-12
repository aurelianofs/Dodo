package com.example.dodo.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Routes : NavKey {

    @Serializable
    data object Index : Routes

    @Serializable
    data object List : Routes

    @Serializable
    data class Edit(val doableId: Int? = null) : Routes

    @Serializable
    data object Calendar : Routes

    @Serializable
    data class DayDetail(val date: String) : Routes
}