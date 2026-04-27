package com.example.arduino_app.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction // Use ONLY this one
import androidx.compose.ui.unit.dp
import com.example.arduino_app.viewModel.ControlViewModel
import kotlin.math.acos
import kotlin.math.tan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VarScreen(viewModel: ControlViewModel) {
    val qValue by viewModel.qRequired.collectAsState()
    val stepsCount by viewModel.activeSteps.collectAsState()
    var currentPFInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center.run {
            Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
        }    ) {
        Text(
            text = "Power Factor Controller",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Target PF Badge (Fixed)
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Target PF: 0.95",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        // Calculation Results
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(
                title = "Required Q",
                value = "${String.format("%.1f", qValue)} kVar",
                modifier = Modifier.weight(1f)
            )
            InfoCard(
                title = "Active Steps",
                value =" ${stepsCount.toString()}+Fixed",
                modifier = Modifier.weight(1f)
            )
        }


        // Input Field
        OutlinedTextField(
            value = currentPFInput,
            onValueChange = { currentPFInput = it },
            label = { Text("Enter Measured Power Factor") },
            placeholder = { Text("e.g. 0.82") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal,
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(onSend = {
                val pf = currentPFInput.toFloatOrNull() ?: 0f
                viewModel.processPFC(pf)
                currentPFInput = ""
            })
        )

        Button(
            onClick = {
                val pf = currentPFInput.toFloatOrNull() ?: 0f
                viewModel.processPFC(pf)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Send, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Update Hardware")
        }
    }
}
@Composable
fun InfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}