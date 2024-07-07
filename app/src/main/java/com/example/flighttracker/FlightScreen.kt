package com.example.flighttracker

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FlightScreen(viewModel: FlightViewModel = viewModel()) {
    val flightAttributes by viewModel.flightAttributes.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = {
            viewModel.startFlight("Airline Name", "AB123")
            startFlightService(context, "Airline Name", "AB123", System.currentTimeMillis(), System.currentTimeMillis() + 2 * 60 * 60 * 1000, "On time")
        }) {
            Text("Start Flight Activity")
        }

        Button(onClick = {
            val currentTime = System.currentTimeMillis()
            viewModel.updateFlight("Delayed", currentTime + 2 * 60 * 60 * 1000, currentTime + 7 * 60 * 60 * 1000)
            updateFlightService(context, "Airline Name", "AB123", currentTime + 2 * 60 * 60 * 1000, currentTime + 7 * 60 * 60 * 1000, "Delayed")
        }, enabled = flightAttributes != null) {
            Text("Update Flight Activity")
        }

        Button(onClick = {
            viewModel.endFlight()
            endFlightService(context)
        }, enabled = flightAttributes != null) {
            Text("End Flight Activity")
        }

        flightAttributes?.let {
            FlightInfo(it)
        }
    }
}

private fun startFlightService(context: Context, airline: String, flightNumber: String, departureTime: Long, arrivalTime: Long, status: String) {
    val flightAttributes = FlightAttributes(airline, flightNumber, departureTime, arrivalTime, status)
    val serviceIntent = Intent(context, FlightService::class.java).apply {
        action = FlightService.ACTION_START
        putExtra(FlightService.EXTRA_FLIGHT_ATTRIBUTES, flightAttributes)
    }
    ContextCompat.startForegroundService(context, serviceIntent)
    Toast.makeText(context, "FlightService started", Toast.LENGTH_SHORT).show()
    Log.d("FlightService", "FlightService started")
}

private fun updateFlightService(context: Context, airline: String, flightNumber: String, departureTime: Long, arrivalTime: Long, status: String) {
    val flightAttributes = FlightAttributes(airline, flightNumber, departureTime, arrivalTime, status)
    val serviceIntent = Intent(context, FlightService::class.java).apply {
        action = FlightService.ACTION_UPDATE
        putExtra(FlightService.EXTRA_FLIGHT_ATTRIBUTES, flightAttributes)
    }
    ContextCompat.startForegroundService(context, serviceIntent)
    Toast.makeText(context, "FlightService updated", Toast.LENGTH_SHORT).show()
}

private fun endFlightService(context: Context) {
    val serviceIntent = Intent(context, FlightService::class.java).apply {
        action = FlightService.ACTION_END
    }
    ContextCompat.startForegroundService(context, serviceIntent)
    Toast.makeText(context, "FlightService ended", Toast.LENGTH_SHORT).show()
}

@Composable
fun FlightInfo(attributes: FlightAttributes) {
    Column {
        Text("Airline: ${attributes.airline}")
        Text("Flight Number: ${attributes.flightNumber}")
        Text("Departure: ${formatFlightTime(attributes.departureTime)}")
        Text("Arrival: ${formatFlightTime(attributes.arrivalTime)}")
        Text("Status: ${attributes.status}")
    }
}

fun formatFlightTime(time: Long): String {
    val formatter = SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.getDefault())
    return formatter.format(Date(time))
}