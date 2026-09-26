package dev.aurefs.dodo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.aurefs.dodo.data.AppDatabase
import dev.aurefs.dodo.data.Doable
import dev.aurefs.dodo.data.DoableEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

data class TodayDoable(val doable: Doable, val date: LocalDate, val isDone: Boolean)

class DoableViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).doableDao()
    private val entryDao = AppDatabase.getDatabase(application).doableEntryDao()

    val doables: StateFlow<List<Doable>> = dao.getActiveDoables()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val today: Flow<LocalDate> = flow {
        while (true) {
            val now = LocalDateTime.now()
            emit(now.toLocalDate())
            val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay()
            delay(Duration.between(now, nextMidnight).toMillis() + 1)
        }
    }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    val todayDoables: StateFlow<List<TodayDoable>> = today
        .flatMapLatest { date ->
            combine(dao.getActiveDoables(), entryDao.getEntriesForDate(date)) { doables, entries ->
                val doneIds = entries
                    .filter { it.entry.done }
                    .mapTo(mutableSetOf()) { it.entry.doableId }
                doables.map { TodayDoable(it, date, isDone = it.id in doneIds) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setDone(item: TodayDoable, done: Boolean) {
        viewModelScope.launch {
            entryDao.upsertEntry(
                DoableEntry(
                    doableId = item.doable.id,
                    date = item.date,
                    done = done
                )
            )
        }
    }

    fun addDoable(title: String) {
        saveDoable(id = null, title = title, meritLevel = 1, costLevel = 1)
    }

    fun saveDoable(id: Int?, title: String, meritLevel: Int, costLevel: Int) {
        val trimmed = title.trim()
        if (trimmed.isBlank()) return
        viewModelScope.launch {
            if (id == null) {
                dao.insertDoable(Doable(title = trimmed, meritLevel = meritLevel, costLevel = costLevel))
            } else {
                val existing = dao.getDoableById(id) ?: return@launch
                dao.updateDoable(existing.copy(title = trimmed, meritLevel = meritLevel, costLevel = costLevel))
            }
        }
    }

    suspend fun findDoable(id: Int): Doable? = dao.getDoableById(id)

    fun deleteDoable(doable: Doable) {
        viewModelScope.launch {
            dao.removeDoable(doable, LocalDate.now())
        }
    }
}