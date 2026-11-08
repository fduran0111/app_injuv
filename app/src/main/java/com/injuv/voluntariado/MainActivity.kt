package com.injuv.voluntariado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.injuv.voluntariado.navigation.InjuvApp
import com.injuv.voluntariado.ui.theme.ProvideAppTheme
import com.injuv.voluntariado.ui.theme.ThemePreferences

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themePrefs = ThemePreferences(this)
        enableEdgeToEdge()
        setContent {
            var isDark by remember { mutableStateOf(themePrefs.isDark()) }
            ProvideAppTheme(
                isDark = isDark,
                onThemeChange = { dark ->
                    isDark = dark
                    themePrefs.setDark(dark)
                },
            ) {
                InjuvApp()
            }
        }
    }
}
