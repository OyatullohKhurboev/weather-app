package com.example.weather.data.remote

import com.example.weather.data.remote.dto.ForecastResponseDto
import com.example.weather.data.remote.dto.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/** https://geocoding-api.open-meteo.com/ */
interface GeocodingApi {
    @GET("v1/search")
    suspend fun searchCities(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json",
    ): GeocodingResponseDto
}

/** https://api.open-meteo.com/ */
interface ForecastApi {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = CURRENT_FIELDS,
        @Query("daily") daily: String = DAILY_FIELDS,
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7,
    ): ForecastResponseDto

    companion object {
        const val CURRENT_FIELDS =
            "temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m,wind_direction_10m"
        const val DAILY_FIELDS =
            "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,wind_speed_10m_max"
    }
}
