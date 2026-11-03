package com.jnet.systemmonitor.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatUtilsTest {

    @Test
    fun formatsBytesInGigabytes() {
        assertEquals("1.0 GB", FormatUtils.formatBytes(1024L * 1024L * 1024L))
    }

    @Test
    fun formatsBytesInMegabytes() {
        assertEquals("1.0 MB", FormatUtils.formatBytes(1024L * 1024L))
    }

    @Test
    fun formatsRawBytes() {
        assertEquals("512 B", FormatUtils.formatBytes(512L))
    }

    @Test
    fun unavailableBytesAreLabelled() {
        assertEquals("Unavailable", FormatUtils.formatBytes(0L))
        assertEquals("Unavailable", FormatUtils.formatBytes(-5L))
    }

    @Test
    fun formatsUptimeWithDays() {
        val ms = ((24L + 2L) * 3600L + 3L * 60L) * 1000L
        assertEquals("1d 2h 3m", FormatUtils.formatUptime(ms))
    }

    @Test
    fun formatsUptimeWithHours() {
        val ms = (2L * 3600L + 3L * 60L + 4L) * 1000L
        assertEquals("2h 3m 4s", FormatUtils.formatUptime(ms))
    }

    @Test
    fun formatsUptimeWithMinutes() {
        val ms = (5L * 60L + 9L) * 1000L
        assertEquals("5m 9s", FormatUtils.formatUptime(ms))
    }

    @Test
    fun formatsTemperature() {
        assertEquals("31.5 °C", FormatUtils.formatTemperature(31.5f))
    }

    @Test
    fun rejectsUnavailableTemperature() {
        assertEquals("Unavailable", FormatUtils.formatTemperature(null))
        assertEquals("Unavailable", FormatUtils.formatTemperature(-80f))
    }

    @Test
    fun formatsPercent() {
        assertEquals("87%", FormatUtils.formatPercent(87))
        assertEquals("Unavailable", FormatUtils.formatPercent(null))
    }

    @Test
    fun formatsCores() {
        assertEquals("8 cores", FormatUtils.formatCores(8))
        assertEquals("Unavailable", FormatUtils.formatCores(0))
    }

    @Test
    fun computesUsageFraction() {
        assertEquals(0.25f, FormatUtils.usageFraction(100L, 75L), 0.0001f)
        assertEquals(0f, FormatUtils.usageFraction(0L, 0L), 0.0001f)
        assertEquals(1f, FormatUtils.usageFraction(100L, -50L), 0.0001f)
    }
}
