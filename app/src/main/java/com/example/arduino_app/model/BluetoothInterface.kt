package com.example.arduino_app.model

import kotlinx.coroutines.flow.StateFlow

interface BluetoothInterface {
    val isConnected: StateFlow<Boolean>
    //val receivedData: StateFlow<String>
    fun sendCommand(command: String)
}