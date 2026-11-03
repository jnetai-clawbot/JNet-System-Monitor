package com.jnet.systemmonitor.data

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import com.jnet.systemmonitor.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SystemInfoProvider(private val context: Context) {

    fun snapshot(): SystemInfo {
        val battery = readBattery()
        val memory = readMemory()
        val storage = readStorage()

        return SystemInfo(
            manufacturer = Build.MANUFACTURER.asDisplayValue(),
            model = Build.MODEL.asDisplayValue(),
            androidVersion = Build.VERSION.RELEASE.asDisplayValue(),
            apiLevel = Build.VERSION.SDK_INT,
            cpu = readCpuName(),
            cpuCores = Runtime.getRuntime().availableProcessors(),
            ramTotal = memory.first,
            ramAvailable = memory.second,
            storageTotal = storage.first,
            storageAvailable = storage.second,
            batteryPercent = battery.percent,
            batteryCharging = battery.charging,
            batteryTemperatureC = battery.temperatureC,
            uptimeMs = SystemClock.elapsedRealtime(),
            appVersion = BuildConfig.VERSION_NAME,
            updatedAt = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        )
    }

    private fun readMemory(): Pair<Long, Long> {
        return try {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val info = ActivityManager.MemoryInfo()
            manager?.getMemoryInfo(info)
            info.totalMem to info.availMem
        } catch (error: Throwable) {
            0L to 0L
        }
    }

    private fun readStorage(): Pair<Long, Long> {
        return try {
            val stat = StatFs(Environment.getDataDirectory().absolutePath)
            stat.totalBytes to stat.availableBytes
        } catch (error: Throwable) {
            0L to 0L
        }
    }

    private data class BatterySnapshot(
        val percent: Int?,
        val charging: Boolean?,
        val temperatureC: Float?
    )

    private fun readBattery(): BatterySnapshot {
        return try {
            val intent = context.registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            ) ?: return BatterySnapshot(null, null, null)

            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val percent = if (level >= 0 && scale > 0) level * 100 / scale else null

            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val charging = when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING,
                BatteryManager.BATTERY_STATUS_FULL -> true
                BatteryManager.BATTERY_STATUS_DISCHARGING,
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> false
                else -> null
            }

            val tenths = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
            val temperature = if (tenths != Int.MIN_VALUE && tenths > -1000) {
                tenths / 10f
            } else {
                null
            }

            BatterySnapshot(percent, charging, temperature)
        } catch (error: Throwable) {
            BatterySnapshot(null, null, null)
        }
    }

    private fun readCpuName(): String {
        val abi = Build.SUPPORTED_ABIS.firstOrNull()

        val hardware = try {
            File("/proc/cpuinfo").useLines { lines ->
                lines.mapNotNull { line ->
                    val parts = line.split(":", limit = 2)
                    if (parts.size == 2) {
                        val key = parts[0].trim().lowercase(Locale.ROOT)
                        if (key == "hardware" || key == "model name") {
                            parts[1].trim().takeIf { it.isNotBlank() }
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }.firstOrNull()
            }
        } catch (error: Throwable) {
            null
        }

        return hardware ?: abi?.let { "ABI: $it" } ?: "Unavailable"
    }

    private fun String?.asDisplayValue(): String {
        return if (this.isNullOrBlank() || this.equals(Build.UNKNOWN, ignoreCase = true)) {
            "Unavailable"
        } else {
            this
        }
    }
}
