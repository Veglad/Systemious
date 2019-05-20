package com.example.systemious.ui.app_manager.entities

import android.graphics.drawable.Drawable

data class AppInfo(
    var appName: String = "",
    var packageName: String = "",
    var icon: Drawable? = null,
    var sizeInMb: Double = 0.0
)