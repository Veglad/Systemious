package com.example.systemious.ui

import android.app.Application
import android.content.*
import androidx.lifecycle.AndroidViewModel
import com.example.systemious.App
import android.util.Log
import android.content.Context.BIND_AUTO_CREATE
import com.example.systemious.data.SystemInfoService.LocalBinder
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import com.example.systemious.data.SystemInfoService
import com.example.systemious.data.repository.Repository
import com.example.systemious.utils.Constants
import kotlinx.coroutines.*
import java.io.File
import java.lang.Exception


class ActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var isBound = false
        set(value) {
            field = value
            if (!value) {
                _isSystemServiceWorking.value = false
            }
        }

    private val job: Job = Job()
    private val uiCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var systemService: SystemInfoService
    private var _isSystemServiceWorking = MutableLiveData<Boolean>().apply { value = false }

    val isSystemServiceWorking: MutableLiveData<Boolean>
        get() = _isSystemServiceWorking

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> = _error

    private var _reportCsvFile = MutableLiveData<File?>()
    val reportCsvFile: MutableLiveData<File?> = _reportCsvFile

    private val systemServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            val binder = binder as LocalBinder
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
        intent?.extras?.let { bundle ->
            val workingCode: Int = bundle.getInt(Constants.SYSTEM_INFO_BROADCAST_RECEIVER_WORKING_CODE_KEY)
            when (workingCode) {
                Constants.SYSTEM_INFO_BROADCAST_RECEIVER_START_CODE -> _isSystemServiceWorking.value = true
                Constants.SYSTEM_INFO_BROADCAST_RECEIVER_STOP_CODE -> _isSystemServiceWorking.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        unBoundSystemService()
        stopSystemService()
        getApplication<App>().unregisterReceiver(systemInfoBroadcastReceiver)
        Repository.forceSave()
        Log.d("FOREGROUND", "onCleared")
    }

    private fun unBoundSystemService() {
        if (isBound) {
            getApplication<App>().unbindService(systemServiceConnection)
            isBound = false
        }
    }

    private fun stopSystemService() {
        getApplication<App>().startService(Intent(getApplication(), SystemInfoService::class.java).apply {
            action = SystemInfoService.ACTION_STOP_FOREGROUND_SERVICE
        })
    }

    fun toggleServiceState() {
        if (_isSystemServiceWorking.value == true) {
            unBoundSystemService()
            stopSystemService()
            Repository.forceSave()
        } else {
            startSystemService()
            bindToSystemService()
        }
    }

    fun clearStorage() {
        Repository.clearStorage()
    }

    fun makeReport() {
        uiCoroutineScope.launch {
            _isLoading.value = true
            try {
                _reportCsvFile.value = withContext(Dispatchers.IO) {
                    Repository.makeReport(getApplication())
                }
            } catch (ex: Exception) {
                _error.value = ex
            } finally {
                _isLoading.value = false
            }
        }
    }
}