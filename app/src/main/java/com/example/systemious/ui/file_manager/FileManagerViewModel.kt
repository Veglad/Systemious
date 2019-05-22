package com.example.systemious.ui.file_manager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import java.io.File

class FileManagerViewModel(application: Application) : AndroidViewModel(application) {

    private val job: Job = Job()
    private val uiCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + job)

    private var _currentPath = MutableLiveData<String>().apply { value = "storage" }
    val currentPath: LiveData<String> = _currentPath

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _fileItemList = MutableLiveData<MutableList<FileItem>>()
    val fileItemList: LiveData<MutableList<FileItem>> = _fileItemList

    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> = _error

    init {
        uiCoroutineScope.launch {
            _isLoading.value = true
            try {
                _fileItemList.value = withContext(Dispatchers.IO) { loadFileItems(_currentPath.value) }
            } catch (ex: Exception) {
                _error.value = ex
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadFileItems(path: String?): MutableList<FileItem> {
        path?.let {
            val rootDir = File(path)
            if (!rootDir.canRead()) 1//inaccessible

            val fileList = mutableListOf<FileItem>()
            for (file in rootDir.list()) {
                if (!file.startsWith(".")) {
                    fileList.add(FileItem(name = file))
                }
            }

            fileList.sortBy { it.name.compareTo(it.name) }
            return fileList
        } ?: return mutableListOf()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
