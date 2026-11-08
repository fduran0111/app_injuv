package com.injuv.voluntariado.ui.theme

import android.content.Context

class ThemePreferences(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isDark(): Boolean = prefs.getBoolean(KEY_DARK, true)

    fun setDark(value: Boolean) {
        prefs.edit().putBoolean(KEY_DARK, value).apply()
    }

    companion object {
        private const val PREFS_NAME = "injuv_prefs"
        private const val KEY_DARK = "dark_theme"
    }
}
