package com.smartutility.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LapEntry(
    val number   : Int,
    val lapTime  : Long,   // time for this lap in ms
    val totalTime: Long    // total elapsed time in ms
)

data class StopwatchUiState(
    val elapsedMs  : Long         = 0L,
    val isRunning  : Boolean      = false,
    val laps       : List<LapEntry> = emptyList(),
    val lapStartMs : Long         = 0L
)

class StopwatchViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StopwatchUiState())
    val uiState: StateFlow<StopwatchUiState> = _uiState.asStateFlow()

    private var timerJob      : Job  = Job()
    private var startSystemTime: Long = 0L
    private var accumulatedMs : Long  = 0L

    // ── Controls ──────────────────────────────────────────────────────────────

    fun start() {
        if (_uiState.value.isRunning) return
        startSystemTime = System.currentTimeMillis()
        _uiState.update { it.copy(isRunning = true) }
        timerJob = viewModelScope.launch {
            while (true) {
                delay(10L)
                val elapsed = accumulatedMs +
                        (System.currentTimeMillis() - startSystemTime)
                _uiState.update { it.copy(elapsedMs = elapsed) }
            }
        }
    }

    fun pause() {
        if (!_uiState.value.isRunning) return
        timerJob.cancel()
        accumulatedMs = _uiState.value.elapsedMs
        _uiState.update { it.copy(isRunning = false) }
    }

    fun reset() {
        timerJob.cancel()
        accumulatedMs = 0L
        _uiState.value = StopwatchUiState()
    }

    fun lap() {
        if (!_uiState.value.isRunning) return
        val state      = _uiState.value
        val lapTime    = state.elapsedMs - state.lapStartMs
        val newLap     = LapEntry(
            number    = state.laps.size + 1,
            lapTime   = lapTime,
            totalTime = state.elapsedMs
        )
        _uiState.update {
            it.copy(
                laps      = listOf(newLap) + it.laps, // newest lap on top
                lapStartMs = state.elapsedMs
            )
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    override fun onCleared() {
        super.onCleared()
        timerJob.cancel()
    }
}