package com.example.systemious.utils

object Constants{
    const val SERVICE_INFO_NOTIFICATION_ID = 15671
    const val SERVICE_INFO_NOTIFICATION_CHANNEL_ID = "SYSTEMIOUS_SERVICE_INFO_NOTIFICATION_CHANNEL_ID"
    const val SERVICE_INFO_NOTIFICATION_CHANNEL_NAME = "SYSTEMIOUS_SERVICE_INFO_NOTIFICATION_CHANNEL_NAME"
    //SystemInfo BroadcastReceiver constants
    const val SYSTEM_INFO_DETAILS_BROADCAST_RECEIVER_ACTION = "SYSTEM_INFO_DETAILS_BROADCAST_RECEIVER_ACTION"
    const val SYSTEM_INFO_BROADCAST_RECEIVER_ACTION = "SYSTEM_INFO_BROADCAST_RECEIVER_ACTION"
    const val SYSTEM_INFO_BROADCAST_RECEIVER_WORKING_CODE_KEY = "SYSTEM_INFO_BROADCAST_RECEIVER_CODE_KEY"
    const val SYSTEM_INFO_BROADCAST_RECEIVER_STOP_CODE = 5631
    const val SYSTEM_INFO_BROADCAST_RECEIVER_START_CODE = 5632

    //Shell
    const val COMMAND_CAT_PATH = "/system/bin/cat"
    const val CPU_INFO_PATH =  "/proc/cpuinfo"
    const val CPU_FREQUENCY_MIN = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"
    const val CPU_FREQUENCY_MAX = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"

}