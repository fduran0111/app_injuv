package com.injuv.voluntariado.data

import android.content.Context

class ApplicationPreferences(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun appliedIds(): Set<Int> =
        prefs.getStringSet(KEY_APPLIED, emptySet())?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()

    fun apply(id: Int) {
        val current = prefs.getStringSet(KEY_APPLIED, emptySet())?.toMutableSet() ?: mutableSetOf()
        current.add(id.toString())
        prefs.edit().putStringSet(KEY_APPLIED, current).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "injuv_applications"
        private const val KEY_APPLIED = "applied_ids"
    }
}
