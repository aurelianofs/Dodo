package dev.aurefs.dodo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.aurefs.dodo.data.AppDatabase
import dev.aurefs.dodo.data.Doable
import dev.aurefs.dodo.data.EntryWithDoable
import dev.aurefs.dodo.domain.Scoring
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class DoableViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).doableDao()
    private val entryDao = AppDatabase.getDatabase(application).doableEntryDao()

    val doables: StateFlow<List<Doable>> = dao.getActiveDoables()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val today = MutableStateFlow(LocalDate.now())

    init {
        viewModelScope.launch {
            today.collect { date -> entryDao.ensureDaysUpTo(date) }
        }
        viewModelScope.launch {
            while (true) {
                val now = LocalDateTime.now()
                val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay()
                delay(Duration.between(now, nextMidnight).toMillis() + 1)
                refreshDate()
            }
        }
    }

    val currentDate: StateFlow<LocalDate> = today.asStateFlow()

    fun refreshDate() {
        today.value = LocalDate.now()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val todayEntries: StateFlow<List<EntryWithDoable>> = today
        .flatMapLatest { date -> entryDao.getEntriesForDate(date) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun entriesFor(date: LocalDate): Flow<List<EntryWithDoable>> = entryDao.getEntriesForDate(date)

    fun scoresFor(month: YearMonth): Flow<Map<LocalDate, Int>> =
        entryDao.getEntriesBetween(month.atDay(1), month.atEndOfMonth()).map(Scoring::dayScores)

    fun setDone(item: EntryWithDoable, done: Boolean) {
        viewModelScope.launch {
            entryDao.setDone(item.entry.doableId, item.entry.date, done)
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
                dao.insertDoableForDay(Doable(title = trimmed, meritLevel = meritLevel, costLevel = costLevel), today.value)
            } else {
                val existing = dao.getDoableById(id) ?: return@launch
                dao.updateDoable(existing.copy(title = trimmed, meritLevel = meritLevel, costLevel = costLevel))
            }
        }
    }

    suspend fun findDoable(id: Int): Doable? = dao.getDoableById(id)

    fun deleteDoable(doable: Doable) {
        viewModelScope.launch {
            dao.removeDoable(doable, today.value)
        }
    }
}