package com.example.systemious

import com.example.systemious.data.loadFileItems
import org.junit.Test

import org.junit.Assert.*


class FileManagerTest {
    @Test
    fun `check if nullable path return empty list`() {
        assertTrue(loadFileItems(null).size == 0)
    }
}
