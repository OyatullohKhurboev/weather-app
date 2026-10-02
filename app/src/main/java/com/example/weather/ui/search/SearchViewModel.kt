package com.example.weather.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.weather.domain.model.City
import com.example.weather.domain.repository.WeatherRepository
import com.example.weather.domain.usecase.SearchCitiesUseCase
import com.example.weather.ui.toUserMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<City> = emptyList(),
    val error: String? = null,
    /** True once a search finished for the current query (used to show "no results"). */
    val searched: Boolean = false,
)

class SearchViewModel(
    private val repository: WeatherRepository,
    private val searchCities: SearchCitiesUseCase = SearchCitiesUseCase(repository),
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query, error = null) }
        searchJob?.cancel()
        if (query.trim().length < 2) {
            _state.update { it.copy(isLoading = false, results = emptyList(), searched = false) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_MS)
            runSearch(query)
        }
    }

    fun searchNow() {
        searchJob?.cancel()
        val query = _state.value.query
        searchJob = viewModelScope.launch { runSearch(query) }
    }

    private suspend fun runSearch(query: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        searchCities(query).fold(
            onSuccess = { list ->
                _state.update { it.copy(isLoading = false, results = list, searched = true) }
            },
            onFailure = { e ->
                _state.update { it.copy(isLoading = false, results = emptyList(), error = e.toUserMessage(), searched = false) }
            },
        )
    }

    fun onCitySelected(city: City) = repository.selectCity(city)

    companion object {
        private const val DEBOUNCE_MS = 400L

        fun factory(repository: WeatherRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { SearchViewModel(repository) }
        }
    }
}
