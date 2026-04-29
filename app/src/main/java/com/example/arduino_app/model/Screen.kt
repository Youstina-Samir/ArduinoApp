package com.example.arduino_app.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import com.example.arduino_app.R

sealed class Screen(val route: String, val label: String, val icon: Any) {
    object Office : Screen("Office", "Office", Icons.Default.Home)
    object Var : Screen("VAR", "VAR", icon= R.drawable.cap)
    object Tracker : Screen("Tracker", "Tracker", Icons.Default.WbSunny)
    object Terminal : Screen("Terminal", "Terminal", Icons.Default.ScreenShare)

}

val items = listOf(Screen.Office, Screen.Var, Screen.Tracker, Screen.Terminal)