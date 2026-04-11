package com.dr.booking_client.data.repository


import android.app.Activity
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class AuthRepository {
    private val auth = Firebase.auth
    private val db   = Firebase.firestore

    val currentUser: FirebaseUser? get() = auth.currentUser

    // Step 1 — send OTP to phone number
    fun sendOtp(
        phoneNumber: String,     // must include country code e.g. "+919876543210"
        activity: Activity,
        onCodeSent: (verificationId: String) -> Unit,
        onVerified: (PhoneAuthCredential) -> Unit,   // instant verify (some devices)
        onError: (String) -> Unit
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Auto-retrieved OTP on some Android devices
                onVerified(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                onError(e.message ?: "Verification failed")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                onCodeSent(verificationId)
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Step 2 — verify OTP code entered by user
    suspend fun verifyOtp(
        verificationId: String,
        otpCode: String
    ): Result<FirebaseUser> = runCatching {
        val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
        signInWithCredential(credential)
    }

    // Step 3 — sign in with credential (shared by both paths)
    suspend fun signInWithCredential(credential: PhoneAuthCredential): FirebaseUser {
        val result = auth.signInWithCredential(credential).await()
        val user   = result.user!!

        // Save user to Firestore if new
        val docRef = db.collection("users").document(user.uid)
        val snap   = docRef.get().await()
        if (!snap.exists()) {
            docRef.set(
                mapOf(
                    "phone" to (user.phoneNumber ?: ""),
                    "role"  to "user",
                    "createdAt" to System.currentTimeMillis()
                )
            ).await()
        }
        return user
    }

    suspend fun isAdmin(uid: String): Boolean = runCatching {
        db.collection("users").document(uid)
            .get().await().getString("role") == "admin"
    }.getOrDefault(false)

    fun logout() = auth.signOut()
}