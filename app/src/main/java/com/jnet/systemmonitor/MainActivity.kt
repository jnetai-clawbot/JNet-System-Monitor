package com.jnet.systemmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jnet.systemmonitor.ui.DashboardScreen
import com.jnet.systemmonitor.ui.theme.JNetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JNetTheme {
                DashboardScreen()
            }
        }
    }
}
