package com.example.systemious

import com.example.systemious.data.IMAGE_EXTENSIONS
import com.example.systemious.data.getFileFolderSize
import com.example.systemious.data.loadFileItems
import com.example.systemious.data.setUriIfImage
import com.example.systemious.ui.file_manager.FileItem
import io.mockk.*
import org.junit.Test

import org.junit.Assert.*
import java.io.File


class FileManagerTest {

    companion object {
        const val CORRECT_IMAGE_EXTENSION_NAME = "image.jpg"
        const val INCORRECT_IMAGE_EXTENSION_NAME = "image.ss"
    }

    @Test
    fun `check if nullable path return empty list`() {
        assertTrue(loadFileItems(null).size == 0)
    }


    @Test
    fun `verify if uri is assigned null when file has incorrect image extension`() {
        mockkStatic("com.example.systemious.data.StorageManagerKt")

        val file: File = mockk()
        every { file.name } returns INCORRECT_IMAGE_EXTENSION_NAME

        val fileItem = FileItem()
        fileItem.iconUri = null

        setUriIfImage(fileItem, file, IMAGE_EXTENSIONS)

        assertNull(fileItem.iconUri)
    }
}
