package com.injuv.voluntariado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.injuv.voluntariado.data.loadDarkThemePreference
import com.injuv.voluntariado.data.saveDarkThemePreference
import com.injuv.voluntariado.navigation.InjuvApp
import com.injuv.voluntariado.ui.theme.ProvideAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val initialDark = loadDarkThemePreference(this)
        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(initialDark) }
            ProvideAppTheme(isDark = isDarkTheme) {
                InjuvApp(
                    isDarkTheme = isDarkTheme,
                    onDarkThemeChange = { dark ->
                        isDarkTheme = dark
                        saveDarkThemePreference(this, dark)
                    },
                )
            }
        }
    }
}
