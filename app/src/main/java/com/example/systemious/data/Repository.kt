package com.example.systemious.data

object Repository {
    private var cpuCoresNumber = 0

    private var coresMaxFrequencies = IntArray(0)

    private var coresMinFrequencies = IntArray(0)

    fun getCpuCoresNumber(): Int {
        return cpuCoresNumber
    }

    fun getCoresMaxFrequencies(): IntArray {
        return coresMaxFrequencies
    }

    fun getCoresMinFrequencies(): IntArray {
        return coresMinFrequencies
    }

    fun saveCpuCoresNumber(cpuCoresNumber: Int) {
        this.cpuCoresNumber = cpuCoresNumber
    }

    fun saveCoresMaxFrequencies(coresMaxFrequencies: IntArray) {
        this.coresMaxFrequencies = coresMaxFrequencies
    }

    fun saveCoresMinFrequencies(coresMinFrequencies: IntArray) {
        this.coresMinFrequencies = coresMinFrequencies
    }
}