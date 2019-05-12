package com.example.systemious.utils

import java.io.InputStream

fun InputStream.getTextAndClose(): String {
    return bufferedReader()
        .use { it.readText() }
}
