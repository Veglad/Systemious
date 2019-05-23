package com.example.systemious

import com.example.systemious.data.IMAGE_EXTENSIONS
import com.example.systemious.data.getBitmapFromFile
import com.example.systemious.data.loadFileItems
import com.example.systemious.data.setBitmapIfImage
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
    fun `verify if setBitMapIfImage gets bitmap for file with correct extension`() {
        mockkStatic("com.example.systemious.data.StorageManagerKt")
        every { getBitmapFromFile(any()) } returns null

        val file: File = mockk()
        every { file.name } returns CORRECT_IMAGE_EXTENSION_NAME

        setBitmapIfImage(FileItem(), file, IMAGE_EXTENSIONS)

        verify { getBitmapFromFile(any()) }
    }

    @Test
    fun `verify if setBitMapIfImage gets bitmap for file with incorrect extension`() {
        mockkStatic("com.example.systemious.data.StorageManagerKt")
        every { getBitmapFromFile(any()) } returns null

        val file: File = mockk()
        every { file.name } returns INCORRECT_IMAGE_EXTENSION_NAME

        setBitmapIfImage(FileItem(), file, IMAGE_EXTENSIONS)

        verify(exactly = 0) { getBitmapFromFile(any()) }
    }
}
