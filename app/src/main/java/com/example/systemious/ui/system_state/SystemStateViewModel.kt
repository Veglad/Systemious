package com.example.systemious.ui.system_state

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.systemious.App
import com.example.systemious.data.SystemInfoManager
import com.example.systemious.data.repository.Repository
import com.example.systemious.data.SystemInfoService
import com.example.systemious.utils.Constants
import java.util.*

class SystemStateViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val MAX_MEMORY_CHART_POINTS_NUMBER = 100
        private const val MAX_CPU_CHART_POINTS_NUMBER = 100
    }

    private var _ramUsed = MutableLiveData<Queue<Double>>().apply { value = LinkedList<Double>() }
    val ramUsed: MutableLiveData<Queue<Double>>
        get() = _ramUsed

    private var _cpuUsages = MutableLiveData<MutableList<Queue<Double>>>()
    val cpuUsages: MutableLiveData<MutableList<Queue<Double>>>
        get() = _cpuUsages

    private var _coresNumber = MutableLiveData<Int>().apply { value = SystemInfoManager.coresNumber }
    val coresNumber: MutableLiveData<Int>
        get() = _coresNumber

    private var _maxRamCapacity =
        MutableLiveData<Long>().apply { value = SystemInfoManager.getRamCapacity(getApplication()) }
    val maxRamCapacity: MutableLiveData<Long>
        get() = _maxRamCapacity

    private var _batteryPercentage = MutableLiveData<Int>().apply { value = 0 }
    val batteryPercentage: MutableLiveData<Int>
        get() = _batteryPercentage

    private val systemInfoBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            systemInfoMessageReceived(intent)
        }
    }

    private val mBatInfoReceiver = object : BroadcastReceiver() {
        override fun onReceive(arg0: Context, intent: Intent) {
            batteryPercentage.value = intent.getIntExtra("level", 0)
        }
    }

    init {
        registerSystemInfoReceiver()
        registerBatteryReceiver()
        initCpuUsagesQueues()
        Repository.init(Repository.TIME_CHECKING_INTERVAL, SystemInfoManager.coresNumber)
    }

    private fun initCpuUsagesQueues() {
        _cpuUsages.value = mutableListOf()
        val coresNumber = _coresNumber.value ?: return
        for (i in 0 until coresNumber) {
            _cpuUsages.value?.add(LinkedList<Double>())
        }
    }

    private fun registerBatteryReceiver() {
        getApplication<App>().registerReceiver(
            this.mBatInfoReceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
    }

    private fun registerSystemInfoReceiver() {
        val intentFilter = IntentFilter(Constants.SYSTEM_INFO_DETAILS_BROADCAST_RECEIVER_ACTION)
        getApplication<App>().registerReceiver(systemInfoBroadcastReceiver, intentFilter)
    }

    private fun systemInfoMessageReceived(intent: Intent?) {
        intent?.extras?.let { bundle ->
            bundle.getLong(SystemInfoService.RAM_INFO_KEY).also { usedRam ->
                saveRamUsageToStorage(usedRam)
                updateRamGraphicPoints(usedRam)
            }
            bundle.getDoubleArray(SystemInfoService.CPU_CURRENT_USAGE_KEY)?.let { coreUsageList ->
                saveCoreListOfUsageToStorage(coreUsageList)
                updateCpuGraphicPoints(coreUsageList)
            }
        }
    }

    private fun saveRamUsageToStorage(usedRam: Long) {
        val ramCapacity = SystemInfoManager.getRamCapacity(getApplication())
        Repository.saveMemoryUsage(usedRam.toFloat() / ramCapacity)
    }

    private fun updateCpuGraphicPoints(coreUsageList: DoubleArray) {
        val queueUsageList = _cpuUsages.value ?: return
        for (i in 0 until queueUsageList.size) {
            if (queueUsageList[i].size == MAX_CPU_CHART_POINTS_NUMBER) {
                queueUsageList[i].remove()
                queueUsageList[i].offer(coreUsageList[i])
            } else {
                queueUsageList[i].offer(coreUsageList[i])
            }
        }

        _cpuUsages.value = queueUsageList
    }

    private fun saveCoreListOfUsageToStorage(coreUsageList: DoubleArray) {
        val coreListOfUsage = mutableListOf<Float>()

        for (i in 0 until coreUsageList.size) {
            coreListOfUsage.add(coreUsageList[i].toFloat())
        }

        Repository.saveCoresUsage(coreListOfUsage)
    }

    private fun updateRamGraphicPoints(ramAvailableNumber: Long) {
        val normalizedNumber = ramAvailableNumber / 1048576 //1024 * 1024
        if (_ramUsed.value?.size == MAX_MEMORY_CHART_POINTS_NUMBER) {
            _ramUsed.value?.remove()
            _ramUsed.value?.offer(normalizedNumber.toDouble())
        } else {
            _ramUsed.value?.offer(normalizedNumber.toDouble())
        }
        _ramUsed.value = LinkedList(_ramUsed.value)
    }

    override fun onCleared() {
        super.onCleared()
        getApplication<App>().unregisterReceiver(systemInfoBroadcastReceiver)
    }
}
