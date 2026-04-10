package com.example.nexentry

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.nexentry.myui.LoginScreen
import com.example.nexentry.myui.MainContainerScreen
import com.example.nexentry.ui.theme.NexEntryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val systemDark = isSystemInDarkTheme()
            var isDarkMode by remember { mutableStateOf(systemDark) }
            
            NexEntryTheme(darkTheme = isDarkMode) {
                var isLoggedIn by remember { mutableStateOf(false) }

                if (isLoggedIn) {
                    MainContainerScreen(
                        isDarkMode = isDarkMode,
                        onThemeChange = { isDarkMode = it }
                    )
                } else {
                    LoginScreen(onLoginSuccess = { 
                        isLoggedIn = true 
                        Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }
    }
}
