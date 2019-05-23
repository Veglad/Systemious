package com.example.systemious.data

import android.content.Context
import androidx.core.content.FileProvider
import com.example.systemious.ui.file_manager.FileItem
import java.io.File
import com.example.systemious.App
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent





fun loadFileItems(path: String?, name: String = ""): MutableList<FileItem> {
    path?.let {
        val rootDir = if (name.isEmpty()) File(path) else File(path, name)
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
