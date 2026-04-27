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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import androidx.navigation.compose.rememberNavController
import com.example.arduino_app.model.Screen
import com.example.arduino_app.ui.theme.Arduino_appTheme
import com.example.arduino_app.viewModel.ControlViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Arduino_appTheme {
             //   val navController = rememberNavController()
                val viewModel: ControlViewModel = viewModel()
                Drawer(viewModel = viewModel)
//                Scaffold(
//                    topBar = {
//                        AppTopBar(viewModel)
//                    },
//                    bottomBar = { BottomNavigationBar(navController) }
//                ) { innerPadding ->
//                    MainScreen(
//                        viewModel = viewModel,
//                        navController = navController,
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
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
    NavHost(navController = navController, startDestination = Screen.Office.route) {
        composable(Screen.Office.route) { OfficeScreen(viewModel) }
        composable(Screen.Var.route) { VarScreen(viewModel) }
        composable(Screen.Tracker.route) { TrackerScreen(viewModel) } // It picks up its own ViewModel!
        composable(Screen.Monitor.route) { MonitorScreen(viewModel) }

    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    Arduino_appTheme {
//        HomePage()
//    }
//}