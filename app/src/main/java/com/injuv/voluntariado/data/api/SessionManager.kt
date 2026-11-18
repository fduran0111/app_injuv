package com.injuv.voluntariado.data.api

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Sesión del usuario: guarda el JWT y los datos de `/auth/me` en SharedPreferences privadas
 * de la app, y expone el usuario actual como StateFlow para que la navegación reaccione
 * (sin usuario → pantalla de login).
 */
object SessionManager {
    private const val PREFS = "injuv_session"
    private const val KEY_TOKEN = "token"
    private const val KEY_USER = "user"
    private const val KEY_ESTADOS = "estados_postulaciones"
    private const val KEY_AVISOS = "avisos_vistos"
    private const val KEY_CALIFICAR = "calificar_recordadas"

    private lateinit var prefs: SharedPreferences

    private val _user = MutableStateFlow<Usuario?>(null)
    val user: StateFlow<Usuario?> = _user.asStateFlow()

    @Volatile
    var token: String? = null
        private set

    fun init(context: Context) {
        if (::prefs.isInitialized) return
        prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        token = prefs.getString(KEY_TOKEN, null)
        _user.value = prefs.getString(KEY_USER, null)?.let {
            runCatching { ApiClient.json.decodeFromString(Usuario.serializer(), it) }.getOrNull()
        }
        if (token == null) _user.value = null
    }

    fun guardar(respuesta: TokenResponse) {
        token = respuesta.access_token
        prefs.edit().putString(KEY_TOKEN, respuesta.access_token).apply()
        actualizarUsuario(respuesta.user)
    }

    fun actualizarUsuario(usuario: Usuario) {
        _user.value = usuario
        prefs.edit().putString(KEY_USER, ApiClient.json.encodeToString(Usuario.serializer(), usuario)).apply()
    }

    /** Refresca los datos del usuario desde la API (por si cambió su rol, horas, etc.). */
    suspend fun refrescar() {
        if (token == null) return
        runCatching { ApiClient.call { me() } }.onSuccess { actualizarUsuario(it) }
    }

    fun cerrarSesion() {
        token = null
        _user.value = null
        prefs.edit().clear().apply()
    }

    internal fun onSessionExpired() = cerrarSesion()

    // ---------- Estados conocidos de las postulaciones (para el aviso de cambios) ----------

    /** Último estado visto por postulación: id → estado. */
    fun estadosConocidos(): Map<Int, String> =
        prefs.getStringSet(KEY_ESTADOS, emptySet()).orEmpty().mapNotNull { linea ->
            val (id, estado) = linea.split("|", limit = 2).takeIf { it.size == 2 } ?: return@mapNotNull null
            id.toIntOrNull()?.let { it to estado }
        }.toMap()

    fun guardarEstados(postulaciones: List<PostulacionUsuario>) {
        prefs.edit().putStringSet(KEY_ESTADOS, postulaciones.map { "${it.id}|${it.estado}" }.toSet()).apply()
    }

    // ---------- Últimos anuncios de foro vistos (para el badge "Nuevo" en Notificaciones) ----------

    /** Último mensaje de anuncios visto por voluntariado: oportunidad_id → mensaje_id. */
    fun avisosVistos(): Map<Int, Int> =
        prefs.getStringSet(KEY_AVISOS, emptySet()).orEmpty().mapNotNull { linea ->
            val (op, msg) = linea.split("|", limit = 2).takeIf { it.size == 2 } ?: return@mapNotNull null
            val opId = op.toIntOrNull() ?: return@mapNotNull null
            msg.toIntOrNull()?.let { opId to it }
        }.toMap()

    fun guardarAvisoVisto(oportunidadId: Int, mensajeId: Int) {
        val actuales = prefs.getStringSet(KEY_AVISOS, emptySet()).orEmpty()
            .filterNot { it.startsWith("$oportunidadId|") }
            .toMutableSet()
        actuales.add("$oportunidadId|$mensajeId")
        prefs.edit().putStringSet(KEY_AVISOS, actuales).apply()
    }

    // ---------- Recordatorios de calificación ya mostrados ----------

    /** Ids de postulaciones cuya invitación a calificar a la ONG ya se mostró. */
    fun calificarVistos(): Set<Int> =
        prefs.getStringSet(KEY_CALIFICAR, emptySet()).orEmpty().mapNotNull { it.toIntOrNull() }.toSet()

    fun guardarCalificarVisto(postulacionId: Int) {
        val actuales = prefs.getStringSet(KEY_CALIFICAR, emptySet()).orEmpty()
        prefs.edit().putStringSet(KEY_CALIFICAR, actuales + postulacionId.toString()).apply()
    }
}
