package com.dr.booking_client.data.model
data class Appointment(
    val id: String          = "",
    val userId: String      = "",       // "" for guest
    val isGuest: Boolean    = false,
    val patientName: String = "",
    val phone: String       = "",
    val email: String       = "",
    val date: String        = "",       // "2026-03-20"
    val slotTime: String    = "",       // "9:00 AM"
    val status: String      = "confirmed", // confirmed | cancelled
    val createdAt: Long     = System.currentTimeMillis()
)
