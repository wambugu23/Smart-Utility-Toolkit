package com.smartutility.ui

sealed class Screen(val route: String) {
    object Home      : Screen("home")
    object Converter : Screen("converter")
    object Currency  : Screen("currency")
    object Stopwatch : Screen("stopwatch")
    object Tasks     : Screen("tasks")
}
