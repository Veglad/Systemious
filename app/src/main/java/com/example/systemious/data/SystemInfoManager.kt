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

        fun getRamInfo(context: Context): RamInfo {
            val memoryInfo = ActivityManager.MemoryInfo()
            val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            activityManager.getMemoryInfo(memoryInfo)

            val ramInfo = RamInfo(memoryInfo.totalMem - memoryInfo.availMem, memoryInfo.totalMem)
            return ramInfo
        }
    }
}