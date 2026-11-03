package com.jnet.systemmonitor.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jnet.systemmonitor.data.SystemInfo
import com.jnet.systemmonitor.data.SystemInfoProvider
import com.jnet.systemmonitor.ui.theme.Accent
import com.jnet.systemmonitor.ui.theme.Background
import com.jnet.systemmonitor.ui.theme.BarTrack
import com.jnet.systemmonitor.ui.theme.CardBackground
import com.jnet.systemmonitor.ui.theme.TextPrimary
import com.jnet.systemmonitor.ui.theme.TextSecondary
import com.jnet.systemmonitor.util.FormatUtils
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val provider = remember { SystemInfoProvider(context.applicationContext) }
    var info by remember { mutableStateOf(provider.snapshot()) }
    var manualRefresh by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5_000)
            info = provider.snapshot()
        }
    }

    LaunchedEffect(manualRefresh) {
        if (manualRefresh > 0) {
            delay(150)
            info = provider.snapshot()
        }
    }

    val pulse = rememberInfiniteTransition(label = "statusPulse")
    val pulseAlpha by pulse.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "statusPulseAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 760.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Header(onRefresh = { manualRefresh++ }) }
            item { StatusBar(info = info, pulseAlpha = pulseAlpha) }
            item { DeviceCard(info) }
            item { CpuCard(info) }
            item { MemoryCard(info) }
            item { StorageCard(info) }
            item { BatteryCard(info) }
            item { AboutCard(info) }
            item { Spacer(modifier = Modifier.height(6.dp)) }
        }
    }
}

@Composable
private fun Header(onRefresh: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "J~Net System Monitor",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Live device telemetry",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
        Button(
            onClick = onRefresh,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CardBackground,
                contentColor = Accent
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(text = "Refresh", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun StatusBar(info: SystemInfo, pulseAlpha: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(RoundedCornerShape(50))
                .background(Accent.copy(alpha = pulseAlpha))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Auto-refresh every 5s",
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Updated ${info.updatedAt}",
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun DeviceCard(info: SystemInfo) {
    MonitorCard(title = "Device") {
        InfoRow("Manufacturer", info.manufacturer)
        InfoRow("Model", info.model)
        InfoRow("Android version", info.androidVersion)
        InfoRow("API level", info.apiLevel.toString())
        InfoRow("Uptime", FormatUtils.formatUptime(info.uptimeMs))
    }
}

@Composable
private fun CpuCard(info: SystemInfo) {
    MonitorCard(title = "Processor") {
        InfoRow("CPU", info.cpu)
        InfoRow("Cores", FormatUtils.formatCores(info.cpuCores))
    }
}

@Composable
private fun MemoryCard(info: SystemInfo) {
    val used = (info.ramTotal - info.ramAvailable).coerceAtLeast(0L)
    MonitorCard(title = "Memory") {
        InfoRow("Total", FormatUtils.formatBytes(info.ramTotal))
        InfoRow("Available", FormatUtils.formatBytes(info.ramAvailable))
        InfoRow("Used", FormatUtils.formatBytes(used))
        Spacer(modifier = Modifier.height(10.dp))
        UsageBar(fraction = FormatUtils.usageFraction(info.ramTotal, info.ramAvailable))
    }
}

@Composable
private fun StorageCard(info: SystemInfo) {
    val used = (info.storageTotal - info.storageAvailable).coerceAtLeast(0L)
    MonitorCard(title = "Internal storage") {
        InfoRow("Total", FormatUtils.formatBytes(info.storageTotal))
        InfoRow("Available", FormatUtils.formatBytes(info.storageAvailable))
        InfoRow("Used", FormatUtils.formatBytes(used))
        Spacer(modifier = Modifier.height(10.dp))
        UsageBar(fraction = FormatUtils.usageFraction(info.storageTotal, info.storageAvailable))
    }
}

@Composable
private fun BatteryCard(info: SystemInfo) {
    MonitorCard(title = "Battery") {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = FormatUtils.formatPercent(info.batteryPercent),
                color = Accent,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = chargingLabel(info.batteryCharging),
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 7.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        UsageBar(fraction = (info.batteryPercent ?: 0) / 100f)
        Spacer(modifier = Modifier.height(10.dp))
        InfoRow("Temperature", FormatUtils.formatTemperature(info.batteryTemperatureC))
        InfoRow("Charging state", chargingLabel(info.batteryCharging))
    }
}

@Composable
private fun AboutCard(info: SystemInfo) {
    MonitorCard(title = "About") {
        InfoRow("Version", "v${info.appVersion}")
        InfoRow("Created By", "J~Net 2026")
        InfoRow("Website", "jnetai.com")
        InfoRow("Package", "com.jnet.systemmonitor")
    }
}

@Composable
private fun MonitorCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardBackground)
            .padding(16.dp)
            .animateContentSize()
    ) {
        Text(
            text = title.uppercase(),
            color = Accent,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        content()
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.4f)
        )
    }
}

@Composable
private fun UsageBar(fraction: Float) {
    val safeFraction = fraction.coerceIn(0f, 1f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50))
            .background(BarTrack)
    ) {
        if (safeFraction > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(safeFraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(Accent)
            )
        }
    }
}

private fun chargingLabel(value: Boolean?): String = when (value) {
    true -> "Charging"
    false -> "Not charging"
    null -> "Unavailable"
}
