package com.example.weather

import android.app.Application
import com.example.weather.data.local.CityStore
import com.example.weather.data.remote.NetworkModule
import com.example.weather.data.repository.WeatherRepositoryImpl
import com.example.weather.domain.repository.WeatherRepository

/** Minimal manual dependency container. */
class WeatherApplication : Application() {
    lateinit var repository: WeatherRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = WeatherRepositoryImpl(
            geocodingApi = NetworkModule.geocodingApi,
            forecastApi = NetworkModule.forecastApi,
            cityStore = CityStore(this),
        )
    }
}
