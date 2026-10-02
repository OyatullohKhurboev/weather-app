package com.example.weather.data.repository

import com.example.weather.data.local.CityStore
import com.example.weather.data.remote.ForecastApi
import com.example.weather.data.remote.GeocodingApi
import com.example.weather.domain.model.City
import com.example.weather.domain.model.WeatherReport
import com.example.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WeatherRepositoryImpl(
    private val geocodingApi: GeocodingApi,
    private val forecastApi: ForecastApi,
    private val cityStore: CityStore,
) : WeatherRepository {

    private val _selectedCity = MutableStateFlow(cityStore.load())
    override val selectedCity: StateFlow<City> = _selectedCity.asStateFlow()

    override fun selectCity(city: City) {
        cityStore.save(city)
        _selectedCity.value = city
    }

    override suspend fun searchCities(query: String): Result<List<City>> = runCatchingSafe {
        geocodingApi.searchCities(query).results.orEmpty().map { it.toDomain() }
    }

    override suspend fun getWeather(city: City): Result<WeatherReport> = runCatchingSafe {
        forecastApi.getForecast(city.latitude, city.longitude).toDomain(city)
    }

    /** Like runCatching, but never swallows coroutine cancellation. */
    private inline fun <T> runCatchingSafe(block: () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}
