package com.example.flighttracker

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FlightAttributes(
    val airline: String,
    val flightNumber: String,
    val departureTime: Long,
    val arrivalTime: Long,
    val status: String
) : Parcelable
