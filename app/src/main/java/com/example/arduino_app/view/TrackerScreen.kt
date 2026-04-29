package com.example.arduino_app.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.arduino_app.viewModel.ControlViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Text
import androidx.compose.material3.ripple

@Composable
fun TrackerScreen(viewModel: ControlViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Manual Tracker Control", style = MaterialTheme.typography.titleLarge)
        Text("Hold to move, release to stop", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(32.dp))

        // D-Pad
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // UP
            DirectionButton(
                icon = Icons.Default.KeyboardArrowUp,
                onPress = { viewModel.sendCommand("U") },
                onRelease = { viewModel.sendCommand("S") }
            )

            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                // LEFT
                DirectionButton(
                    icon = Icons.Default.KeyboardArrowLeft,
                    onPress = { viewModel.sendCommand("L") },
                    onRelease = { viewModel.sendCommand("S") }
                )
                Spacer(modifier = Modifier.width(88.dp)) // Space for center
                // RIGHT
                DirectionButton(
                    icon = Icons.Default.KeyboardArrowRight,
                    onPress = { viewModel.sendCommand("R") },
                    onRelease = { viewModel.sendCommand("S") }
                )
            }

            // DOWN
            DirectionButton(
                icon = Icons.Default.KeyboardArrowDown,
                onPress = { viewModel.sendCommand("D") },
                onRelease = { viewModel.sendCommand("S") }
            )
        }
    }
}

@Composable
fun DirectionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(72.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(40.dp)
        )
    }
}
@Composable
fun DirectionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onPress: () -> Unit,
    onRelease: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Trigger actions based on state change
    LaunchedEffect(isPressed) {
        if (isPressed) {
            onPress()
        } else {
            onRelease()
        }
    }

    Surface(
        color = if (isPressed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .size(80.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),

                onClick = {} // Required for clickable, but logic is in LaunchedEffect
            )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = if (isPressed) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}