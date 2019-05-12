package com.example.systemious.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CpuInfo(
    var number: Int = 0,
    var currFrequency: Double = 0.0,
    var maxFrequency: Double = 0.0
) : Parcelable

@Parcelize
data class RamInfo(
    var usedRam: Long = 0,
    var totalRam: Long = 0
) : Parcelable