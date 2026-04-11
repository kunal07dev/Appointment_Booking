package com.dr.booking_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dr.booking_client.data.model.Appointment
import com.dr.booking_client.data.repository.AppointmentRepository
import com.dr.booking_client.data.repository.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val apptRepo = AppointmentRepository()
    private val authRepo = AuthRepository()

    private val _isAdmin         = MutableStateFlow(false)
    private val _isCheckingRole  = MutableStateFlow(true)
    private val _filterDate      = MutableStateFlow<String?>(null)
    private val _cancelResult    = MutableStateFlow<String?>(null)

    val isAdmin: StateFlow<Boolean>        = _isAdmin
    val isCheckingRole: StateFlow<Boolean> = _isCheckingRole
    val cancelResult: StateFlow<String?>   = _cancelResult

    val appointments: StateFlow<List<Appointment>> =
        combine(apptRepo.getAllAppointmentsFlow(), _filterDate) { list, date ->
            if (date == null) list else list.filter { it.date == date }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun checkAdminRole(uid: String) {
        viewModelScope.launch {
            _isAdmin.value        = authRepo.isAdmin(uid)
            _isCheckingRole.value = false
        }
    }

    fun setDateFilter(date: String?) { _filterDate.value = date }

    fun cancelAppointment(id: String) {
        viewModelScope.launch {
            apptRepo.cancelAppointment(id).fold(
                onSuccess = { _cancelResult.value = "Appointment cancelled" },
                onFailure = { _cancelResult.value = "Failed: ${it.message}" }
            )
        }
    }

    fun clearCancelResult() { _cancelResult.value = null }
}