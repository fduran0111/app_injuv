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
