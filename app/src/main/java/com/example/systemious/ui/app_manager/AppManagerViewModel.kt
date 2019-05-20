package com.example.systemious.ui.app_manager

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.systemious.App
import com.example.systemious.ui.app_manager.entities.AppInfo
import kotlinx.coroutines.*
import java.io.File

class AppManagerViewModel(application: Application) : AndroidViewModel(application) {

    private val job: Job = Job()
    private val uiCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + job)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _appsInfoList = MutableLiveData<MutableList<AppInfo>>()
    val appsInfoList: LiveData<MutableList<AppInfo>>
        get() = _appsInfoList

    init {
        loadAppInfo()
    }

    fun loadAppInfo() {
        uiCoroutineScope.launch {
            _isLoading.value = true
            try {
                _appsInfoList.value = withContext(Dispatchers.Default) {
                    val packageManager = getApplication<App>().packageManager
                    val appsInflist = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

                    val appInfoList = mutableListOf<AppInfo>()
                    for (appInfo in appsInflist) {
                        AppInfo().apply {
                            icon = appInfo.loadIcon(packageManager)
                            appName = appInfo.loadLabel(packageManager).toString()
                            packageName = appInfo.packageName
                            val appSizeInBytes = getApkSize(getApplication(), appInfo.packageName)
                            sizeInMb = appSizeInBytes.toDouble() / 1048576
                            appInfoList.add(this)
                        }
                    }
                    appInfoList
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getApkSize(context: Context, packageName: String): Long {
        return File(context.packageManager.getApplicationInfo(packageName, 0).publicSourceDir).length()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
