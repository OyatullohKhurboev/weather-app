package com.example.weather.ui

import retrofit2.HttpException
import java.io.IOException

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

fun Throwable.toUserMessage(): String = when (this) {
    is HttpException -> "Server error (HTTP ${code()}). Please try again later."
    is IOException -> "Can't reach the weather service. Check your internet connection."
    else -> "Something went wrong: ${message ?: javaClass.simpleName}"
}
