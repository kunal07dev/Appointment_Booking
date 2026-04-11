package com.dr.booking_client.ui.screens.booking



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dr.booking_client.data.model.TimeSlot
import com.dr.booking_client.ui.theme.SlotSelected
import com.dr.booking_client.ui.theme.SlotUnavailable

@Composable
fun Step2SlotScreen(
    slots: List<TimeSlot>,
    isLoading: Boolean,
    selectedSlot: String?,
    selectedDate: String,
    onSlotSelected: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text  = selectedDate,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text  = "Tap a green slot to select your time",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            slots.isEmpty() -> Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text("No slots available on this day (clinic closed)",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium)
            }
            else -> {
                // Legend
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    LegendItem(color = MaterialTheme.colorScheme.primaryContainer, label = "Available")
                    LegendItem(color = SlotSelected, label = "Selected")
                    LegendItem(color = SlotUnavailable, label = "Booked")
                }
                Spacer(Modifier.height(12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    items(slots) { slot ->
                        val isSelected = slot.time == selectedSlot
                        val containerColor = when {
                            isSelected      -> MaterialTheme.colorScheme.primary
                            !slot.isAvailable -> SlotUnavailable
                            else            -> MaterialTheme.colorScheme.primaryContainer
                        }
                        val textColor = when {
                            isSelected      -> MaterialTheme.colorScheme.onPrimary
                            !slot.isAvailable -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            else            -> MaterialTheme.colorScheme.onPrimaryContainer
                        }
                        Surface(
                            onClick = { if (slot.isAvailable) onSlotSelected(slot.time) },
                            enabled = slot.isAvailable,
                            shape = RoundedCornerShape(10.dp),
                            color = containerColor,
                            modifier = Modifier.height(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(slot.time, style = MaterialTheme.typography.labelLarge, color = textColor)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onNext,
            enabled = selectedSlot != null,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Continue to Your Details →")
        }
    }
}

@Composable
private fun LegendItem(color: androidx.compose.ui.graphics.Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = color,
            modifier = Modifier.size(14.dp)
        ) {}
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}