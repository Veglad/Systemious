package com.example.systemious.data

object SystemInfoManager {

    val coresNumber = CpuInfoCollector.calcCpuCoreNumber()

    val maxCoresFrequencies by lazy {
        CpuInfoCollector.takeMaxCoresFrequencies()
    }

    val minCoresFrequencies by lazy {
        CpuInfoCollector.takeMinCoresFrequencies()
    }

    val currentCoresFrequencies = CpuInfoCollector.takeCurrentCoresFrequencies()
}