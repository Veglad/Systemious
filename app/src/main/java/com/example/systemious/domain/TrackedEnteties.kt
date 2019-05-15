package com.example.systemious.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RamInfo(
    var usedRam: Long = 0,
    var totalRam: Long = 0
) : Parcelable

@Parcelize
data class AllCoresFrequencies(
    var frequencies: MutableList<Int> = mutableListOf(),
    var maxFrequencies: MutableList<Int> = mutableListOf(),
    var minFrequencies: MutableList<Int> = mutableListOf()
) : Parcelable

@Parcelize
data class OneCpuInfo (
    var idle: Long = 0,
    var total: Long = 0
) : Parcelable