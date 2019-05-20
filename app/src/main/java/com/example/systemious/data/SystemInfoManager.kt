package com.example.systemious.data

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.view.WindowManager
import androidx.core.content.ContextCompat.getSystemService
import com.example.systemious.domain.RamInfo
import android.R.attr.y
import android.R.attr.x
import android.graphics.Point
import android.view.Display



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

        // Params for System Details
        val deviceName = Build.MANUFACTURER

        val deviceModel = Build.MODEL

        val serialNumber = Build.SERIAL

        var radioVersion = Build.getRadioVersion() ?: "Not available"

        var androidVersion = Build.VERSION.RELEASE

        fun getScreenResolution(context: Context): String {
            val windowsManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowsManager.defaultDisplay
            val size = Point()
            display.getSize(size)

            val width = size.x //device width
            val height = size.y //device height

            return "$width x $height" //example "480 * 800"
        }

        fun getAndroidVersion(packageInfo: PackageInfo): String {
            return packageInfo.versionName + " " + packageInfo.versionCode
        }

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