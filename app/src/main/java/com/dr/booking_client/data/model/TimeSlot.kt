package com.dr.booking_client.data.model

data class TimeSlot(
    val time: String,
    val isAvailable: Boolean = true
)