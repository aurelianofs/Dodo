package dev.aurefs.dodo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.aurefs.dodo.data.AppDatabase
import dev.aurefs.dodo.data.Doable
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
        saveDoable(id = null, title = title, merit = 1, cost = 1)
    }

    fun saveDoable(id: Int?, title: String, merit: Int, cost: Int) {
        val trimmed = title.trim()
        if (trimmed.isBlank()) return
        viewModelScope.launch {
            if (id == null) {
                dao.insertDoable(Doable(title = trimmed, merit = merit, cost = cost))
            } else {
                dao.updateDoable(Doable(id = id, title = trimmed, merit = merit, cost = cost))
            }
        }
    }

    suspend fun findDoable(id: Int): Doable? = dao.getDoableById(id)

    fun deleteDoable(doable: Doable) {
        viewModelScope.launch {
            dao.deleteDoable(doable)
        }
    }
}