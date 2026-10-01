package com.example.hellow.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hellow.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UserPreferenceState {
    data object Loading : UserPreferenceState
    data class Ready(val name: String?) : UserPreferenceState
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserPreferencesRepository(application)

    val userState: StateFlow<UserPreferenceState> = repository.userNameFlow
        .map { name -> UserPreferenceState.Ready(name) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferenceState.Loading
        )

    fun saveUserName(name: String, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveUserName(name)
            onSaved()
        }
    }
}
