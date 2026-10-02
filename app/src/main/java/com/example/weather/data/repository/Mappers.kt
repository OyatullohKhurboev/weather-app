package com.example.weather.data.repository

import com.example.weather.data.remote.dto.ForecastResponseDto
import com.example.weather.data.remote.dto.GeocodingResultDto
import com.example.weather.domain.model.City
import com.example.weather.domain.model.CurrentWeather
import com.example.weather.domain.model.DailyForecast
import com.example.weather.domain.model.WeatherCondition
import com.example.weather.domain.model.WeatherReport
import java.time.LocalDate

fun GeocodingResultDto.toDomain() = City(
    id = id,
    name = name,
    country = country,
    region = admin1,
    latitude = latitude,
    longitude = longitude,
)

fun ForecastResponseDto.toDomain(city: City): WeatherReport {
    val d = daily
    val days = d.time.indices.mapNotNull { i ->
        val max = d.temperatureMax.getOrNull(i) ?: return@mapNotNull null
        val min = d.temperatureMin.getOrNull(i) ?: return@mapNotNull null
        DailyForecast(
            date = LocalDate.parse(d.time[i]),
            condition = WeatherCondition.fromWmoCode(d.weatherCode.getOrElse(i) { -1 }),
            maxTempC = max,
            minTempC = min,
            precipitationProbabilityPercent = d.precipitationProbabilityMax.getOrNull(i),
            maxWindSpeedKmh = d.windSpeedMax.getOrNull(i),
        )
    }
    return WeatherReport(
        city = city,
        current = CurrentWeather(
            temperatureC = current.temperature,
            apparentTemperatureC = current.apparentTemperature,
            humidityPercent = current.humidity,
            windSpeedKmh = current.windSpeed,
            windDirectionDeg = current.windDirection,
            condition = WeatherCondition.fromWmoCode(current.weatherCode),
        ),
        daily = days,
    )
}
