package com.example.arduino_app.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.arduino_app.viewModel.ControlViewModel

@Composable
fun OfficeScreen(viewModel: ControlViewModel) {

    val isAutoMode by viewModel.isAutoMode.collectAsState()
    val isLampOn by viewModel.isLampOn.collectAsState()
    val isDoorOpen by viewModel.isDoorOpen.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Office Control",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 🔁 Automatic Mode Switch
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Automatic Mode",
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = isAutoMode,
                onCheckedChange = {
                    viewModel.setAutoMode(it)
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 💡 Lamp Control (Manual)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Lamp",
                modifier = Modifier.weight(1f),
                color = if (isAutoMode) Color.Gray else Color.Unspecified
            )

            Switch(
                checked = isLampOn,
                onCheckedChange = {
                   viewModel.setLamp(it)
                },
               enabled = !isAutoMode // 🔥 THIS is the key
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Door",
                modifier = Modifier.weight(1f),
                color = if (isAutoMode) Color.Gray else Color.Unspecified
            )

            Switch(
                checked = isDoorOpen,
                onCheckedChange = {
                    viewModel.ToggleDoor(it)
                },
                 enabled = !isAutoMode // 🔥 THIS is the key
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isAutoMode)
                "Lamp is controlled automatically"
            else
                "Manual control enabled",
            color = Color.Gray
        )
    }
}