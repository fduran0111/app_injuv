package com.injuv.voluntariado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.injuv.voluntariado.navigation.InjuvApp
import com.injuv.voluntariado.ui.theme.ProvideAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProvideAppTheme(isDark = true) {
                InjuvApp()
            }
        }
    }
}
