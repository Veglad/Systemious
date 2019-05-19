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
import com.example.systemious.ui.SystemInfoService
import com.example.systemious.utils.Constants
import java.util.*

class SystemStateViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val MAX_MEMORY_CHART_POINTS_NUMBER = 100
    }

    private var _ramCapacity = MutableLiveData<Long>().apply { value = SystemInfoManager.getRamCapacity(getApplication()) }
    val ramCapacity: MutableLiveData<Long>
        get() = _ramCapacity

    private var _ramUsed = MutableLiveData<Queue<Long>>().apply { value = LinkedList<Long>() }
    val ramUsed: MutableLiveData<Queue<Long>>
        get() = _ramUsed

    private var _coresNumber = MutableLiveData<Int>().apply { value = SystemInfoManager.coresNumber }
    val coresNumber: MutableLiveData<Int>
        get() = _coresNumber

    private var _maxRamCapacity = MutableLiveData<Long>().apply { value = SystemInfoManager.getRamCapacity(getApplication()) }
    val maxRamCapacity: MutableLiveData<Long>
        get() = _maxRamCapacity

    private var _cpuUsages = MutableLiveData<DoubleArray>().apply { value = DoubleArray(0) }
    val cpuUsages: MutableLiveData<DoubleArray>
        get() = _cpuUsages

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
    }

    private fun registerBatteryReceiver() {
        getApplication<App>().registerReceiver(this.mBatInfoReceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    private fun registerSystemInfoReceiver() {
        val intentFilter = IntentFilter(Constants.SYSTEM_INFO_DETAILS_BROADCAST_RECEIVER_ACTION)
        getApplication<App>().registerReceiver(systemInfoBroadcastReceiver, intentFilter)
    }

    private fun systemInfoMessageReceived(intent: Intent?) {
        intent?.extras?.let {bundle ->
            addRamInfoValue(bundle.getLong(SystemInfoService.RAM_INFO_KEY))
            _cpuUsages.value = bundle.getDoubleArray(SystemInfoService.CPU_CURRENT_USAGE_KEY)
        }
    }

    private fun addRamInfoValue(ramAvailableNumber: Long) {
        val normalizedNumber = ramAvailableNumber/ 1048576 //1024 * 1024
        if (_ramUsed.value?.size == MAX_MEMORY_CHART_POINTS_NUMBER) {
            _ramUsed.value?.remove()
            _ramUsed.value?.offer(normalizedNumber)
            _ramUsed.value = LinkedList(_ramUsed.value)
        } else {
            _ramUsed.value?.offer(normalizedNumber)
            _ramUsed.value = LinkedList(_ramUsed.value)
        }
    }

    override fun onCleared() {
        super.onCleared()
        getApplication<App>().unregisterReceiver(systemInfoBroadcastReceiver)
    }
}
