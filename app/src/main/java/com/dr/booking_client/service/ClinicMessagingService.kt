//package com.dr.booking_client.service
//
//
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import androidx.core.app.NotificationCompat
//import androidx.core.content.ContextCompat.getSystemService
//import com.google.firebase.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//import com.dr.booking_client.MainActivity
//import com.dr.booking_client.R
//import com.google.firebase.messaging.FirebaseMessagingService
//
//class ClinicMessagingService : FirebaseMessagingService() {
//
//    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
//        val title = message.notification?.title ?: "Sharma Wellness Clinic"
//        val body  = message.notification?.body  ?: "You have a new notification"
//        showNotification(title, body)
//    }
//
//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//        // Optionally save to Firestore: db.collection("users").document(uid).update("fcmToken", token)
//    }
//
//    private fun showNotification(title: String, body: String) {
//        val channelId = "clinic_reminders"
//        val manager   = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            manager.createNotificationChannel(
//                NotificationChannel(channelId, "Clinic Reminders",
//                    NotificationManager.IMPORTANCE_HIGH)
//            )
//        }
//
//        val intent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        }
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, intent,
//            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val notification = NotificationCompat.Builder(this, channelId)
//            .setContentTitle(title)
//            .setContentText(body)
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//            .build()
//
//        manager.notify(System.currentTimeMillis().toInt(), notification)
//    }
//}