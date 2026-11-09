package com.injuv.voluntariado.data

import android.content.Context

class AttendancePreferences(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun presentNames(date: String): Set<String> =
        prefs.getStringSet(key(date), emptySet()) ?: emptySet()

    fun savePresent(date: String, names: Set<String>) {
        prefs.edit().putStringSet(key(date), names).apply()
    }

    private fun key(date: String) = "$KEY_PREFIX$date"

    companion object {
        private const val PREFS_NAME = "injuv_attendance"
        private const val KEY_PREFIX = "present_"
    }
}
