package com.example.weather

import com.example.weather.data.remote.dto.ForecastResponseDto
import com.example.weather.data.remote.dto.GeocodingResponseDto
import com.example.weather.data.repository.toDomain
import com.example.weather.domain.model.City
import com.example.weather.domain.model.WeatherCondition
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MapperAndConditionTest {
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    @Test
    fun wmoCodesMapToConditions() {
        assertEquals(WeatherCondition.CLEAR, WeatherCondition.fromWmoCode(0))
        assertEquals(WeatherCondition.RAIN, WeatherCondition.fromWmoCode(63))
        assertEquals(WeatherCondition.THUNDERSTORM, WeatherCondition.fromWmoCode(95))
        assertEquals(WeatherCondition.UNKNOWN, WeatherCondition.fromWmoCode(1234))
    }

    @Test
    fun parsesForecastJsonAndMaps() {
        val raw = """
        {"latitude":52.52,"timezone":"Europe/Berlin",
         "current":{"time":"2026-10-02T20:45","interval":900,"temperature_2m":16.1,"relative_humidity_2m":84,
           "apparent_temperature":16.8,"weather_code":3,"wind_speed_10m":3.3,"wind_direction_10m":13},
         "daily":{"time":["2026-10-02","2026-10-03"],"weather_code":[61,3],
           "temperature_2m_max":[17.0,19.6],"temperature_2m_min":[12.3,13.6],
           "precipitation_probability_max":[30,null],"wind_speed_10m_max":[10.0,8.1]}}
        """.trimIndent()
        val city = City(1, "Berlin", "Germany", null, 52.52, 13.41)
        val report = json.decodeFromString<ForecastResponseDto>(raw).toDomain(city)
        assertEquals(16.1, report.current.temperatureC, 0.0)
        assertEquals(84, report.current.humidityPercent)
        assertEquals(WeatherCondition.OVERCAST, report.current.condition)
        assertEquals(2, report.daily.size)
        assertEquals(WeatherCondition.RAIN, report.daily[0].condition)
        assertEquals(30, report.daily[0].precipitationProbabilityPercent)
        assertNull(report.daily[1].precipitationProbabilityPercent)
    }

    @Test
    fun parsesGeocodingJson() {
        val raw = """{"results":[{"id":2950159,"name":"Berlin","latitude":52.52437,"longitude":13.41053,
            "country":"Germany","admin1":"State of Berlin","population":3426354}]}"""
        val cities = json.decodeFromString<GeocodingResponseDto>(raw).results.orEmpty().map { it.toDomain() }
        assertEquals("Berlin, State of Berlin, Germany", cities.single().displayName)
        assertEquals(0, json.decodeFromString<GeocodingResponseDto>("{}").results.orEmpty().size)
    }
}
