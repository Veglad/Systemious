package com.example.systemious.data.repository

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


data class CpuCoresUsageInterval(
    var coresUsages: RealmList<CoreUsages> = RealmList(),
    var startIntervalDateInMs: Long = 0,
    var timeCheckingOffsetInMs: Int = 1000
) : RealmObject()

data class CoreUsages(
    @PrimaryKey
    var id: Short = 0,
    var usages: RealmList<Float> = RealmList()
)

data class ramUsageInterval(
    var ramUsages: RealmList<Float> = RealmList(),
    var startIntervalDateInMs: Long = 0,
    var timeCheckingOffsetInMs: Int = 1000
)