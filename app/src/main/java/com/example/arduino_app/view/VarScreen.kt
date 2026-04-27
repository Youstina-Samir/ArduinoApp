package com.example.arduino_app.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction // Use ONLY this one
import androidx.compose.ui.unit.dp
import com.example.arduino_app.viewModel.OfficeViewModel
import com.example.arduino_app.viewModel.VarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VarScreen (viewModel: VarViewModel) {
    val arduinoLogs by viewModel.receivedData.collectAsState() // From our previous "Read" logic
    var targetInput by remember { mutableStateOf("0.95") }
    var currentPFInput by remember { mutableStateOf("") }

    // Parsing the Arduino response "RESULT:12.5:3"
    val dataParts = arduinoLogs.split(":").filter { it.isNotBlank() }
    val qRequired = if (dataParts.size >= 3 && dataParts[0].contains("RESULT")) dataParts[1] else "--"
    val stepsActive = if (dataParts.size >= 3 && dataParts[0].contains("RESULT")) dataParts[2] else "0"

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Power Factor Controller", style = MaterialTheme.typography.headlineMedium)

        // 1. Data Display Cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoCard("Q Required", "$qRequired kVar", Modifier.weight(1f))
            InfoCard("Steps Active", stepsActive, Modifier.weight(1f))
        }

        // 2. Target PF Input
        OutlinedTextField(
            value = targetInput,
            onValueChange = { targetInput = it },
            label = { Text("Set Target Power Factor") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                viewModel.sendCommand("T$targetInput") // Send with 'T' prefix
            })
        )

        // 3. Current PF Input (Simulating the sensor)
        OutlinedTextField(
            value = currentPFInput,
            onValueChange = { currentPFInput = it },
            label = { Text("Enter Current PF (from sensor)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                viewModel.sendCommand(currentPFInput)
                currentPFInput = ""
            })
        )

        Button(
            onClick = { viewModel.sendCommand("T$targetInput") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Target PF")
        }
    }
}

@Composable
fun InfoCard(title: String, value: String, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}