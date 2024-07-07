package com.example.flighttracker

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FlightViewModel(application: Application) : AndroidViewModel(application) {
    private val _flightAttributes = MutableStateFlow<FlightAttributes?>(null)
    val flightAttributes: StateFlow<FlightAttributes?> = _flightAttributes

    fun startFlight(airline: String, flightNumber: String) {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val flightAttributes = FlightAttributes(
                airline = airline,
                flightNumber = flightNumber,
                departureTime = currentTime + 60 * 60 * 1000,
                arrivalTime = currentTime + 5 * 60 * 60 * 1000,
                status = "On Time"
            )
            _flightAttributes.value = flightAttributes

            val intent = Intent(getApplication(), FlightService::class.java).apply {
                action = FlightService.ACTION_START
                putExtra(FlightService.EXTRA_FLIGHT_ATTRIBUTES, flightAttributes)
            }
            getApplication<Application>().startService(intent)
        }
    }

    fun updateFlight(status: String, departureTime: Long, arrivalTime: Long) {
        viewModelScope.launch {
            _flightAttributes.value?.let {
                val updatedAttributes = it.copy(
                    status = status,
                    departureTime = departureTime,
                    arrivalTime = arrivalTime
                )
                _flightAttributes.value = updatedAttributes

                val intent = Intent(getApplication(), FlightService::class.java).apply {
                    action = FlightService.ACTION_UPDATE
                    putExtra(FlightService.EXTRA_FLIGHT_ATTRIBUTES, updatedAttributes)
                }
                getApplication<Application>().startService(intent)
            }
        }
    }

    fun endFlight() {
        viewModelScope.launch {
            _flightAttributes.value?.let {
                val updatedAttributes = it.copy(status = "Arrived")
                _flightAttributes.value = updatedAttributes

                val intent = Intent(getApplication(), FlightService::class.java).apply {
                    action = FlightService.ACTION_END
                }
                getApplication<Application>().startService(intent)
            }
        }
    }
}
