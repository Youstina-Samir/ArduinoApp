package com.example.arduino_app.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction // Use ONLY this one
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arduino_app.viewModel.OfficeViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitorScreen(viewModel: OfficeViewModel) {
    var terminalHistory by remember { mutableStateOf("Terminal Ready...\n") }
    var commandInput by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    LaunchedEffect(terminalHistory) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SERIAL MONITOR", style = MaterialTheme.typography.titleMedium) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 1. The Terminal Partition inside a Box to overlay the 'X' button
            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth().fillMaxHeight()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E1E1E).copy(alpha = 0.9f),
                    tonalElevation = 8.dp
                ) {
                    BasicTextField(
                        value = terminalHistory,
                        onValueChange = { newText ->
                            // Intercept Enter key
                            if (newText.endsWith("\n")) {
                                // Add your send logic here
                                terminalHistory += ""
                            } else {
                                terminalHistory = newText
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                            .verticalScroll(scrollState),
                        textStyle = TextStyle(
                            color = Color.Green,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        ),
                        cursorBrush = SolidColor(Color.Green),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send)
                    )
                }

                // The Clear (X) Button positioned at the top right
                IconButton(
                    onClick = { terminalHistory = "" },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Clear,
                        contentDescription = "Clear Terminal",
                        tint = Color.Gray // Subdued so it's not distracting
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Bottom Command Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = commandInput,
                    onValueChange = { commandInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Quick Command...") },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF2D2D2D), // Slightly lighter than terminal
                        unfocusedContainerColor = Color(0xFF1E1E1E),
                        focusedIndicatorColor = Color.Transparent, // Removes the bottom line
                        unfocusedIndicatorColor = Color.Transparent,
                      //  cursorColor = Color.Green
                    ),
                        singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (commandInput.isNotBlank()) {
                            viewModel.sendCommand(commandInput)
                            terminalHistory += "\n> $commandInput"
                            commandInput = ""
                        }
                    })
                )

                FilledIconButton(
                    onClick = {
                        if (commandInput.isNotBlank()) {
                            viewModel.sendCommand(commandInput)
                            terminalHistory += "\n> $commandInput"
                            commandInput = ""
                        }
                    },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))
        }
    }
}