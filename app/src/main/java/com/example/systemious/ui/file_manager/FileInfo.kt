package com.example.systemious.ui.file_manager

import android.net.Uri

data class FileInfo (
    var iconUri: Uri? = null,
    var fileName: String = "",
    var filePath: String = "",
    var fileSize: Double = 0.0,
    var fileSizeSuffix: String = "",
    var fileLastModificationDate: String = "",
    var fileType: FileType = FileType.FILE
)