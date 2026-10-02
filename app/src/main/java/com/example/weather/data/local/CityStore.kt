package com.example.weather.data.local

import android.content.Context
import com.example.weather.domain.model.City

/** Persists the last selected city in SharedPreferences. */
class CityStore(context: Context) {
    private val prefs = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)

    fun load(): City {
        if (!prefs.contains(KEY_NAME)) return DEFAULT_CITY
        return City(
            id = prefs.getLong(KEY_ID, DEFAULT_CITY.id),
            name = prefs.getString(KEY_NAME, DEFAULT_CITY.name) ?: DEFAULT_CITY.name,
            country = prefs.getString(KEY_COUNTRY, null),
            region = prefs.getString(KEY_REGION, null),
            latitude = prefs.getFloat(KEY_LAT, DEFAULT_CITY.latitude.toFloat()).toDouble(),
            longitude = prefs.getFloat(KEY_LON, DEFAULT_CITY.longitude.toFloat()).toDouble(),
        )
    }

    fun save(city: City) {
        prefs.edit()
            .putLong(KEY_ID, city.id)
            .putString(KEY_NAME, city.name)
            .putString(KEY_COUNTRY, city.country)
            .putString(KEY_REGION, city.region)
            .putFloat(KEY_LAT, city.latitude.toFloat())
            .putFloat(KEY_LON, city.longitude.toFloat())
            .apply()
    }

    companion object {
        val DEFAULT_CITY = City(
            id = 524901,
            name = "Moscow",
            country = "Russia",
            region = "Moscow",
            latitude = 55.75222,
            longitude = 37.61556,
        )
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_COUNTRY = "country"
        private const val KEY_REGION = "region"
        private const val KEY_LAT = "lat"
        private const val KEY_LON = "lon"
    }
}
