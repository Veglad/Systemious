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
import com.example.systemious.domain.RamInfo
import com.example.systemious.ui.SystemInfoService
import com.example.systemious.utils.Constants

class SystemStateViewModel(application: Application) : AndroidViewModel(application) {
    private var _ramInfo = MutableLiveData<RamInfo>().apply { value = RamInfo() }
    val ramInfo: MutableLiveData<RamInfo>
        get() = _ramInfo

    private var _coresNumber = MutableLiveData<Int>().apply { value = SystemInfoManager.coresNumber }
    val coresNumber: MutableLiveData<Int>
        get() = _coresNumber

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
            _ramInfo.value = bundle.getParcelable(SystemInfoService.RAM_INFO_KEY)
            _cpuUsages.value = bundle.getDoubleArray(SystemInfoService.CPU_CURRENT_USAGE_KEY)
        }
    }

    override fun onCleared() {
        super.onCleared()
        getApplication<App>().unregisterReceiver(systemInfoBroadcastReceiver)
    }
}
