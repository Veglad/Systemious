package com.example.systemious.data.repository

object Repository : ComponentsInfoStorageContract{

    const val TIME_CHECKING_INTERVAL = 1000

    fun init(timeCheckingIntervalInMs: Int, coresNumber: Int) {
        ComponentsInfoStorage.init(timeCheckingIntervalInMs, coresNumber)
    }

    override fun saveMemoryUsage(memoryUsage: Float) {
        ComponentsInfoStorage.saveMemoryUsage(memoryUsage)
    }

    override fun saveCoresUsage(coresUsage: MutableList<Float>) {
        ComponentsInfoStorage.saveCoresUsage(coresUsage)
    }

    override fun forceSave() {
        ComponentsInfoStorage.forceSave()
    }

    override fun clearStorage() {
        ComponentsInfoStorage.clearStorage()
    }
}