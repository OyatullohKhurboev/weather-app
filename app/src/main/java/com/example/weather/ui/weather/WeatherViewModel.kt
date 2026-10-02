package com.example.weather.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.weather.domain.model.City
import com.example.weather.domain.model.WeatherReport
import com.example.weather.domain.repository.WeatherRepository
import com.example.weather.domain.usecase.GetWeatherUseCase
import com.example.weather.ui.UiState
import com.example.weather.ui.toUserMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val getWeather: GetWeatherUseCase = GetWeatherUseCase(repository),
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<WeatherReport>>(UiState.Loading)
    val state: StateFlow<UiState<WeatherReport>> = _state.asStateFlow()

    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            repository.selectedCity.collect { city -> load(city) }
        }
    }

    fun refresh() = load(repository.selectedCity.value)

    private fun load(city: City) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = UiState.Loading
            getWeather(city).fold(
                onSuccess = { _state.value = UiState.Success(it) },
                onFailure = { _state.value = UiState.Error(it.toUserMessage()) },
            )
        }
    }

    companion object {
        fun factory(repository: WeatherRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { WeatherViewModel(repository) }
        }
    }
}
