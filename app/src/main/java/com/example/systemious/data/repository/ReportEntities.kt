package com.example.systemious.data.repository

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*


open class CpuCoresUsageInterval(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var coresUsages: RealmList<CoreUsages> = RealmList(),
    var startIntervalDateInMs: Long = 0,
    var timeCheckingIntervalInMs: Int = 1000
) : RealmObject() {
    companion object {
        fun copyOf(cpuCoresUsageInterval: CpuCoresUsageInterval) : CpuCoresUsageInterval {

            val cpuUsages: RealmList<CoreUsages> = RealmList()
            for (usage in cpuCoresUsageInterval.coresUsages) {
                cpuUsages.add(CoreUsages.copyOf(usage))
            }

            return CpuCoresUsageInterval(

                cpuCoresUsageInterval.id,
                cpuUsages,
                cpuCoresUsageInterval.startIntervalDateInMs,
                cpuCoresUsageInterval.timeCheckingIntervalInMs
            )
        }
    }
}

open class CoreUsages(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var usages: RealmList<Float> = RealmList()
) : RealmObject() {
    companion object {
        fun copyOf(coreUsages: CoreUsages): CoreUsages {

            val usages = RealmList<Float>()

            for (usage in coreUsages.usages) {
                usages.add(usage)
            }

            return CoreUsages(
                coreUsages.id,
                usages
            )
        }
    }
}

open class RamUsageInterval(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var ramUsages: RealmList<Float> = RealmList(),
    var startIntervalDateInMs: Long = 0,
    var timeCheckingIntervalInMs: Int = 1000
) : RealmObject()