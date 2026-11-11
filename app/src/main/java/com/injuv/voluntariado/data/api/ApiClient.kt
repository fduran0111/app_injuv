package com.injuv.voluntariado.data.api

import com.injuv.voluntariado.BuildConfig
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Error de la API con un mensaje listo para mostrar al usuario. */
class ApiException(message: String, val code: Int? = null) : Exception(message)

object ApiClient {

    /** Termina en "/". Se define por tipo de build en app/build.gradle.kts. */
    val baseUrl: String = BuildConfig.API_BASE_URL

    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
        isLenient = true
    }

    /** Agrega `Authorization: Bearer <token>`; si la API responde 401 con token, la sesión expiró. */
    private val authInterceptor = Interceptor { chain ->
        val token = SessionManager.token
        val request = if (token != null) {
            chain.request().newBuilder().header("Authorization", "Bearer $token").build()
        } else {
            chain.request()
        }
        val response = chain.proceed(request)
        if (response.code == 401 && token != null) SessionManager.onSessionExpired()
        response
    }

    private val http = OkHttpClient.Builder()
        // Render (plan gratis) tarda ~1 minuto en despertar la API la primera vez.
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(70, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            }
        }
        .build()

    val api: InjuvApi = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(http)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(InjuvApi::class.java)

    // ---------- URLs de archivos (imágenes y descargas) ----------
    fun fotoUsuario(userId: Int, version: String?): String = "${baseUrl}usuarios/$userId/foto" + (version?.let { "?v=$it" } ?: "")
    fun logoOrganizacion(orgId: Int): String = "${baseUrl}organizaciones/$orgId/logo"
    fun imagenNoticia(archivo: String): String = "${baseUrl}noticias/imagen/$archivo"
    fun descargaDocumento(doc: Documento): String = "${baseUrl}repositorio/${doc.seccion}/${doc.id}/descargar"
    fun certificadoPostulacion(postulacionId: Int): String = "${baseUrl}postulaciones/$postulacionId/certificado"

    /**
     * Ejecuta una llamada y traduce los errores a [ApiException] con el mensaje del backend
     * (`{"detail": "..."}`) o uno legible si no hay conexión.
     */
    suspend fun <T> call(block: suspend InjuvApi.() -> T): T = try {
        api.block()
    } catch (e: HttpException) {
        throw ApiException(mensajeDeError(e), e.code())
    } catch (e: IOException) {
        throw ApiException("No se pudo conectar con el servidor. Revisa tu conexión e inténtalo de nuevo.")
    }

    private fun mensajeDeError(e: HttpException): String {
        val body = runCatching { e.response()?.errorBody()?.string() }.getOrNull()
        val detail = body?.let { runCatching { json.parseToJsonElement(it).jsonObject["detail"] }.getOrNull() }
        val texto = when (detail) {
            is JsonPrimitive -> detail.contentOrNull
            // Errores de validación de FastAPI: [{"msg": "...", ...}]
            is JsonArray -> detail.firstOrNull()?.let { (it as? JsonObject)?.get("msg")?.jsonPrimitive?.contentOrNull }
            else -> null
        }
        return texto ?: when (e.code()) {
            401 -> "Tu sesión expiró. Vuelve a iniciar sesión."
            403 -> "No tienes permisos para esta acción."
            404 -> "No encontramos lo que buscabas."
            in 500..599 -> "El servidor tuvo un problema. Inténtalo más tarde."
            else -> "Ocurrió un error (${e.code()})."
        }
    }
}
