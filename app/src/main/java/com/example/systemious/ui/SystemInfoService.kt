package com.example.systemious.ui

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.app.PendingIntent
import android.app.Service
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.systemious.R
import com.example.systemious.utils.Constants

class SystemInfoService : Service() {

    companion object {
      private const val TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE"
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
        const val ACTION_SET_UPDATE_FREQUENCY = "ACTION_SET_UPDATE_FREQUENCY"

        const val FREQUENCY_UPDATE_MS_KEY = "FREQUENCY_UPDATE_MS_KEY"
    }

    private val mBinder = LocalBinder()

    var infoUpdateFrequencyMs = 1000
    private set

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val action = intent.action

            when (action) {
                ACTION_SET_UPDATE_FREQUENCY -> {
                    val tempInfoUpdateFrequencyMs = intent.extras?.getInt(FREQUENCY_UPDATE_MS_KEY) ?: infoUpdateFrequencyMs
                    if (tempInfoUpdateFrequencyMs > 100) {
                        infoUpdateFrequencyMs = tempInfoUpdateFrequencyMs
                    }
                }
                ACTION_START_FOREGROUND_SERVICE -> startForegroundService()
                ACTION_STOP_FOREGROUND_SERVICE -> stopForegroundService()
                else -> {}
            }
        }

        return Service.START_STICKY
    }

    private fun stopForegroundService() {
        notifyServiceStopped()
        Log.d(TAG_FOREGROUND_SERVICE, "Foreground service is stopped.")
        stopForeground(true)
        stopSelf()
    }

    private fun notifyServiceStopped() {
        val intent = Intent(Constants.SYSTEM_INFO_BROADCAST_RECEIVER_ACTION)
        intent.putExtra(
            Constants.SYSTEM_INFO_BROADCAST_RECEIVER_WORKING_CODE_KEY,
            Constants.SYSTEM_INFO_BROADCAST_RECEIVER_STOP_CODE
        )
        sendBroadcast(intent)
    }

    private fun startForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Foreground service is started.")

        val notificationIntent = Intent(application, SystemStateActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val stopIntent = Intent(application, SystemInfoService::class.java)
        stopIntent.action = ACTION_STOP_FOREGROUND_SERVICE
        val pStopIntent = PendingIntent.getService(this, 0, stopIntent, 0)

        val icon = BitmapFactory.decodeResource(application.resources, com.example.systemious.R.drawable.ic_chart)

        val notification = NotificationCompat.Builder(application, Constants.SERVICE_INFO_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(application.resources.getString(com.example.systemious.R.string.system_info_notification_title))
            .setTicker(application.resources.getString(com.example.systemious.R.string.system_info_notification_description))
            .setContentText(application.resources.getString(com.example.systemious.R.string.system_info_notification_description))
            .setSmallIcon(com.example.systemious.R.drawable.ic_chart)
            .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .addAction(
                R.drawable.ic_stop_secondary_color_24dp,
                application.resources.getString(R.string.system_info_notification_action_stop),
                pStopIntent
            ).build()

        notifyServiceStarted()
        startForeground(Constants.SERVICE_INFO_NOTIFICATION_ID, notification)
    }

    private fun notifyServiceStarted() {
        val intent = Intent(Constants.SYSTEM_INFO_BROADCAST_RECEIVER_ACTION)
        intent.putExtra(
            Constants.SYSTEM_INFO_BROADCAST_RECEIVER_WORKING_CODE_KEY,
            Constants.SYSTEM_INFO_BROADCAST_RECEIVER_START_CODE
        )
        sendBroadcast(intent)
    }

    inner class LocalBinder : Binder() {
        internal// Return this instance of LocalService so clients can call public methods
        val service: SystemInfoService
            get() = this@SystemInfoService
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }
}