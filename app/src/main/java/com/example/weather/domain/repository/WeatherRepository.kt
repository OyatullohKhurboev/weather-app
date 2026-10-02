package com.example.weather.domain.repository

import com.example.weather.domain.model.City
import com.example.weather.domain.model.WeatherReport
import kotlinx.coroutines.flow.StateFlow

interface WeatherRepository {
    /** Currently selected city (persisted across launches). */
    val selectedCity: StateFlow<City>

    fun selectCity(city: City)

    suspend fun searchCities(query: String): Result<List<City>>

    suspend fun getWeather(city: City): Result<WeatherReport>
}
