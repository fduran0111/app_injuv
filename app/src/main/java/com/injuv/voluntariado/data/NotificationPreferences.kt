package com.injuv.voluntariado.data

import android.content.Context

/**
 * Preferencias de notificaciones del usuario, guardadas en el teléfono. Las notificaciones vienen
 * del servidor (GET /notificaciones, sin push); aquí solo se decide qué tipos se muestran.
 */
class NotificationPreferences(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Interruptor general: apagado, ningún tipo muestra alertas. */
    var activas: Boolean
        get() = prefs.getBoolean(KEY_ACTIVAS, true)
        set(v) = prefs.edit().putBoolean(KEY_ACTIVAS, v).apply()

    /** Cambios de estado en las postulaciones (novedades). */
    var estados: Boolean
        get() = prefs.getBoolean(KEY_ESTADOS, true)
        set(v) = prefs.edit().putBoolean(KEY_ESTADOS, v).apply()

    /** Anuncios de los tutores en los foros. */
    var anuncios: Boolean
        get() = prefs.getBoolean(KEY_ANUNCIOS, true)
        set(v) = prefs.edit().putBoolean(KEY_ANUNCIOS, v).apply()

    /** Recordatorios para calificar a la organización al terminar un voluntariado. */
    var calificar: Boolean
        get() = prefs.getBoolean(KEY_CALIFICAR, true)
        set(v) = prefs.edit().putBoolean(KEY_CALIFICAR, v).apply()

    /** Resumen con el estado actual de las postulaciones activas. */
    var resumen: Boolean
        get() = prefs.getBoolean(KEY_RESUMEN, true)
        set(v) = prefs.edit().putBoolean(KEY_RESUMEN, v).apply()

    /** true si el tipo está permitido (interruptor general + el propio). */
    fun permiteEstados() = activas && estados
    fun permiteAnuncios() = activas && anuncios
    fun permiteCalificar() = activas && calificar
    fun permiteResumen() = activas && resumen

    /** Si una notificación del servidor se muestra según su tipo y las preferencias. */
    fun permite(tipo: String) = when (tipo) {
        "estado_postulacion", "postulacion_enviada" -> permiteEstados()
        "anuncio_foro" -> permiteAnuncios()
        "pendiente_calificar" -> permiteCalificar()
        else -> activas
    }

    /** Cuántos tipos quedaron activos, para subtítulos. */
    fun tiposActivos() = listOf(estados, anuncios, calificar, resumen).count { it }

    companion object {
        private const val PREFS_NAME = "injuv_notifications"
        private const val KEY_ACTIVAS = "activas"
        private const val KEY_ESTADOS = "estados"
        private const val KEY_ANUNCIOS = "anuncios"
        private const val KEY_CALIFICAR = "calificar"
        private const val KEY_RESUMEN = "resumen"
    }
}
