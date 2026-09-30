package com.example.sandbox.day1

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.delay

/**
 * Module 3: Flow Deep Dive Practice
 * Topics: Cold vs Hot flows, StateFlow, SharedFlow, and Operators (combine, flatMapLatest, debounce, flowOn).
 */
class FlowPractice {

    /**
     * 1. Cold Flow:
     * Does nothing until someone collects it. Each collector triggers the builder block from scratch.
     */
    fun createColdFlow(): Flow<Int> = flow {
        emit(1)
        delay(10)
        emit(2)
        delay(10)
        emit(3)
    }

    /**
     * 2. StateFlow (Hot Flow):
     * Always holds a single latest value. Replays the latest value to new subscribers.
     * Perfect for UI state management (following Now in Android architecture).
     */
    private val _uiState = MutableStateFlow("Loading")
    val uiState: StateFlow<String> = _uiState.asStateFlow()

    fun updateState(newState: String) {
        _uiState.value = newState
    }

    /**
     * 3. SharedFlow (Hot Event Stream):
     * Used for one-off events like navigation or showing a Snackbar.
     */
    private val _events = MutableSharedFlow<String>()
    val events: SharedFlow<String> = _events.asSharedFlow()

    suspend fun emitEvent(event: String) {
        _events.emit(event)
    }

    /**
     * 4. Operators Example (combine):
     * Combines two flows together whenever either emits a new value.
     */
    fun combineFlows(flow1: Flow<String>, flow2: Flow<Int>): Flow<String> {
        return flow1.combine(flow2) { text, number ->
            "$text: $number"
        }
    }
}
