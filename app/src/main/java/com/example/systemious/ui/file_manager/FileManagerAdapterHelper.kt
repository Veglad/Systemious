package com.example.systemious.ui.file_manager

import com.example.systemious.data.getFolderSizeParams
import java.io.File
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

object FileManagerAdapterHelper {

    val dateTimeFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss  Z")

    fun getAdvancedFileInfo(fileItem: FileItem) : FileInfo {
        val file = File(fileItem.absolutePath)
        val fileInfo = FileInfo()
        val sizeToSuffix = getFolderSizeParams(file)

        fileInfo.fileSize = sizeToSuffix.first
        fileInfo.fileSizeSuffix = sizeToSuffix.second
        fileInfo.fileName = fileItem.name
        fileInfo.filePath = fileItem.absolutePath
        fileInfo.fileLastModificationDate = dateTimeFormat.format(file.lastModified())
        fileInfo.iconUri = fileItem.iconUri
        fileInfo.fileType = fileItem.type

        return fileInfo
    }
}