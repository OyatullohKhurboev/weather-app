package com.example.weather.domain.model

/** Human-readable condition derived from a WMO weather interpretation code. */
enum class WeatherCondition(val label: String, val emoji: String) {
    CLEAR("Clear sky", "☀️"),
    MAINLY_CLEAR("Mainly clear", "🌤️"),
    PARTLY_CLOUDY("Partly cloudy", "⛅"),
    OVERCAST("Overcast", "☁️"),
    FOG("Fog", "🌫️"),
    DRIZZLE("Drizzle", "🌦️"),
    RAIN("Rain", "🌧️"),
    FREEZING_RAIN("Freezing rain", "🌧️"),
    SNOW("Snow", "🌨️"),
    SNOW_GRAINS("Snow grains", "🌨️"),
    SHOWERS("Rain showers", "🌦️"),
    SNOW_SHOWERS("Snow showers", "🌨️"),
    THUNDERSTORM("Thunderstorm", "⛈️"),
    UNKNOWN("Unknown", "❓");

    companion object {
        fun fromWmoCode(code: Int): WeatherCondition = when (code) {
            0 -> CLEAR
            1 -> MAINLY_CLEAR
            2 -> PARTLY_CLOUDY
            3 -> OVERCAST
            45, 48 -> FOG
            51, 53, 55 -> DRIZZLE
            56, 57, 66, 67 -> FREEZING_RAIN
            61, 63, 65 -> RAIN
            71, 73, 75 -> SNOW
            77 -> SNOW_GRAINS
            80, 81, 82 -> SHOWERS
            85, 86 -> SNOW_SHOWERS
            95, 96, 99 -> THUNDERSTORM
            else -> UNKNOWN
        }
    }
}
