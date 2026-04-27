package com.example.arduino_app.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.arduino_app.model.Screen
import com.example.arduino_app.viewModel.ControlViewModel
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Drawer(viewModel: ControlViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val isConnected by viewModel.isConnected.collectAsState()
    val connectedName by viewModel.connectedDeviceName.collectAsState() // Observe here
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 1. Define the MACs and names
    val deviceList = listOf(
        "Office" to "00:24:08:00:3F:96",
        "Var" to "00:24:08:00:AA:11",
        "Tracker" to "00:24:08:00:BB:22"
    )

    // 2. State to "remember" which device we want to connect to after permission is granted
    var pendingDevice by remember { mutableStateOf<Pair<String, String>?>(null) }

    // 3. Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val connectGranted = permissions[Manifest.permission.BLUETOOTH_CONNECT] ?: false
        val scanGranted = permissions[Manifest.permission.BLUETOOTH_SCAN] ?: false

        if (connectGranted && scanGranted) {
            pendingDevice?.let { (name, mac) ->
                viewModel.connectToDevice(context, mac, name)
            }
        } else {
            Toast.makeText(context, "Bluetooth permissions are required", Toast.LENGTH_LONG).show()
        }
    }

    // 4. The Click Handler logic (moved inside a lambda for the onClick)
    val onConnectClick: (String, String) -> Unit = { name, mac ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val hasConnect = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
            val hasScan = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED

            if (hasConnect && hasScan) {
                viewModel.connectToDevice(context, mac, name)
            } else {
                pendingDevice = name to mac
                permissionLauncher.launch(
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
                )
            }
        } else {
            viewModel.connectToDevice(context, mac, name)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text("CONTROL HUB", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text("CONNECT TO DEVICE", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.labelLarge)

                deviceList.forEach { (name, mac) ->
                    NavigationDrawerItem(
                        label = { Text("Connect $name") },
                        selected = false,
                        icon = { Icon(Icons.Default.Refresh, null) },
                        onClick = {
                            onConnectClick(name, mac) // Use the handler we defined
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                NavigationDrawerItem(
                    label = { Text("Disconnect Device", color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    icon = { Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    onClick = {
                        viewModel.disconnect()
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ARDUINO APP", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = if (isConnected) "Connected:$connectedName" else "Disconnected",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isConnected) Color.Green else Color.Red
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Open Slider")
                        }
                    }
                )
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) { innerPadding ->
            MainScreen(
                viewModel = viewModel,
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}