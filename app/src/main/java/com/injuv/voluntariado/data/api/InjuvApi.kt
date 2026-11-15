package com.injuv.voluntariado.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Endpoints del backend FastAPI que usa la app (mismos que el frontend web).
 * Documentación interactiva: {API}/docs
 */
interface InjuvApi {

    // ---------- Sesión ----------
    @POST("auth/login")
    suspend fun login(@Body body: LoginBody): TokenResponse

    @POST("auth/register")
    suspend fun register(@Body body: RegisterBody): TokenResponse

    @GET("auth/me")
    suspend fun me(): Usuario

    // ---------- Oportunidades ----------
    @GET("oportunidades")
    suspend fun oportunidades(
        @Query("q") q: String? = null,
        @Query("region") region: String? = null,
        @Query("limit") limit: Int? = null,
    ): List<Oportunidad>

    @GET("oportunidades/{id}")
    suspend fun oportunidad(@Path("id") id: Int): Oportunidad

    /** Voluntariados de la organización del usuario (integrantes). */
    @GET("oportunidades/mias")
    suspend fun oportunidadesMias(): List<Oportunidad>

    /** Voluntariados donde el usuario es encargado/tutor (designado desde la web o la app). */
    @GET("oportunidades/tutorias")
    suspend fun tutorias(): List<Oportunidad>

    /** Administrador y miembros de la organización del usuario. */
    @GET("organizaciones/mia/integrantes")
    suspend fun integrantes(): List<Usuario>

    @GET("oportunidades/{id}/encargados")
    suspend fun encargados(@Path("id") id: Int): List<Encargado>

    /** Reemplaza la lista de encargados (misma columna `encargados_ids` que usa la web). */
    @PUT("oportunidades/{id}/encargados")
    suspend fun guardarEncargados(@Path("id") id: Int, @Body body: EncargadosBody): List<Encargado>

    @POST("oportunidades/{id}/postular")
    suspend fun postular(@Path("id") id: Int): PostularResponse

    // ---------- Búsqueda de usuarios (organización/admin) ----------
    @GET("usuarios/buscar")
    suspend fun buscarUsuario(@Query("email") email: String): Usuario

    // ---------- Mis postulaciones ----------
    @GET("usuarios/me/postulaciones")
    suspend fun misPostulaciones(): List<PostulacionUsuario>

    /** Retira la postulación (estado "Declinada por usuario"). */
    @PUT("postulaciones/{id}/declinar")
    suspend fun declinar(@Path("id") id: Int): PostulacionUsuario

    /** Solo si fue "Seleccionado": "Confirmado" o "No confirmado". */
    @PUT("postulaciones/{id}/confirmacion")
    suspend fun confirmar(@Path("id") id: Int, @Body body: ConfirmacionBody): PostulacionUsuario

    // ---------- Contenido público ----------
    @GET("public/estadisticas")
    suspend fun estadisticas(): Estadisticas

    @GET("noticias")
    suspend fun noticias(@Query("limit") limit: Int = 50): List<Noticia>

    @GET("biblioteca/tematicas")
    suspend fun tematicas(): List<Tematica>

    @GET("biblioteca/documentos")
    suspend fun biblioteca(@Query("tematica_id") tematicaId: Int? = null): List<Documento>

    @GET("academia/documentos")
    suspend fun academia(@Query("categoria") categoria: String? = null): List<Documento>
}
