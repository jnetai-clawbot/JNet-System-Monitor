package com.jnet.systemmonitor.data

data class SystemInfo(
    val manufacturer: String,
    val model: String,
    val androidVersion: String,
    val apiLevel: Int,
    val cpu: String,
    val cpuCores: Int,
    val ramTotal: Long,
    val ramAvailable: Long,
    val storageTotal: Long,
    val storageAvailable: Long,
    val batteryPercent: Int?,
    val batteryCharging: Boolean?,
    val batteryTemperatureC: Float?,
    val uptimeMs: Long,
    val appVersion: String,
    val updatedAt: String
)
