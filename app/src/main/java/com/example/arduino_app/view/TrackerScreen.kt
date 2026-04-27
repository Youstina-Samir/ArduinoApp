package com.example.arduino_app.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.arduino_app.viewModel.OfficeViewModel

@Composable
fun TrackerScreen(viewModel: OfficeViewModel) {
    // Collect the state from the ViewModel
    //  val state by viewModel.uiState.collectAsState()

    // Pass the state down to the pure UI function
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Text(text = "Current Progress: ${state.progress}")
//        Button(onClick = onButtonClick) {
//            Text("Update Tracker")
//        }
    }
}