package com.example.arduino_app.view

import android.R
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center.run {
            Arrangement.spacedBy(22.dp, Alignment.CenterVertically)
        }
    ) {
        Text("Office Control", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        // --- 1. AUTOMATIC MODE SECTION ---
        // A full-width card to toggle the "Smart" logic
        SmartToggleCard(
            title = "Automatic Mode",
            subtitle = if (isAutoMode) "Sensors are in control" else "Manual control active",
            isActive = isAutoMode,
            icon = Icons.Default.AutoAwesome,
            onClick = { viewModel.setAutoMode(!isAutoMode) }
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Devices",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleSmall,
            color = Color.Gray
        )

        // --- 2. DEVICE TILES ---
        // We put these in a Row to create a grid look
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // LAMP TILE
            DeviceTile(
                title = "Lamp",
                status = if (isLampOn) "On" else "Off",
                isActive = isLampOn,
                activeColor =  Color(0xFFFFC107),
                icon = if (isLampOn) Icons.Filled.Lightbulb else Icons.Outlined.Lightbulb,
                enabled = !isAutoMode,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.setLamp(!isLampOn) }
            )

            // DOOR TILE
            DeviceTile(
                title = "Door",
                status = if (isDoorOpen) "Open" else "Closed",
                isActive = isDoorOpen,
                activeColor = MaterialTheme.colorScheme.primary,
                icon = if (isDoorOpen) Icons.Filled.MeetingRoom else Icons.Outlined.MeetingRoom,
                enabled = !isAutoMode,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.ToggleDoor(!isDoorOpen) }
            )
        }
    }
}

@Composable
fun DeviceTile(
    title: String,
    status: String,
    isActive: Boolean,
    activeColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Animations for the visionary feel
    val animatedBgColor by animateColorAsState(
        targetValue = if (isActive) activeColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    )
    val animatedIconColor by animateColorAsState(
        targetValue = if (isActive) activeColor else Color.Gray
    )

    Surface(
        onClick = { if (enabled) onClick() },
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(24.dp),
        color = animatedBgColor,
        border = if (isActive) BorderStroke(2.dp, activeColor.copy(alpha = 0.5f)) else null,
        enabled = enabled
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = animatedIconColor,
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (enabled) status else "Auto",
                    style = MaterialTheme.typography.bodySmall,
                    color = animatedIconColor
                )
            }
        }
    }
}
@Composable
fun SmartToggleCard(
    title: String,
    subtitle: String,
    isActive: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick, // Clicking the whole card toggles the state
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        // The card still changes color to show it's "Active"
        color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,//MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Icon (e.g., AutoAwesome)
//            Icon(
//                imageVector = icon,
//                contentDescription = null,
//                modifier = Modifier.size(28.dp)
//            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    // Keep subtitle readable against the primary color
                    color = if (isActive) Color.White.copy(alpha = 0.8f) else Color.Gray
                )
            }

            // --- The Switch ---
            Switch(
                checked = isActive,
                onCheckedChange = { onClick() }, // Toggles via the same logic as the card
                // Optional: Custom colors to make the switch pop against the blue background
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = Color.White,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }
}