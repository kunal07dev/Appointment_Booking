package com.dr.booking_client.viewmodel



import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dr.booking_client.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle           : AuthState()
    object SendingOtp     : AuthState()
    object OtpSent        : AuthState()   // show OTP input screen
    object VerifyingOtp   : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String)      : AuthState()
}

class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()

    private val _authState        = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // Holds verificationId between step 1 and step 2
    private var verificationId: String = ""

    val currentUser get() = repo.currentUser

    // STEP 1: Send OTP
    fun sendOtp(rawPhone: String, activity: Activity) {
        // Prepend +91 if user typed only 10 digits
        val phone = if (rawPhone.startsWith("+")) rawPhone else "+91$rawPhone"

        _authState.value = AuthState.SendingOtp

        repo.sendOtp(
            phoneNumber = phone,
            activity    = activity,
            onCodeSent  = { id ->
                verificationId       = id
                _authState.value     = AuthState.OtpSent
            },
            onVerified  = { credential ->
                // Auto-verified (rare but handle it)
                signInWithCredential(credential)
            },
            onError     = { msg ->
                _authState.value = AuthState.Error(msg)
            }
        )
    }

    // STEP 2: Verify OTP entered by user
    fun verifyOtp(otpCode: String) {
        viewModelScope.launch {
            _authState.value = AuthState.VerifyingOtp
            repo.verifyOtp(verificationId, otpCode).fold(
                onSuccess = { _authState.value = AuthState.Success(it) },
                onFailure = { _authState.value = AuthState.Error(it.message ?: "Wrong OTP") }
            )
        }
    }

    // Used for auto-verification path
    private fun signInWithCredential(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            _authState.value = AuthState.VerifyingOtp
            runCatching { repo.signInWithCredential(credential) }.fold(
                onSuccess = { _authState.value = AuthState.Success(it) },
                onFailure = { _authState.value = AuthState.Error(it.message ?: "Sign-in failed") }
            )
        }
    }

    fun logout() {
        repo.logout()
        _authState.value = AuthState.Idle
    }

    fun resetState() { _authState.value = AuthState.Idle }
}