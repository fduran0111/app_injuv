package com.injuv.voluntariado.data

import android.content.Context

/**
 * Listas de asistencia por fecha, guardadas en el teléfono. La plataforma solo registra un
 * flag por postulación ("asistencia a la actividad"), así que el historial por día vive aquí.
 */
class AttendancePreferences(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun presentIds(programId: Int, date: String): Set<Int> =
        prefs.getStringSet(key(programId, date), emptySet()).orEmpty().mapNotNull { it.toIntOrNull() }.toSet()

    fun savePresent(programId: Int, date: String, ids: Set<Int>) {
        prefs.edit().putStringSet(key(programId, date), ids.map { it.toString() }.toSet()).apply()
    }

    private fun key(programId: Int, date: String) = "${programId}_$KEY_PREFIX$date"

    companion object {
        private const val PREFS_NAME = "injuv_attendance"
        private const val KEY_PREFIX = "present_"
    }
}
