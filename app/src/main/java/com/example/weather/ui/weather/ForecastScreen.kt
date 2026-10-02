package com.example.weather.ui.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weather.domain.model.DailyForecast
import com.example.weather.domain.model.WeatherReport
import com.example.weather.ui.ErrorView
import com.example.weather.ui.LoadingView
import com.example.weather.ui.UiState
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun ForecastScreen(
    state: UiState<WeatherReport>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        UiState.Loading -> LoadingView(modifier)
        is UiState.Error -> ErrorView(state.message, onRetry = onRefresh, modifier = modifier)
        is UiState.Success -> LazyColumn(
            modifier = modifier,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    "7-day forecast · ${state.data.city.displayName}",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            items(state.data.daily, key = { it.date.toString() }) { day -> DayRow(day) }
        }
    }
}

private val dateFormat = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())

@Composable
private fun DayRow(day: DailyForecast) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.width(96.dp)) {
                Text(
                    day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(day.date.format(dateFormat), style = MaterialTheme.typography.bodySmall)
            }
            Text(day.condition.emoji, style = MaterialTheme.typography.headlineMedium)
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(day.condition.label, style = MaterialTheme.typography.bodyMedium)
                val extras = listOfNotNull(
                    day.precipitationProbabilityPercent?.let { "Rain $it%" },
                    day.maxWindSpeedKmh?.let { "Wind ${it.roundToInt()} km/h" },
                ).joinToString(" · ")
                if (extras.isNotEmpty()) {
                    Text(extras, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(
                "${day.maxTempC.roundToInt()}° / ${day.minTempC.roundToInt()}°",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}
