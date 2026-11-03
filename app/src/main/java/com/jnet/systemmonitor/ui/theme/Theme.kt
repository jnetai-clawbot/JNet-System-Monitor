package com.jnet.systemmonitor.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Background = Color(0xFF121212)
val CardBackground = Color(0xFF1E1E1E)
val Accent = Color(0xFF4CAF50)
val TextPrimary = Color(0xFFEDEDED)
val TextSecondary = Color(0xFF9E9E9E)
val BarTrack = Color(0xFF2A2A2A)

private val JNetColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = Color.Black,
    secondary = Accent,
    onSecondary = Color.Black,
    background = Background,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextSecondary,
    error = Color(0xFFCF6679)
)

@Composable
fun JNetTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = JNetColorScheme,
        content = content
    )
}
