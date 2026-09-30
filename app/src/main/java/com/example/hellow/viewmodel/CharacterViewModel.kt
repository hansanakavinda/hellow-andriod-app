package com.example.hellow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hellow.model.Result
import com.example.hellow.model.RickCharacter
import com.example.hellow.network.RickAndMortyRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class CharacterViewModel : ViewModel() {

    private val repository = RickAndMortyRepository()

    // 1. User search query input stream
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // 2. UI State Flow using flatMapLatest and debounce for live searching
    private val _uiState = MutableStateFlow<Result<List<RickCharacter>>>(Result.Loading)
    val uiState: StateFlow<Result<List<RickCharacter>>> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300L.milliseconds) // Wait 300ms after user stops typing
                .distinctUntilChanged() // Skip if query hasn't changed
                .flatMapLatest { query ->
                    flow {
                        emit(Result.Loading)
                        try {
                            val characters = repository.searchCharacters(query)
                            emit(Result.Success(characters))
                        } catch (e: Exception) {
                            emit(Result.Error(e))
                        }
                    }
                }
                .collect { result ->
                    _uiState.value = result
                }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }
}
