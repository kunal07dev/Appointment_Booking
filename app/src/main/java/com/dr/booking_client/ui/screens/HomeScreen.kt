package com.dr.booking_client.ui.screens


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.dr.booking_client.ui.components.SectionTitle
import com.google.firebase.auth.FirebaseUser


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentUser: FirebaseUser?,
    onBookClick: () -> Unit,
    onMyApptClick: () -> Unit,
    onLoginClick: () -> Unit,
    onLogout: () -> Unit,
    onAdminClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sharma Wellness Clinic") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    if (currentUser != null) {
                        IconButton(onClick = onMyApptClick) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "My Appointments",
                                tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout",
                                tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    } else {
                        TextButton(onClick = onLoginClick) {
                            Text("Login", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar placeholder
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                        modifier = Modifier.size(90.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(52.dp))
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Dr. Priya Sharma",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary)
                    Text("General Physician & Internal Medicine",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    Text("14 years experience",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))

                    Spacer(Modifier.height(16.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onBookClick,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onPrimary,
                                contentColor   = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Book Appointment")
                        }
                        OutlinedButton(
                            onClick = { /* dial */ },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Call Clinic", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }

            // Clinic info card
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    SectionTitle("Clinic Info")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Block C-12, Sector 18, Noida, UP — 201301",
                            style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("+91 98765 43210", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Hours card
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    SectionTitle("Clinic Hours")
                    val hours = listOf(
                        Triple("Mon – Tue, Thu – Fri", "9–11:30 AM", "4–6 PM"),
                        Triple("Wednesday",            "9–11:30 AM", "Closed"),
                        Triple("Saturday",             "10 AM–12:30 PM", "Closed"),
                        Triple("Sunday",               "Closed", "")
                    )
                    hours.forEach { (day, morning, evening) ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(day, style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1.5f))
                            Column(horizontalAlignment = Alignment.End) {
                                Text(morning, style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary)
                                if (evening.isNotEmpty())
                                    Text(evening, style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        if (day != hours.last().first)
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    }
                }
            }

            // Services card
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    SectionTitle("Services Offered")
                    val services = listOf(
                        "General Consultation",
                        "Preventive Health Check-up",
                        "Chronic Disease Management",
                        "Vaccination & Immunisation",
                        "Lab Test Referrals",
                        "Follow-up Consultations"
                    )
                    services.forEach { service ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(service, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Admin access (discreet)
            TextButton(
                onClick = onAdminClick,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
                Spacer(Modifier.width(4.dp))
                Text("Admin", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}