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
) : RealmObject()

open class CoreUsages(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var usages: RealmList<Usage> = RealmList()
) : RealmObject()

open class RamUsageInterval(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var ramUsages: RealmList<Usage> = RealmList(),
    var startIntervalDateInMs: Long = 0,
    var timeCheckingIntervalInMs: Int = 1000
) : RealmObject()

open class Usage(
    var usage: Float = 0f
) : RealmObject()