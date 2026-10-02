package com.example.weather.domain.model

import java.time.LocalDate

data class City(
    val id: Long,
    val name: String,
    val country: String?,
    val region: String?,
    val latitude: Double,
    val longitude: Double,
) {
    val displayName: String
        get() = listOfNotNull(name, region?.takeIf { it != name }, country).joinToString(", ")
}

data class CurrentWeather(
    val temperatureC: Double,
    val apparentTemperatureC: Double,
    val humidityPercent: Int,
    val windSpeedKmh: Double,
    val windDirectionDeg: Int,
    val condition: WeatherCondition,
)

data class DailyForecast(
    val date: LocalDate,
    val condition: WeatherCondition,
    val maxTempC: Double,
    val minTempC: Double,
    val precipitationProbabilityPercent: Int?,
    val maxWindSpeedKmh: Double?,
)

data class WeatherReport(
    val city: City,
    val current: CurrentWeather,
    val daily: List<DailyForecast>,
)
