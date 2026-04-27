package com.example.arduino_app.viewModel

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arduino_app.model.BluetoothManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class VarViewModel : ViewModel() {

    private val bluetoothManager = BluetoothManager()

    // In ControlViewModel.kt
    private val _uiMessage = MutableSharedFlow<String?>(
        replay = 1, // This ensures the last message is sent to new listeners
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val Message = _uiMessage.asSharedFlow()
    // UI STATE
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _isAutoMode = MutableStateFlow(false)
    val isAutoMode: StateFlow<Boolean> = _isAutoMode

    private val _isLampOn = MutableStateFlow(false)
    val isLampOn: StateFlow<Boolean> = _isLampOn

    private val _isDoorOpen = MutableStateFlow(false)
    val isDoorOpen: StateFlow<Boolean> = _isDoorOpen

    // 🔌 CONNECT TO HC-05
    fun connectOfficeToHC05(context: Context) {
        // Determine if we are on Android 12 or higher
        val needsNewPermissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

        val hasConnect = if (needsNewPermissions) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else true

        val hasScan = if (needsNewPermissions) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        } else true

        if (!hasConnect || !hasScan) {
            viewModelScope.launch {
                _uiMessage.emit("Missing required permissions")
            }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("BT_LOG", "Starting connection...")
                val adapter = BluetoothAdapter.getDefaultAdapter() ?: run {
                    _uiMessage.emit("Adapter not found")
                    return@launch
                }

                // Add this to check if BT is even turned on
                if (!adapter.isEnabled) {
                    _uiMessage.emit("Please turn on Bluetooth")
                    return@launch
                }

                val device = adapter.getRemoteDevice("00:24:08:00:3F:96")
                Log.d("BT_LOG", "Device found: ${device.name}")

                val success = bluetoothManager.connect(device)
                Log.d("BT_LOG", "Connection result: $success")

                _isConnected.value = success
                _uiMessage.emit(if (success) "Connected!" else "Connection Failed")

            } catch (e: Exception) {
                Log.e("BT_LOG", "Error: ", e)
                _uiMessage.emit("Error: ${e.localizedMessage}")
            }
        }
    }
    fun disconnect() {
        bluetoothManager.disconnect()
        _isConnected.value = false
    }

    // 🔁 AUTO MODE
    fun setAutoMode(enabled: Boolean) {
        _isAutoMode.value = enabled
        sendCommand(
            if (enabled) "AUTO_ON" else "AUTO_OFF"
        )
    }

    // 💡 LAMP CONTROL
    fun setLamp(on: Boolean) {
        if (_isAutoMode.value) return // prevent manual override
        _isLampOn.value = on
        sendCommand(
            if (on) "l" else "f "
        )
    }

    fun ToggleDoor(open: Boolean) {
        if (_isAutoMode.value) return // prevent manual override
        _isDoorOpen.value = open
        sendCommand(
            if (open) "o" else "c"
        )
    }

    // 🔄 SEND TO ARDUINO
     fun sendCommand(command: String) {
        viewModelScope.launch(Dispatchers.IO) {
            bluetoothManager.sendCommand(command)
        }
    }
}