package com.example.systemious

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.systemious.utils.Constants
import io.realm.Realm
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        createSystemInfoNotificationChannel()
        initTimber()
        realmInit()
    }

    private fun realmInit() {
        Realm.init(this)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun createSystemInfoNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceInfoChannel = NotificationChannel(
                Constants.SERVICE_INFO_NOTIFICATION_CHANNEL_ID,
                Constants.SERVICE_INFO_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceInfoChannel)
        }
    }
}