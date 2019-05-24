package com.example.systemious.ui.file_manager

import android.graphics.Bitmap

data class FileItem (var name: String = "",
                     var size: Double = 0.0,
                     var sizeSuffix: String = "MB",
                     var type: FileType = FileType.FILE,
                     var icon: Bitmap? = null)

enum class FileType{
    FILE, DIRECTORY, PARENT_FOLDER
}