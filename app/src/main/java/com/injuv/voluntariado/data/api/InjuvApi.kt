package com.injuv.voluntariado.data.api

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    // ---------- Perfil ----------
    @PUT("usuarios/me")
    suspend fun actualizarPerfil(@Body body: PerfilUsuarioBody): Usuario

    @Multipart
    @POST("usuarios/me/foto")
    suspend fun subirFoto(@Part imagen: MultipartBody.Part): Usuario

    @DELETE("usuarios/me/foto")
    suspend fun eliminarFoto(): Usuario

    // ---------- Oportunidades ----------
    @GET("oportunidades")
    suspend fun oportunidades(
        @Query("q") q: String? = null,
        @Query("region") region: String? = null,
        @Query("limit") limit: Int? = null,
    ): List<Oportunidad>

    @GET("oportunidades/{id}")
    suspend fun oportunidad(@Path("id") id: Int): Oportunidad

    /** Administrador: todos los voluntariados de su organización; tutor: los que tiene asignados. */
    @GET("oportunidades/mias")
    suspend fun oportunidadesMias(@Query("org_id") orgId: Int? = null): List<Oportunidad>

    /** Voluntariados donde el usuario es encargado/tutor (designado desde la web o la app). */
    @GET("oportunidades/tutorias")
    suspend fun tutorias(): List<Oportunidad>

    @GET("oportunidades/{id}/encargados")
    suspend fun encargados(@Path("id") id: Int): List<Encargado>

    /** Reemplaza la lista de encargados (misma columna `encargados_ids` que usa la web). */
    @PUT("oportunidades/{id}/encargados")
    suspend fun guardarEncargados(@Path("id") id: Int, @Body body: EncargadosBody): List<Encargado>

    @POST("oportunidades/{id}/postular")
    suspend fun postular(@Path("id") id: Int): PostularResponse

    // ---------- Organización (integrantes) ----------
    /** Todas las organizaciones del usuario: la que administra y las donde es miembro. */
    @GET("organizaciones/mias")
    suspend fun misOrganizaciones(): List<OrgResumen>

    /** Sin `orgId`: la organización principal. Con `orgId`: otra organización donde es miembro. */
    @GET("organizaciones/mia")
    suspend fun miOrganizacion(@Query("org_id") orgId: Int? = null): OrganizacionDetalle

    /** Solo el administrador de la organización. */
    @PUT("organizaciones/mia")
    suspend fun actualizarOrganizacion(@Body body: OrganizacionDetalle): OrganizacionDetalle

    /** Administrador y miembros de la organización del usuario. */
    @GET("organizaciones/mia/integrantes")
    suspend fun integrantes(@Query("org_id") orgId: Int? = null): List<Integrante>

    @GET("oportunidades/{id}/postulaciones")
    suspend fun postulantes(@Path("id") id: Int): List<PostulacionOrg>

    @PATCH("postulaciones/{id}/asistencia")
    suspend fun registrarAsistencia(@Path("id") id: Int, @Body body: AsistenciaBody): PostulacionOrg

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

    // --- Foros por voluntariado (compartidos con la plataforma) ---
    @GET("foros")
    suspend fun foros(): List<ForoCanal>

    @GET("oportunidades/{id}/foro")
    suspend fun foroMensajes(@Path("id") id: Int, @Query("canal") canal: String): List<ForoMensaje>

    @POST("oportunidades/{id}/foro")
    suspend fun publicarEnForo(@Path("id") id: Int, @Body body: ForoMensajeBody): ForoMensaje
}
