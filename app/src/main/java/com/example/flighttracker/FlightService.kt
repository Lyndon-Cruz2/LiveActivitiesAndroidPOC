package com.example.flighttracker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FlightService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("FlightService", "onStartCommand")
        if (intent != null) {
            val action = intent.action
            when (action) {
                ACTION_START -> {
                    val flightAttributes = intent.getParcelableExtra<FlightAttributes>(EXTRA_FLIGHT_ATTRIBUTES)
                    if (flightAttributes != null) {
                        startForegroundService(flightAttributes)
                        Toast.makeText(this, "FlightService Attributes started", Toast.LENGTH_SHORT).show()
                        Log.d("FlightService", "FlightService Attributes started")
                    } else {
                        Toast.makeText(this, "FlightService Attributes not found", Toast.LENGTH_SHORT).show()
                        Log.d("FlightService", "FlightService Attributes not found")
                    }
                }
                ACTION_UPDATE -> {
                    val flightAttributes = intent.getParcelableExtra<FlightAttributes>(EXTRA_FLIGHT_ATTRIBUTES)
                    if (flightAttributes != null) {
                        updateNotification(flightAttributes)
                        Toast.makeText(this, "FlightService Attributes updated", Toast.LENGTH_SHORT).show()
                        Log.d("FlightService", "FlightService Attributes updated")
                    }
                }
                ACTION_END -> {
                    stopForeground(true)
                    stopSelf()
                    Toast.makeText(this, "FlightService Attributes ended", Toast.LENGTH_SHORT).show()
                    Log.d("FlightService", "FlightService Attributes ended")
                }
            }
        } else {
            Toast.makeText(this, "FlightService Attributes not found", Toast.LENGTH_SHORT).show()
            Log.d("FlightService", "FlightService Attributes not found")
        }
        return START_STICKY
    }

    private fun startForegroundService(flightAttributes: FlightAttributes) {
        createNotificationChannel()
        val notification = buildNotification(flightAttributes)
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun updateNotification(flightAttributes: FlightAttributes) {
        val notification = buildNotification(flightAttributes)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(flightAttributes: FlightAttributes): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Flight: ${flightAttributes.flightNumber}")
            .setContentText("Status: ${flightAttributes.status}")
            .setSmallIcon(R.drawable.ic_flight)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Airline: ${flightAttributes.airline}\n" +
                        "Departure: ${formatTime(flightAttributes.departureTime)}\n" +
                        "Arrival: ${formatTime(flightAttributes.arrivalTime)}\n" +
                        "Status: ${flightAttributes.status}"))
            .build()
    }

    private fun createNotificationChannel() {
        Toast.makeText(this, "FlightService Notification Channel created", Toast.LENGTH_SHORT).show()
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Flight Live Activities",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val ACTION_START = "com.example.flighttracker.ACTION_START"
        const val ACTION_UPDATE = "com.example.flighttracker.ACTION_UPDATE"
        const val ACTION_END = "com.example.flighttracker.ACTION_END"
        const val EXTRA_FLIGHT_ATTRIBUTES = "com.example.flighttracker.EXTRA_FLIGHT_ATTRIBUTES"
        const val CHANNEL_ID = "flight_flight_tracker_channel"
        const val NOTIFICATION_ID = 1
    }
}

fun formatTime(time: Long): String {
    val formatter = SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.getDefault())
    return formatter.format(Date(time))
}