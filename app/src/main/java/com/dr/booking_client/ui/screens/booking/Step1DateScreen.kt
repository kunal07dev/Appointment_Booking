package com.dr.booking_client.ui.screens.booking


import android.widget.CalendarView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.util.Calendar

@Composable
fun Step1DateScreen(
    selectedDate: Calendar?,
    onDateSelected: (Calendar) -> Unit
) {
    var internalDate by remember { mutableStateOf(selectedDate) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text  = "Select a date for your appointment",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(Modifier.height(16.dp))

        Card(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AndroidView(
                factory = { ctx ->
                    CalendarView(ctx).apply {
                        minDate = System.currentTimeMillis() - 1000
                        setOnDateChangeListener { _, year, month, day ->
                            val cal = Calendar.getInstance().apply { set(year, month, day) }
                            internalDate = cal
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { internalDate?.let(onDateSelected) },
            enabled = internalDate != null,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
            Text("Continue to Pick a Slot →")
        }
    }
}