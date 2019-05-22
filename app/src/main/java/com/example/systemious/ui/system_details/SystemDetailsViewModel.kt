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
import java.text.DecimalFormat

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
        val context = getApplication<App>()

        systemParameterList.add(SystemParameter("Device name", SystemInfoManager.deviceName, ParameterTypes.DEVICE))
        systemParameterList.add(SystemParameter("Model", SystemInfoManager.deviceModel, ParameterTypes.DEVICE))
        systemParameterList.add(SystemParameter("Screen dpi",
            SystemInfoManager.getScreeenDensity(context).toString() + "dpi", ParameterTypes.DEVICE))
        systemParameterList.add(SystemParameter("CPU", SystemInfoManager.cpuShortDescription, ParameterTypes.CPU))
        systemParameterList.add(SystemParameter("CPU architecture", SystemInfoManager.cpuArchitecture, ParameterTypes.CPU))
        systemParameterList.add(SystemParameter("RAM capacity",
            formatRamCapacity(SystemInfoManager.getRamCapacity(context)), ParameterTypes.CPU))
        systemParameterList.add(SystemParameter("Internal Storage", SystemInfoManager.internalStorageMemory, ParameterTypes.MEMORY))
        systemParameterList.add(SystemParameter("External Storage",
            SystemInfoManager.externalStoragMemory ?: "Not available", ParameterTypes.MEMORY))
        systemParameterList.add(SystemParameter("Serial number", SystemInfoManager.serialNumber, ParameterTypes.OS))
        systemParameterList.add(SystemParameter("Radio version", SystemInfoManager.radioVersion, ParameterTypes.OS))
        systemParameterList.add(SystemParameter("Screen resolution", SystemInfoManager.getScreenResolution(app), ParameterTypes.OS))
        systemParameterList.add(SystemParameter("Android version", SystemInfoManager.androidVersion, ParameterTypes.OS))

        val sensors = SystemInfoManager.getSensorslist(context)
        sensors?.let {
            for (sensor in sensors) {
                systemParameterList.add(SystemParameter(sensor.name, sensor.vendor, ParameterTypes.SENSORS))
            }
        }

        systemParameterList.sortBy { it.parameterType }
        return systemParameterList
    }

    private fun formatRamCapacity(capacity: Long): String {
        val normalizedNumber = capacity/ 1073741824 //1024 * 1024 * 1024
        return DecimalFormat("#.#").format(normalizedNumber) + " Gb"
    }
}
