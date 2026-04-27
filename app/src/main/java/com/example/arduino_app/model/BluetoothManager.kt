package com.example.arduino_app.model

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class BluetoothManager {

    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    private val hc05UUID: UUID =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    fun connect(device: BluetoothDevice): Boolean {
        return try {

            socket = device.createRfcommSocketToServiceRecord(hc05UUID)

            socket?.connect()

            outputStream = socket?.outputStream

            true

        } catch (e: Exception) {
            e.printStackTrace() // 🔥 look at Logcat
            false
        }
    }

    fun sendCommand(command: String) {
        try {
            outputStream?.write((command + "\n").toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        socket?.close()
        socket = null
    }
}