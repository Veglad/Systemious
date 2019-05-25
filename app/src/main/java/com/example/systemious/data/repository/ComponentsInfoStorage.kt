package com.example.systemious.data.repository

import android.content.Context
import android.net.Uri
import com.example.systemious.data.createReportFile
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object ComponentsInfoStorage : ComponentsInfoStorageContract {

    private const val MAX_INTERVAL_ITEMS_SIZE = 100
    private var timeCheckingIntervalInMs = 1000

    private var coresUsages: MutableList<MutableList<Float>> = mutableListOf()
    private var coresUsagesStartIntervalDate: Date = Date()
    private var memoryUsages: MutableList<Float> = mutableListOf()
    private var memoryUsagesStartIntervalDate: Date = Date()
    private var coresNumber: Int = 1

    fun init(timeCheckingIntervalInMs: Int, coresNumber: Int) {
        this.timeCheckingIntervalInMs = timeCheckingIntervalInMs
        this.coresNumber = coresNumber
        for (i in 0 until coresNumber) {
            coresUsages.add(mutableListOf())
        }
    }

    /**
     * coresUsage - list of usages, where each usage is specific for each core.
     * item position equals to core number
     */
    override fun saveCoresUsage(coresUsage: MutableList<Float>) {
        // if new interval, save start interval date
        if (coresUsages[0].isEmpty()) {
            coresUsagesStartIntervalDate = Date()
        }

        for (i in 0 until coresUsage.size) {
            coresUsages[i].add(coresUsage[i])
        }

        if (coresUsages[0].size >= MAX_INTERVAL_ITEMS_SIZE) {
            forceSaveCpuInfo()
        }
    }

    private fun forceSaveCpuInfo() {
        saveCoreUsagesToStorage(coresUsages, timeCheckingIntervalInMs, coresUsagesStartIntervalDate.time)
        coresUsages = mutableListOf()
        for (i in 0 until coresNumber) {
            coresUsages.add(mutableListOf())
        }
    }

    private fun saveCoreUsagesToStorage(
        coresUsages: MutableList<MutableList<Float>>,
        timeCheckingIntervalInMs: Int,
        startIntervalDateInMs: Long
    ) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransactionAsync { transactionRealm ->
                val cpuCoresUsageInterval =
                    transactionRealm.createObject(CpuCoresUsageInterval::class.java, UUID.randomUUID().toString())
                cpuCoresUsageInterval.startIntervalDateInMs = startIntervalDateInMs
                cpuCoresUsageInterval.timeCheckingIntervalInMs = timeCheckingIntervalInMs

                val coreUsagesList = RealmList<CoreUsages>()

                for (i in 0 until coresUsages.size) {
                    val coreUsages = transactionRealm.createObject(CoreUsages::class.java, UUID.randomUUID().toString())

                    for (usage in coresUsages[i]) {
                        //val managedUsage = transactionRealm.copyFromRealm(Usage::class.java)
                        //managedUsage.usage = usage
                        coreUsages.usages.add(usage)
                    }
                    coreUsagesList.add(coreUsages)
                }

                cpuCoresUsageInterval.coresUsages = coreUsagesList
                transactionRealm.insertOrUpdate(cpuCoresUsageInterval)
            }
        }
    }

    override fun saveMemoryUsage(memoryUsage: Float) {
        // if new interval, save start interval date
        if (memoryUsages.isEmpty()) {
            memoryUsagesStartIntervalDate = Date()
        }
        memoryUsages.add(memoryUsage)

        if (memoryUsages.size >= MAX_INTERVAL_ITEMS_SIZE) {
            forceSaveRam()
        }
    }

    private fun forceSaveRam() {
        saveMemoryUsagesToStorage(memoryUsages, timeCheckingIntervalInMs, memoryUsagesStartIntervalDate.time)
        memoryUsages = mutableListOf()
    }

    private fun saveMemoryUsagesToStorage(
        memoryUsages: MutableList<Float>,
        timeCheckingIntervalInMs: Int,
        startIntervalDateInMs: Long
    ) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransactionAsync { transactionRealm ->
                val memoryUsage =
                    transactionRealm.createObject(RamUsageInterval::class.java, UUID.randomUUID().toString())
                memoryUsage.startIntervalDateInMs = startIntervalDateInMs
                memoryUsage.timeCheckingIntervalInMs = timeCheckingIntervalInMs

                for (usage in memoryUsages) {
                    //val managedUsage = transactionRealm.createObject(Usage::class.java)
                    //managedUsage.usage = usage
                    memoryUsage.ramUsages.add(usage)
                }

                transactionRealm.insertOrUpdate(memoryUsage)
            }
        }
    }

    override fun forceSave() {
        forceSaveRam()
        forceSaveCpuInfo()
    }

    override fun clearStorage() {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransactionAsync { transactionRealm ->
                transactionRealm.deleteAll()
            }
        }
    }

    fun makeReport(context: Context): File? {
        var fileResult: File? = null
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction { transactionRealm ->
                val file = createReportFile(context)
                val cpuCoresUsageIntervals = transactionRealm.where(CpuCoresUsageInterval::class.java).findAll()
                //Needs mapping for Realm results
                for (interval in cpuCoresUsageIntervals) {
                    writeCpuCoresUsagesIntervalsToFile(CpuCoresUsageInterval.copyOf(interval), file)
                }

                val ramUsageIntervals = transactionRealm.where(RamUsageInterval::class.java).findAll()
                writeRamUsagesIntervalsToFile(ramUsageIntervals, file)
                fileResult = file
            }
        }
        return fileResult
    }

    private fun writeCpuCoresUsagesIntervalsToFile(
        cpuInterval: CpuCoresUsageInterval,
        file: File
    ) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss Z")
        FileWriter(file, true).use { fileWriter ->
            fileWriter.write("CPU Cores intervals\n")
            val dateString = dateFormat.format(cpuInterval.startIntervalDateInMs)
            fileWriter.write("$dateString, tracking period - ${cpuInterval.timeCheckingIntervalInMs}ms\nCPU usage %\n")

            for (i in 0 until cpuInterval.coresUsages.size) {
                fileWriter.write("core $i\n")
                //Write usages (percentage)
                cpuInterval.coresUsages[i]?.let { core ->
                    for (j in 0 until core.usages.size) {
                        val coreUsage = core.usages[j]//?.usage
                        fileWriter.write("$coreUsage\n")
                    }
                }
            }
            fileWriter.write("----------------------------------\n")
        }
    }

    private fun writeRamUsagesIntervalsToFile(ramUsageIntervals: RealmResults<RamUsageInterval>?, file: File) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss Z")
        if (ramUsageIntervals == null) return
        FileWriter(file, true).use { fileWriter ->
            fileWriter.write("RAM intervals\n")
            for (interval in ramUsageIntervals) {
                val dateString = dateFormat.format(interval.startIntervalDateInMs)
                fileWriter.write("$dateString, tracking period - ${interval.timeCheckingIntervalInMs}ms\nRAM usage %\n")

                for (i in 0 until interval.ramUsages.size) {
                    fileWriter.write("${interval.ramUsages[i]/*?.usage*/}\n")
                }
            }
            fileWriter.write("----------------------------------\n")
        }
    }
}

interface ComponentsInfoStorageContract {
    fun saveMemoryUsage(memoryUsage: Float)
    fun saveCoresUsage(coresUsage: MutableList<Float>)
    fun forceSave()
    fun clearStorage()
}