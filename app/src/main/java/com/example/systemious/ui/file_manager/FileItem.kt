package com.example.systemious.ui.file_manager

import android.net.Uri

data class FileItem (var name: String = "",
                     var size: Double = 0.0,
                     var sizeSuffix: String = "MB",
                     var type: FileType = FileType.FILE,
                     var iconUri: Uri? = null)

enum class FileType{
    FILE, DIRECTORY, PARENT_FOLDER
}