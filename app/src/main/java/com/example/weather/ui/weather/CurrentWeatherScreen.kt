package com.example.weather.ui.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.weather.domain.model.CurrentWeather
import com.example.weather.domain.model.WeatherReport
import com.example.weather.ui.ErrorView
import com.example.weather.ui.LoadingView
import com.example.weather.ui.UiState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentWeatherScreen(
    state: UiState<WeatherReport>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        UiState.Loading -> LoadingView(modifier)
        is UiState.Error -> ErrorView(state.message, onRetry = onRefresh, modifier = modifier)
        is UiState.Success -> PullToRefreshBox(
            isRefreshing = false,
            onRefresh = onRefresh,
            modifier = modifier.fillMaxSize(),
        ) {
            CurrentWeatherContent(state.data)
        }
    }
}

@Composable
private fun CurrentWeatherContent(report: WeatherReport) {
    val c = report.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(report.city.displayName, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        Text(c.condition.emoji, style = MaterialTheme.typography.displayLarge)
        Text("${c.temperatureC.roundToInt()}°C", style = MaterialTheme.typography.displayLarge)
        Text(c.condition.label, style = MaterialTheme.typography.titleLarge)
        Text(
            "Feels like ${c.apparentTemperatureC.roundToInt()}°C",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        DetailsCard(c)
    }
}

@Composable
private fun DetailsCard(c: CurrentWeather) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Detail("Wind", "${c.windSpeedKmh.roundToInt()} km/h ${compass(c.windDirectionDeg)}")
            Detail("Humidity", "${c.humidityPercent}%")
        }
    }
}

@Composable
private fun Detail(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}

internal fun compass(degrees: Int): String {
    val points = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    val idx = (((degrees % 360) + 360) % 360 / 45.0).roundToInt() % 8
    return points[idx]
}
