package com.example.weather.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weather.R
import com.example.weather.WeatherApplication
import com.example.weather.ui.search.SearchScreen
import com.example.weather.ui.search.SearchViewModel
import com.example.weather.ui.weather.CurrentWeatherScreen
import com.example.weather.ui.weather.ForecastScreen
import com.example.weather.ui.weather.WeatherViewModel

private enum class Destination(val route: String, val label: String, val icon: String) {
    TODAY("today", "Today", "☀️"),
    FORECAST("forecast", "7 days", "📅"),
    SEARCH("search", "Search", "🔍"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherApp() {
    val app = LocalContext.current.applicationContext as WeatherApplication
    val repository = app.repository
    val navController = rememberNavController()

    val weatherViewModel: WeatherViewModel = viewModel(factory = WeatherViewModel.factory(repository))
    val searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.factory(repository))

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(stringResource(R.string.app_name)) }) },
        bottomBar = {
            NavigationBar {
                Destination.entries.forEach { dest ->
                    NavigationBarItem(
                        selected = currentRoute == dest.route,
                        onClick = {
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(dest.icon) },
                        label = { Text(dest.label) },
                    )
                }
            }
        },
    ) { padding ->
        val weatherState by weatherViewModel.state.collectAsStateWithLifecycle()
        val searchState by searchViewModel.state.collectAsStateWithLifecycle()
        val contentModifier = Modifier.padding(padding)

        NavHost(navController = navController, startDestination = Destination.TODAY.route) {
            composable(Destination.TODAY.route) {
                CurrentWeatherScreen(weatherState, onRefresh = weatherViewModel::refresh, modifier = contentModifier)
            }
            composable(Destination.FORECAST.route) {
                ForecastScreen(weatherState, onRefresh = weatherViewModel::refresh, modifier = contentModifier)
            }
            composable(Destination.SEARCH.route) {
                SearchScreen(
                    state = searchState,
                    onQueryChange = searchViewModel::onQueryChange,
                    onSearch = searchViewModel::searchNow,
                    onCityClick = { city ->
                        searchViewModel.onCitySelected(city)
                        navController.navigate(Destination.TODAY.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = contentModifier,
                )
            }
        }
    }
}
