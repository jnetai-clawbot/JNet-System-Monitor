package com.jnet.systemmonitor.util

import java.util.Locale

object FormatUtils {

    private val UNITS = arrayOf("B", "KB", "MB", "GB", "TB")

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0L) return "Unavailable"

        var value = bytes.toDouble()
        var unit = 0
        while (value >= 1024.0 && unit < UNITS.lastIndex) {
            value /= 1024.0
            unit++
        }

        return if (unit == 0) {
            "${value.toLong()} ${UNITS[unit]}"
        } else {
            String.format(Locale.US, "%.1f %s", value, UNITS[unit])
        }
    }

    fun formatUptime(ms: Long): String {
        if (ms <= 0L) return "Unavailable"

        val totalSeconds = ms / 1000
        val days = totalSeconds / 86_400
        val hours = (totalSeconds % 86_400) / 3_600
        val minutes = (totalSeconds % 3_600) / 60
        val seconds = totalSeconds % 60

        return when {
            days > 0 -> "${days}d ${hours}h ${minutes}m"
            hours > 0 -> "${hours}h ${minutes}m ${seconds}s"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }

    fun formatTemperature(celsius: Float?): String {
        return if (celsius == null || celsius < -50f || celsius > 150f) {
            "Unavailable"
        } else {
            String.format(Locale.US, "%.1f °C", celsius)
        }
    }

    fun formatPercent(percent: Int?): String {
        return if (percent == null || percent < 0) "Unavailable" else "$percent%"
    }

    fun formatCores(cores: Int): String {
        return if (cores <= 0) "Unavailable" else "$cores cores"
    }

    fun usageFraction(total: Long, available: Long): Float {
        if (total <= 0L) return 0f
        val used = (total - available).coerceAtLeast(0L)
        return (used.toDouble() / total.toDouble()).toFloat().coerceIn(0f, 1f)
    }
}
