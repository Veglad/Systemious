package com.example.systemious.data.repository

import android.content.Context
import android.content.Intent
import com.example.systemious.ui.file_manager.FileItem
import java.io.File

object Repository : ComponentsInfoStorageContract{

    const val TIME_CHECKING_INTERVAL = 1000

    fun init(timeCheckingIntervalInMs: Int, coresNumber: Int) {
        ComponentsInfoStorage.init(timeCheckingIntervalInMs, coresNumber)
    }

    override fun saveMemoryUsage(memoryUsage: Float) {
        ComponentsInfoStorage.saveMemoryUsage(memoryUsage)
    }

    override fun saveCoresUsage(coresUsage: MutableList<Float>) {
        ComponentsInfoStorage.saveCoresUsage(coresUsage)
    }

    override fun forceSave() {
        ComponentsInfoStorage.forceSave()
    }

    override fun clearStorage() {
        ComponentsInfoStorage.clearStorage()
    }

    fun makeReport(context: Context): File? = ComponentsInfoStorage.makeReport(context)
    fun loadFileItems(path: String?, name: String = ""): MutableList<FileItem> {
        return com.example.systemious.data.loadFileItems(path, name)
    }

    fun deleteFile(path: String, fileItem: FileItem) {
        return com.example.systemious.data.deleteFile(path, fileItem)
    }

    fun getOpenFileIntent(file: File, context: Context): Intent? {
        return com.example.systemious.data.getOpenFileIntent(file, context)
    }
}