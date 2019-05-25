package com.example.systemious.data

import com.example.systemious.domain.AllCoresFrequencies
import com.example.systemious.domain.OneCpuInfo
import com.example.systemious.utils.Constants
import com.example.systemious.utils.getTextAndClose
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*
import java.util.regex.Pattern

class CpuInfoCollector {

    companion object {
        // core count cache
        private var sLastCpuCoreCount = -1

        /**
         * Gets the number of cores available in this device, across all processors.
         * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
         *
         * @return The number of cores, or "availableProcessors" if failed to get result
         */
        // from http://stackoverflow.com/questions/7962155/how-can-you-detect-a-dual-core-cpu-on-an-android-device-from-code
        fun calcCpuCoreNumber(): Int {

            if (sLastCpuCoreCount >= 1) {
                // Cache
                return sLastCpuCoreCount
            }

            sLastCpuCoreCount = try {
                // Get directory containing CPU info
                val dir = File("/sys/devices/system/cpu/")
                // Filter to only list the devices we care about
                val files = dir.listFiles { pathname ->
                    //Check if filename is "cpu", followed by a single digit number
                    Pattern.matches("cpu[0-9]", pathname.name)
                }

                // Return the number of cores (virtual CPU devices)
                files.size

            } catch (e: Exception) {
                Runtime.getRuntime().availableProcessors()
            }

            return sLastCpuCoreCount
        }

        /**
         * Get current CPU clock
         *
         * @return A number such as 384000 (0 for acquisition errors)
         */
        private fun takeCurrentCpuFreq(coreIndex: Int): Int {
            return readIntegerFile("/sys/devices/system/cpu/cpu$coreIndex/cpufreq/scaling_cur_freq")
        }

        /**
         * Get minimum CPU clock
         *
         * @return A number such as 384000 (0 for acquisition errors)
         */
        private fun takeMinCpuFreq(coreIndex: Int): Int {
            return readIntegerFile("/sys/devices/system/cpu/cpu$coreIndex/cpufreq/cpuinfo_min_freq")
        }

        /**
         * Get maximum CPU clock
         *
         * @return A number such as 384000 (0 for acquisition errors)
         */
        private fun takeMaxCpuFreq(coreIndex: Int): Int {
            return readIntegerFile("/sys/devices/system/cpu/cpu$coreIndex/cpufreq/cpuinfo_max_freq")
        }

        fun takeAllCoresFrequencies() : AllCoresFrequencies {
            val cpuCoresNumber = calcCpuCoreNumber()
            val allCoresFrequencies = AllCoresFrequencies()

            for (i in 0 until cpuCoresNumber) {
                allCoresFrequencies.frequencies[i] = takeCurrentCpuFreq(i)
                allCoresFrequencies.maxFrequencies[i] = takeMaxCpuFreq(i)
                allCoresFrequencies.minFrequencies[i] = takeMinCpuFreq(i)
            }

            return allCoresFrequencies
        }

        fun takeMaxCoresFrequencies() : IntArray {
            val cpuCoresNumber = calcCpuCoreNumber()
            val maxCoresFrequencies = IntArray(cpuCoresNumber)

            for (i in 0 until cpuCoresNumber) {
                maxCoresFrequencies[i] = takeMaxCpuFreq(i)
            }

            return maxCoresFrequencies
        }

        fun takeMinCoresFrequencies() : IntArray {
            val cpuCoresNumber = calcCpuCoreNumber()
            val minCoresFrequencies = IntArray(cpuCoresNumber)

            for (i in 0 until cpuCoresNumber) {
                minCoresFrequencies[i] = takeMinCpuFreq(i)
            }

            return minCoresFrequencies
        }

        fun takeCurrentCoresFrequencies() : IntArray {
            val cpuCoresNumber = calcCpuCoreNumber()
            val currentCoresFrequencies = IntArray(cpuCoresNumber)


            for (i in 0 until cpuCoresNumber) {
                currentCoresFrequencies[i] = takeCurrentCpuFreq(i)
            }

            return currentCoresFrequencies
        }

        private fun readIntegerFile(filePath: String): Int {

            try {
                BufferedReader(
                    InputStreamReader(FileInputStream(filePath)), 1000).use { reader ->

                    val line = reader.readLine()
                    return Integer.parseInt(line)
                }

            } catch (e: Exception) {
                Timber.e(e)

                return 0
            }

        }

        /**
         * /proc/stat Get CPU value of each core from
         *
         * @return List of CPU usage (from 0 to 1)
         */
        fun takeCpuUsageSnapshot(): DoubleArray {

            // [0] is the whole, [1] and later are individual CPUs
            val cpusUsageInPercent = mutableListOf<Double>()

            val process = ProcessBuilder("cat", "/proc/stat")//array as vararg
                .start()
            val cpuInfoString = process.inputStream.getTextAndClose()

            for (line in cpuInfoString.split('\n')) {
                if (!line.startsWith("cpu")) {
                    continue
                }

                //     user     nice    system  idle    iowait  irq     softirq     steal
                //cpu  48200 4601 35693 979258 5095 1 855 0 0 0
                //cpu0 26847 1924 25608 212324 2212 1 782 0 0 0
                //cpu1 8371 1003 4180 254096 1026 0 50 0 0 0
                //cpu2 8450 983 3916 252872 1304 0 9 0 0 0
                //cpu3 4532 691 1989 259966 553 0 14 0 0 0

                val tokens = line.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val idle = java.lang.Long.parseLong(tokens[4])
                val ioWait = java.lang.Long.parseLong(tokens[5])
                val total = (java.lang.Long.parseLong(tokens[1])
                        + java.lang.Long.parseLong(tokens[2])
                        + java.lang.Long.parseLong(tokens[3])
                        + idle
                        + ioWait
                        + java.lang.Long.parseLong(tokens[6])
                        + java.lang.Long.parseLong(tokens[7]))

                val percentUsage = (total - idle - ioWait).toDouble() / total
                cpusUsageInPercent.add(percentUsage)
            }

            cpusUsageInPercent.removeAt(0) //first element is total cpu info

            return cpusUsageInPercent.toDoubleArray()
        }
    }
}
