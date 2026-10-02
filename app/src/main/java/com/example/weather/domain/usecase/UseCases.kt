package com.example.weather.domain.usecase

import com.example.weather.domain.model.City
import com.example.weather.domain.model.WeatherReport
import com.example.weather.domain.repository.WeatherRepository

class GetWeatherUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(city: City): Result<WeatherReport> = repository.getWeather(city)
}

class SearchCitiesUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(query: String): Result<List<City>> {
        val trimmed = query.trim()
        if (trimmed.length < 2) return Result.success(emptyList())
        return repository.searchCities(trimmed)
    }
}
