package com.dr.booking_client.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dr.booking_client.data.model.Appointment
import com.dr.booking_client.data.model.TimeSlot
import com.dr.booking_client.data.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class BookingUiState(
    val selectedDate: Calendar?    = null,
    val selectedSlot: String?      = null,
    val slots: List<TimeSlot>      = emptyList(),
    val isLoadingSlots: Boolean    = false,
    val isBooking: Boolean         = false,
    val bookingSuccess: String?    = null,   // appointmentId on success
    val error: String?             = null
)

class BookingViewModel : ViewModel() {
    private val repo = AppointmentRepository()

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState

    fun selectDate(cal: Calendar) {
        _uiState.value = _uiState.value.copy(
            selectedDate = cal,
            selectedSlot = null,
            slots        = emptyList()
        )
        loadSlots(cal)
    }

    private fun loadSlots(cal: Calendar) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingSlots = true)
            val dateStr  = formatDate(cal)
            val allSlots = repo.getSlotsForDay(cal.get(Calendar.DAY_OF_WEEK))
            val booked   = repo.getBookedSlots(dateStr)
            _uiState.value = _uiState.value.copy(
                slots        = allSlots.map { it.copy(isAvailable = it.time !in booked) },
                isLoadingSlots = false
            )
        }
    }

    fun selectSlot(time: String) {
        _uiState.value = _uiState.value.copy(selectedSlot = time)
    }

    fun confirmBooking(
        name: String,
        phone: String,
        email: String,
        userId: String,   // empty string if guest
        isGuest: Boolean
    ) {
        val date = _uiState.value.selectedDate ?: return
        val slot = _uiState.value.selectedSlot ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBooking = true, error = null)
            repo.bookAppointment(
                Appointment(
                    userId      = userId,
                    isGuest     = isGuest,
                    patientName = name,
                    phone       = phone,
                    email       = email,
                    date        = formatDate(date),
                    slotTime    = slot
                )
            ).fold(
                onSuccess = { id ->
                    _uiState.value = _uiState.value.copy(isBooking = false, bookingSuccess = id)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(isBooking = false, error = e.message)
                }
            )
        }
    }

    fun reset() { _uiState.value = BookingUiState() }
    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }

    fun formatDate(cal: Calendar): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

    fun formatDisplayDate(cal: Calendar): String =
        SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault()).format(cal.time)
}