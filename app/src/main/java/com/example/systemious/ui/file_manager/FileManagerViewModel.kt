package com.example.systemious.ui.file_manager

import android.app.Application
import android.content.Intent
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.systemious.data.deleteFile
import com.example.systemious.data.getOpenFileIntent
import com.example.systemious.data.loadFileItems
import com.example.systemious.ui.Event
import com.example.systemious.ui.EventWithContent
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File

class FileManagerViewModel(application: Application) : AndroidViewModel(application) {

    private val job: Job = Job()
    private val uiCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + job)
    private var rootDirectory = Environment.getExternalStorageDirectory().absolutePath

    private var _currentPath = MutableLiveData<String>().apply { value = rootDirectory }
    val currentPath: LiveData<String> = _currentPath

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _fileItemList = MutableLiveData<MutableList<FileItem>>()
    val fileItemList: LiveData<MutableList<FileItem>> = _fileItemList

    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> = _error

    private val _openFileIntent = MutableLiveData<EventWithContent<Intent>>()
    val openFileIntent: LiveData<EventWithContent<Intent>> = _openFileIntent

    private val _grantPermission = MutableLiveData<Event>()
    val grantPermission: LiveData<Event> = _grantPermission

    private val _deletedFile = MutableLiveData<EventWithContent<FileItem>>()
    val deletedFile: LiveData<EventWithContent<FileItem>> = _deletedFile

    init {
        if(checkIfCanLoadFiles()) {
            loadFiles()
        } else {
            _grantPermission.value = Event()
        }
    }

    private fun checkIfCanLoadFiles(): Boolean {
        val rootFiles = Environment.getExternalStorageDirectory().listFiles()
        return rootFiles != null && rootFiles.isNotEmpty()
    }

    fun loadFiles() {
        uiCoroutineScope.launch {
            _isLoading.value = true
            try {
                val files = withContext(Dispatchers.IO) { loadFileItems(_currentPath.value) }
                if (_currentPath.value != rootDirectory) {
                    files.add(0, FileItem(name = "..", type = FileType.PARENT_FOLDER))
                }
                _fileItemList.value = files
            } catch (ex: Exception) {
                _error.value = ex
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun openSelectedItem(fileItem: FileItem) {
        when {
            fileItem.type == FileType.PARENT_FOLDER -> {
                _currentPath.value = File(_currentPath.value).parent
                loadFiles()
            }
            fileItem.type == FileType.DIRECTORY -> {
                _currentPath.value = _currentPath.value + File.separator + fileItem.name
                loadFiles()
            }
            else -> _currentPath.value?.let { path ->
                val intent = getOpenFileIntent(File("$path/${fileItem.name}"), getApplication())
                intent?.let {
                    _openFileIntent.value = EventWithContent(intent)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun deleteItem(fileItem: FileItem) {
        _currentPath.value?.let { path ->
            try {
                deleteFile(path, fileItem)
                _deletedFile.value = EventWithContent(fileItem)
            } catch (ex: Exception) {
                Timber.e("Delete file error: ${ex.message}")
            }
        }
    }
}
