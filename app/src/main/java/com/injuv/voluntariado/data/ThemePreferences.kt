package com.injuv.voluntariado.data

import android.content.Context

private const val PREFS_NAME = "injuv_prefs"
private const val KEY_DARK_THEME = "dark_theme"

fun loadDarkThemePreference(context: Context): Boolean {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getBoolean(KEY_DARK_THEME, true)
}

fun saveDarkThemePreference(context: Context, isDark: Boolean) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(KEY_DARK_THEME, isDark)
        .apply()
}
