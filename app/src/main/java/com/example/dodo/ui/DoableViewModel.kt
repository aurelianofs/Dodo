package com.example.dodo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dodo.data.AppDatabase
import com.example.dodo.data.Doable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DoableViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).doableDao()

    val doables: StateFlow<List<Doable>> = dao.getAllDoables()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addDoable(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            dao.insertDoable(Doable(title = title))
        }
    }

    fun deleteDoable(doable: Doable) {
        viewModelScope.launch {
            dao.deleteDoable(doable)
        }
    }
}