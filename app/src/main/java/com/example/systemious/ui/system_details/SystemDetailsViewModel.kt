package com.example.systemious.ui.system_details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.systemious.App
import com.example.systemious.data.SystemInfoManager
import com.example.systemious.ui.system_details.entities.ParameterTypes
import com.example.systemious.ui.system_details.entities.SystemParameter
import kotlinx.coroutines.*

class SystemDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val job: Job = Job()
    private val uiCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + job)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _systemDetailsList = MutableLiveData<MutableList<SystemParameter>>()
    val systemDetailsList: LiveData<MutableList<SystemParameter>>
        get() = _systemDetailsList

    private val app = getApplication<App>()

    init {
        loadSystemDetails()
    }

    fun loadSystemDetails() {
        uiCoroutineScope.launch {
            _isLoading.value = true
            try {
                _systemDetailsList.value = withContext(Dispatchers.Default) { loadParameterList() }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadParameterList() : MutableList<SystemParameter> {
        val systemParameterList = mutableListOf<SystemParameter>()

        systemParameterList.add(SystemParameter("Device name", SystemInfoManager.deviceName, ParameterTypes.DEVICE))
        systemParameterList.add(SystemParameter("Model", SystemInfoManager.deviceModel, ParameterTypes.DEVICE))
        systemParameterList.add(SystemParameter("Serial number", SystemInfoManager.serialNumber, ParameterTypes.OS))
        systemParameterList.add(SystemParameter("Radio version", SystemInfoManager.radioVersion, ParameterTypes.OS))
        systemParameterList.add(SystemParameter("Screen resolution", SystemInfoManager.getScreenResolution(app), ParameterTypes.OS))
        systemParameterList.add(SystemParameter("Android version", SystemInfoManager.androidVersion))

        systemParameterList.sortBy { it.parameterType }
        return systemParameterList
    }
}
