package com.dr.booking_client.ui.screens.booking



import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseUser
import com.dr.booking_client.ui.components.StepIndicator
import com.dr.booking_client.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    viewModel: BookingViewModel,
    currentUser: FirebaseUser?,
    onBack: () -> Unit,
    onLoginRequest: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentStep by remember { mutableIntStateOf(1) }

    // Reset when leaving
    DisposableEffect(Unit) { onDispose { viewModel.reset() } }

    // Advance to step 4 on success
    LaunchedEffect(uiState.bookingSuccess) {
        if (uiState.bookingSuccess != null) currentStep = 4
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titles = listOf("", "Choose Date", "Pick a Slot", "Your Details", "Confirmed!")
                    Text(titles[currentStep])
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 1) currentStep-- else onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            StepIndicator(currentStep = currentStep)
            Spacer(Modifier.height(20.dp))

            when (currentStep) {
                1 -> Step1DateScreen(
                    selectedDate = uiState.selectedDate,
                    onDateSelected = { cal ->
                        viewModel.selectDate(cal)
                        currentStep = 2
                    }
                )
                2 -> Step2SlotScreen(
                    slots      = uiState.slots,
                    isLoading  = uiState.isLoadingSlots,
                    selectedSlot = uiState.selectedSlot,
                    selectedDate = uiState.selectedDate?.let { viewModel.formatDisplayDate(it) } ?: "",
                    onSlotSelected = { viewModel.selectSlot(it) },
                    onNext     = { currentStep = 3 }
                )
                3 -> Step3DetailsScreen(
                    isLoading      = uiState.isBooking,
                    error          = uiState.error,
                    currentUser    = currentUser,
                    selectedDate   = uiState.selectedDate?.let { viewModel.formatDisplayDate(it) } ?: "",
                    selectedSlot   = uiState.selectedSlot ?: "",
                    onConfirm      = { name, phone, email ->
                        viewModel.confirmBooking(
                            name     = name,
                            phone    = phone,
                            email    = email,
                            userId   = currentUser?.uid ?: "",
                            isGuest  = currentUser == null
                        )
                    },
                    onLoginRequest = onLoginRequest,
                    onClearError   = { viewModel.clearError() }
                )
                4 -> Step4ConfirmedScreen(
                    appointmentId = uiState.bookingSuccess ?: "",
                    patientName   = "",
                    date          = uiState.selectedDate?.let { viewModel.formatDisplayDate(it) } ?: "",
                    slot          = uiState.selectedSlot ?: "",
                    onDone        = onBack
                )
            }
        }
    }
}