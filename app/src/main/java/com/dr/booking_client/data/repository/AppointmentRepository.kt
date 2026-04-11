package com.dr.booking_client.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.dr.booking_client.data.model.Appointment
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import com.sharmaclinic.booking.data.model.Appointment
//import com.sharmaclinic.booking.data.model.TimeSlot
import com.dr.booking_client.data.model.TimeSlot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class AppointmentRepository {
    private val db = Firebase.firestore

    // Clinic schedule per day of week
    fun getSlotsForDay(dayOfWeek: Int): List<TimeSlot> {
        val morning = listOf("9:00 AM","9:30 AM","10:00 AM","10:30 AM","11:00 AM","11:30 AM")
        val evening = listOf("4:00 PM","4:30 PM","5:00 PM","5:30 PM","6:00 PM")
        val satMorning = listOf("10:00 AM","10:30 AM","11:00 AM","11:30 AM","12:00 PM","12:30 PM")
        return when (dayOfWeek) {
            Calendar.SUNDAY                    -> emptyList()
            Calendar.WEDNESDAY                 -> morning.map { TimeSlot(it) }
            Calendar.SATURDAY                  -> satMorning.map { TimeSlot(it) }
            else                               -> (morning + evening).map { TimeSlot(it) }
        }
    }

    suspend fun getBookedSlots(date: String): List<String> = runCatching {
        db.collection("appointments")
            .whereEqualTo("date", date)
            .whereEqualTo("status", "confirmed")
            .get().await()
            .documents.mapNotNull { it.getString("slotTime") }
    }.getOrDefault(emptyList())

    suspend fun bookAppointment(appointment: Appointment): Result<String> = runCatching {
        val ref  = db.collection("appointments").document()
        val appt = appointment.copy(id = ref.id)
        ref.set(appt).await()
        ref.id
    }

    fun getUserAppointmentsFlow(userId: String): Flow<List<Appointment>> = callbackFlow {
        val listener = db.collection("appointments")
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                val list = snap?.toObjects(Appointment::class.java) ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getAllAppointmentsFlow(): Flow<List<Appointment>> = callbackFlow {
        val listener = db.collection("appointments")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                val list = snap?.toObjects(Appointment::class.java) ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun cancelAppointment(appointmentId: String): Result<Unit> = runCatching {
        db.collection("appointments").document(appointmentId)
            .update("status", "cancelled").await()
    }
}