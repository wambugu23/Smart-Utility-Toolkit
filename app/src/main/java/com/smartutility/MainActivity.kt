package com.smartutility

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartutility.ui.Screen
import com.smartutility.ui.converter.ConverterScreen
import com.smartutility.ui.currency.CurrencyScreen
import com.smartutility.ui.home.HomeScreen
import com.smartutility.ui.stopwatch.StopwatchScreen
import com.smartutility.ui.tasks.TaskScreen
import com.smartutility.ui.theme.SmartUtilityTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartUtilityTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController    = navController,
                        startDestination = Screen.Home.route
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(navController = navController)
                        }
                        composable(Screen.Converter.route) {
                            ConverterScreen(navController = navController)
                        }
                        composable(Screen.Currency.route) {
                            CurrencyScreen(navController = navController)
                        }
                        composable(Screen.Stopwatch.route) {
                            StopwatchScreen(navController = navController)
                        }
                        composable(Screen.Tasks.route) {
                            TaskScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}