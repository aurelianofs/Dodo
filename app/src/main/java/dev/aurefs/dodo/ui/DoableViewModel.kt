package dev.aurefs.dodo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.aurefs.dodo.data.AppDatabase
import dev.aurefs.dodo.data.Doable
import dev.aurefs.dodo.data.DoableEntry
import dev.aurefs.dodo.data.EntryStatus
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

    val doables: StateFlow<List<Doable>> = dao.getAllDoables()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Emits the current date, and again right after each midnight
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
            combine(dao.getAllDoables(), entryDao.getEntriesForDate(date)) { doables, entries ->
                val doneIds = entries
                    .filter { it.status == EntryStatus.DONE }
                    .mapTo(mutableSetOf()) { it.doableId }
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
                    status = if (done) EntryStatus.DONE else EntryStatus.PENDING
                )
            )
        }
    }

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