package com.injuv.voluntariado.data

import android.content.Context

class DesignationsPreferences(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun tutorsFor(programId: Int): Set<String> =
        prefs.getStringSet(key(programId), emptySet()) ?: emptySet()

    fun save(programId: Int, names: Set<String>) {
        prefs.edit().putStringSet(key(programId), names).apply()
    }

    private fun key(programId: Int) = "$KEY_PREFIX$programId"

    companion object {
        private const val PREFS_NAME = "injuv_designations"
        private const val KEY_PREFIX = "tutors_"
    }
}
