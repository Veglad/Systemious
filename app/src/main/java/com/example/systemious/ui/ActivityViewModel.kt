package com.example.systemious.ui

import android.app.Application
import android.content.*
import androidx.lifecycle.AndroidViewModel
import com.example.systemious.App
import android.util.Log
import android.content.Context.BIND_AUTO_CREATE
import com.example.systemious.ui.SystemInfoService.LocalBinder
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import android.content.IntentFilter
import com.example.systemious.utils.Constants


class ActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var isBound = false
    set(value) {
        field = value
        if (!value) {
            _isSystemServiceWorking.value = false
        }
    }
    private lateinit var systemService: SystemInfoService

    private var _isSystemServiceWorking = MutableLiveData<Boolean>().apply { value = false }
    val isSystemServiceWorking: MutableLiveData<Boolean>
    get() = _isSystemServiceWorking

    private val systemServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as LocalBinder
            systemService = binder.service
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
            stopSystemService()
        }
    }

    private val systemInfoBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            systemInfoMessageReceived(intent)
        }
    }

    init {
        startSystemService()
        bindToSystemService()
        registerSystemInfoBroadcastReceiver()
    }

    private fun startSystemService() {
        getApplication<App>().startService(Intent(getApplication(), SystemInfoService::class.java).apply {
            action = SystemInfoService.ACTION_START_FOREGROUND_SERVICE
        })
    }

    private fun bindToSystemService() {
        val intent = Intent(getApplication(), SystemInfoService::class.java)
        getApplication<App>().bindService(intent, systemServiceConnection, BIND_AUTO_CREATE)
    }

    private fun registerSystemInfoBroadcastReceiver() {
        val intentFilter = IntentFilter(Constants.SYSTEM_INFO_BROADCAST_RECEIVER_ACTION)
        getApplication<App>().registerReceiver(systemInfoBroadcastReceiver, intentFilter)
    }

    private fun systemInfoMessageReceived(intent: Intent?) {
        intent?.extras?.let {bundle ->
            val workingCode: Int = bundle.getInt(Constants.SYSTEM_INFO_BROADCAST_RECEIVER_WORKING_CODE_KEY)
            when (workingCode) {
                Constants.SYSTEM_INFO_BROADCAST_RECEIVER_START_CODE -> _isSystemServiceWorking.value = true
                Constants.SYSTEM_INFO_BROADCAST_RECEIVER_STOP_CODE -> _isSystemServiceWorking.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        unBoundSystemService() // Also stops service
        Log.d("FOREGROUND", "onCleared")
    }

    private fun unBoundSystemService() {
        if (isBound) {
            // Also stops service in onServiceDisconnected callback
            getApplication<App>().unbindService(systemServiceConnection)
            isBound = false
        }
    }

    private fun stopSystemService() {
        getApplication<App>().startService(Intent(getApplication(), SystemInfoService::class.java).apply {
            action = SystemInfoService.ACTION_STOP_FOREGROUND_SERVICE
        })
    }
}