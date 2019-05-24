package com.example.systemious.data

import android.content.Context
import androidx.core.content.FileProvider
import com.example.systemious.ui.file_manager.FileItem
import java.io.File
import android.content.Intent
import android.net.Uri
import com.example.systemious.ui.file_manager.FileType


val IMAGE_EXTENSIONS = listOf("jpg", "png", "gif", "jpeg", "webp")

fun loadFileItems(path: String?, name: String = ""): MutableList<FileItem> {
    path?.let {
        val rootDir = if (name.isEmpty()) File(path) else File("$path/$name")
        if (!rootDir.canRead() || rootDir.list().isEmpty()) return mutableListOf()

        val fileList = mutableListOf<FileItem>()
        for (file in rootDir.listFiles()) {
            if (!file.startsWith(".")) {
                val fileItem = FileItem()

                fileItem.name = file.name
                fileItem.type = if(file.isDirectory) FileType.DIRECTORY else FileType.FILE
                fileItem.absolutePath = file.absolutePath
                setBitmapIfImage(fileItem, file, IMAGE_EXTENSIONS)

                fileList.add(fileItem)
            }
        }

        fileList.sortBy { it.name.compareTo(it.name) }
        return fileList
    } ?: return mutableListOf()
}

fun setBitmapIfImage(fileItem: FileItem, file: File, imageExtensions: List<String>) {
    for (extension in imageExtensions) {
        if (file.name.toLowerCase().endsWith(extension)) {
            fileItem.iconUri = Uri.fromFile(file)
        }
    }
}

fun deleteFile(path: String, fileItem: FileItem) {
    val file = File("$path/${fileItem.name}")
    if (fileItem.type == FileType.DIRECTORY) {
        file.deleteRecursively()
    } else {
        file.delete()
    }
}

fun getFolderSizeParams(file: File) : Pair<Double, String>{
    var size = getFileFolderSize(file).toDouble()
    var sizeSuffix = "B"

    if (size > 1024) {
        size /= 1024
        sizeSuffix = "KB"
        if (size > 1024) {
            size /= 1024
            sizeSuffix = "MB"
        }
        if (size > 1024) {
            size /= 1024
            sizeSuffix = "GB"
        }
    }

    return size to sizeSuffix
}

fun getFileFolderSize(dir: File): Long {
    var size: Long = 0
    if (dir.isDirectory) {
        for (file in dir.listFiles()!!) {
            size += if (file.isFile) {
                file.length()
            } else
                getFileFolderSize(file)
        }
    } else if (dir.isFile) {
        size += dir.length()
    }
    return size
}

fun getOpenFileIntent(path: String, fileName: String, context: Context): Intent? {
    val file = File("$path/$fileName")
    if (!file.exists()) return null
    val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", File("$path/$fileName"))
    val mime = context.contentResolver.getType(uri)

    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    intent.setDataAndType(uri, mime)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    return if (intent.resolveActivity(context.packageManager)!= null) intent else null
}
