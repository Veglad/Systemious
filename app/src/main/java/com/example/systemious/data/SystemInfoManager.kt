package com.example.systemious.data

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import com.example.systemious.domain.RamInfo

class SystemInfoManager {
    companion object {
        val coresNumber = CpuInfoCollector.calcCpuCoreNumber()

        val maxCoresFrequencies by lazy {
            CpuInfoCollector.takeMaxCoresFrequencies()
        }

        val minCoresFrequencies by lazy {
            CpuInfoCollector.takeMinCoresFrequencies()
        }

        val currentCoresFrequencies = CpuInfoCollector.takeCurrentCoresFrequencies()

        fun getCpuUsageSnapshot() = CpuInfoCollector.takeCpuUsageSnapshot()

        fun getRamUsedValue(context: Context): Long {
            val memoryInfo = ActivityManager.MemoryInfo()
            val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            activityManager.getMemoryInfo(memoryInfo)

            return memoryInfo.totalMem - memoryInfo.availMem
        }

        fun getRamCapacity(context: Context): Long {
            val memoryInfo = ActivityManager.MemoryInfo()
            val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            activityManager.getMemoryInfo(memoryInfo)

            return memoryInfo.totalMem
        }
    }
}