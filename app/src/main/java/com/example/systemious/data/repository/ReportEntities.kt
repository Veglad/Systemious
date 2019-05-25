package com.example.systemious.data.repository


data class CpuCoresUsageInterval(
    var coresUsages: MutableList<MutableList<Float>> = mutableListOf(),
    var startIntervalDateInMs: Long = 0,
    var timeCheckingOffsetInMs: Int = 1000
)

data class ramUsageInterval(
    var ramUsages: MutableList<Float> = mutableListOf(),
    var startIntervalDateInMs: Long = 0,
    var timeCheckingOffsetInMs: Int = 1000
)