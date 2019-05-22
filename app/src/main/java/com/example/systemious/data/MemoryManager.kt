package com.example.systemious.data

import android.os.Environment
import android.os.StatFs
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
                val path = Environment.getExternalStorageDirectory()
                val stat = StatFs(path.path)
                val blockSize = stat.blockSizeLong
                val totalBlocks = stat.blockCountLong
                formatSize(totalBlocks * blockSize)
            } else {
                null
            }
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