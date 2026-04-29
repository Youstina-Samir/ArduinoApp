package com.example.arduino_app.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.arduino_app.model.Screen
import com.example.arduino_app.viewModel.ControlViewModel
import com.example.compose.darkScheme
import com.example.compose.lightScheme
import com.example.ui.theme.AppTypography

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ControlViewModel = viewModel()
            val isDark by viewModel.isDarkMode.collectAsState()
            Arduino_appThemeNew(darkTheme = isDark) {             //   val navController = rememberNavController()
                Drawer(viewModel = viewModel)

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(viewModel: ControlViewModel) {
    val context = LocalContext.current
    val isConnected by viewModel.isConnected.collectAsState()

    // Launcher for both required permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val connectGranted = permissions[Manifest.permission.BLUETOOTH_CONNECT] ?: false
        val scanGranted = permissions[Manifest.permission.BLUETOOTH_SCAN] ?: false

        if (connectGranted && scanGranted) {
            viewModel.connectOfficeToHC05(context)
        } else {
            Toast.makeText(context, "Both permissions are required on Android 12+", Toast.LENGTH_LONG).show()
        }
    }

    TopAppBar(
        title = { Text(text = if (isConnected) "Connected" else "Disconnected") },
        actions = {
            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val hasConnect = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                        val hasScan = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED

                        if (hasConnect && hasScan) {
                            viewModel.connectOfficeToHC05(context)
                        } else {
                            // Request both at once
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.BLUETOOTH_CONNECT,
                                    Manifest.permission.BLUETOOTH_SCAN
                                )
                            )
                        }
                    } else {
                        // For Android 11 and below, manifest permissions are enough
                        viewModel.connectOfficeToHC05(context)
                    }
                }
            ) {
                Text("Connect")
            }
        }
    )
}
@Composable
fun MainScreen(viewModel: ControlViewModel, navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController,
        startDestination = Screen.Office.route,
        enterTransition = {
            fadeIn(animationSpec = tween(100))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(100))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(100))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(100))
        }) {
        composable(Screen.Office.route) { OfficeScreen(viewModel) }
        composable(Screen.Var.route) { VarScreen(viewModel) }
        composable(Screen.Tracker.route) { TrackerScreen(viewModel) } // It picks up its own ViewModel!
        composable(Screen.Terminal.route) { MonitorScreen(viewModel) }

    }
}

@Composable
fun Arduino_appThemeNew(
    darkTheme: Boolean = isSystemInDarkTheme(), // Default to system setting
    content: @Composable () -> Unit
) {
    // Choose colors based on the boolean
    val colorScheme = if (darkTheme) {
        darkScheme // Your professional Dark Colors
    } else {
        lightScheme // Your professional Light Colors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, // This is where your Montserrat lives
        content = content
    )
}

