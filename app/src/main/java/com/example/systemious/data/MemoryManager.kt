package com.example.systemious.data

import android.os.Environment
import android.os.StatFs
import java.io.File
import java.text.DecimalFormat


class MemoryManager {
    companion object {
        private fun externalMemoryAvailable(): Boolean {
            return when ( Environment.getExternalStorageState()) {
                Environment.MEDIA_MOUNTED -> true
                Environment.MEDIA_MOUNTED_READ_ONLY -> true
                else -> false
            }
        }

        fun getTotalInternalMemorySize(): String {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.getPath())
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            return formatSize(totalBlocks * blockSize)
        }



        fun getTotalExternalMemorySize(): String? {
            return if (externalMemoryAvailable()) {
                val file = Environment.getExternalStorageDirectory()
                formatSize(getFileSize(file))
            } else {
                null
            }
        }

        fun getFileSize(file: File): Long {
            val stat = StatFs(file.path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            return totalBlocks * blockSize
        }

        private fun formatSize(size: Long): String {
            var size = size.toDouble()
            var suffix: String? = null

            if (size >= 1024) {
                suffix = "KB"
                size /= 1024
                if (size >= 1024) {
                    suffix = "MB"
                    size /= 1024
                    if (size >= 1024) {
                        suffix = "GB"
                        size /= 1024
                    }
                }
            }

            return DecimalFormat("#.##").format(size) + " $suffix"
        }
    }
}