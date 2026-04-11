package com.dr.booking_client.ui.screens.admin


import android.widget.CalendarView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import com.dr.booking_client.data.model.Appointment
import com.dr.booking_client.ui.screens.appointments.StatusChip
import com.dr.booking_client.viewmodel.AdminViewModel
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    currentUser: FirebaseUser?,
    onBack: () -> Unit,
    onLoginRequired: () -> Unit
) {
    val viewModel: AdminViewModel = viewModel()
    val isCheckingRole  by viewModel.isCheckingRole.collectAsStateWithLifecycle()
    val isAdmin         by viewModel.isAdmin.collectAsStateWithLifecycle()
    val appointments    by viewModel.appointments.collectAsStateWithLifecycle()
    val cancelResult    by viewModel.cancelResult.collectAsStateWithLifecycle()

    var showCalendar by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser) {
        if (currentUser == null) onLoginRequired()
        else viewModel.checkAdminRole(currentUser.uid)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(cancelResult) {
        cancelResult?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearCancelResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showCalendar = !showCalendar }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter by date",
                            tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    if (showCalendar) {
                        TextButton(onClick = {
                            viewModel.setDateFilter(null)
                            showCalendar = false
                        }) {
                            Text("Clear", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when {
            isCheckingRole -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            !isAdmin -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Lock, contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Text("Access Denied", style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error)
                    Text("You do not have admin privileges.",
                        style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onBack) { Text("Go Back") }
                }
            }
            else -> Column(Modifier.fillMaxSize().padding(padding)) {
                // Stats bar
                val confirmed  = appointments.count { it.status == "confirmed" }
                val cancelled  = appointments.count { it.status == "cancelled" }
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard("Total", appointments.size.toString(), Modifier.weight(1f))
                    StatCard("Confirmed", confirmed.toString(), Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary)
                    StatCard("Cancelled", cancelled.toString(), Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.error)
                }

                // Calendar filter
                if (showCalendar) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                CalendarView(ctx).apply {
                                    setOnDateChangeListener { _, year, month, day ->
                                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        val cal = Calendar.getInstance().apply { set(year, month, day) }
                                        viewModel.setDateFilter(sdf.format(cal.time))
                                        showCalendar = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // List
                if (appointments.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No appointments found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(appointments, key = { it.id }) { appt ->
                            AdminAppointmentCard(
                                appointment = appt,
                                onCancel = { viewModel.cancelAppointment(appt.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Card(modifier = modifier, shape = RoundedCornerShape(10.dp)) {
        Column(
            Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun AdminAppointmentCard(appointment: Appointment, onCancel: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Cancel this appointment?") },
            text = {
                Text("Patient: ${appointment.patientName}\n" +
                        "Date: ${appointment.date} at ${appointment.slotTime}")
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false; onCancel() }) {
                    Text("Cancel Appointment", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Keep") }
            }
        )
    }

    Card(shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(appointment.patientName, style = MaterialTheme.typography.titleMedium)
                    Text(appointment.phone, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    if (appointment.isGuest) {
                        Text("Guest booking", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary)
                    }
                }
                StatusChip(appointment.status)
            }
            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null,
                        modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(4.dp))
                    Text(appointment.date, style = MaterialTheme.typography.bodyMedium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null,
                        modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(4.dp))
                    Text(appointment.slotTime, style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (appointment.status == "confirmed") {
                Spacer(Modifier.height(4.dp))
                TextButton(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Cancel appointment")
                }
            }
        }
    }
}