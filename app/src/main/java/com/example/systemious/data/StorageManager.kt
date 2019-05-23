package com.example.systemious.data

import android.content.Context
import androidx.core.content.FileProvider
import com.example.systemious.ui.file_manager.FileItem
import java.io.File
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory




val IMAGE_EXTENSIONS = listOf("jpg", "png", "gif", "jpeg", "webp")

fun loadFileItems(path: String?, name: String = ""): MutableList<FileItem> {
    path?.let {
        val rootDir = if (name.isEmpty()) File(path) else File(path, name)
        if (!rootDir.canRead() || rootDir.list().isEmpty()) return mutableListOf()

        val fileList = mutableListOf<FileItem>()
        for (fileName in rootDir.list()) {
            if (!fileName.startsWith(".")) {
                val fileItem = FileItem()
                val file = File(path, fileName)

                fileItem.name = fileName
                fileItem.isDirectory = file.isDirectory
                setFileItemFileSize(fileItem, file)
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
            fileItem.icon = getBitmapFromFile(file)
        }
    }
}

fun getBitmapFromFile(file: File): Bitmap? {
    val options = BitmapFactory.Options()
    options.inPreferredConfig = Bitmap.Config.ARGB_8888
    return BitmapFactory.decodeFile(file.path, options)
}

fun setFileItemFileSize(fileItem: FileItem, file: File) {
    var size = if (fileItem.isDirectory) file.length().toDouble() else folderSize(file).toDouble()
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

    fileItem.size = size
    fileItem.sizeSuffix = sizeSuffix
}

fun folderSize(directory: File): Long {
    var length: Long = 0
    for (file in directory.listFiles()!!) {
        length += if (file.isFile)
            file.length()
        else
            folderSize(file)
    }
    return length
}

/**
 *
 */
fun getOpenFileIntent(path: String, fileName: String, context: Context): Intent? {
    val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", File(path, fileName))
    val mime = context.contentResolver.getType(uri)

    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    intent.setDataAndType(uri, mime)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    return if (intent.resolveActivity(context.packageManager)!= null) intent else null
}
