package com.injuv.voluntariado.data.api

import kotlinx.serialization.Serializable

/*
 * Modelos de la API FastAPI de INJUV (src/injuv/services/serializers.py en el repo web).
 * Los nombres de propiedad son los mismos campos JSON que devuelve el backend, para no
 * necesitar @SerialName. Los campos que la app no usa se ignoran (ignoreUnknownKeys).
 */

@Serializable
data class OrganizacionRef(
    val id: Int,
    val nombre: String? = null,
    /** "admin" = administrador de la organización, "miembro" = miembro del equipo. */
    val rol: String? = null,
)

@Serializable
data class Usuario(
    val id: Int,
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val rut: String = "",
    val telefono: String = "",
    val region: String = "",
    val ciudad: String = "",
    val comuna: String = "",
    val fecha_nacimiento: String? = null,
    /** "user" | "organizacion" | "admin" */
    val rol: String = "user",
    val horas_voluntariado: Double = 0.0,
    val tiene_foto: Boolean = false,
    val foto_version: String? = null,
    val created_at: String? = null,
    val organizacion: OrganizacionRef? = null,
) {
    val nombreCompleto: String get() = listOf(nombre, apellido).filter { it.isNotBlank() }.joinToString(" ").ifBlank { email }
}

@Serializable
data class TokenResponse(
    val access_token: String,
    val token_type: String = "bearer",
    val user: Usuario,
)

@Serializable
data class LoginBody(val email: String, val password: String)

@Serializable
data class RegisterBody(
    val nombre: String,
    val apellido: String,
    val email: String,
    val rut: String,
    /** yyyy-MM-dd */
    val fecha_nacimiento: String,
    val password: String,
)

@Serializable
data class MiPostulacion(val id: Int, val estado: String)

@Serializable
data class Oportunidad(
    val id: Int,
    val titulo: String = "",
    val descripcion: String = "",
    val organizacion_id: Int = 0,
    val organizacion_nombre: String = "",
    val organizacion_tiene_logo: Boolean = false,
    val meta_postulantes: Int? = null,
    val cupo_maximo: Int? = null,
    val fecha_limite_postulacion: String? = null,
    val fecha_inicio_voluntariado: String? = null,
    val fecha_fin_voluntariado: String? = null,
    val horas_voluntariado: Int? = null,
    /** "activa" | "abierta" | "cerrada" */
    val estado: String = "activa",
    val responsable_nombre: String = "",
    val responsable_apellido: String = "",
    val responsable_email: String = "",
    val responsable_telefono: String = "",
    val region: String = "",
    val ciudad: String = "",
    val comuna: String = "",
    val tipo_de_voluntariado: String = "",
    val num_postulaciones: Int = 0,
    /** Solo viene en GET /oportunidades/{id} cuando hay sesión. */
    val mi_postulacion: MiPostulacion? = null,
) {
    val ubicacion: String get() = listOf(comuna, region).filter { it.isNotBlank() }.joinToString(", ").ifBlank { "Chile" }
    val cerrada: Boolean get() = estado == "cerrada"
}

@Serializable
data class PostularResponse(val id: Int, val estado: String)

/** GET /usuarios/me/postulaciones */
@Serializable
data class PostulacionUsuario(
    val id: Int,
    val oportunidad_id: Int,
    val estado: String = "Pendiente de revisión",
    /** "Pendiente" | "Confirmado" | "No confirmado" */
    val estado_confirmacion: String = "Pendiente",
    val motivo_no_seleccion: String = "",
    val horas_voluntariado: Double = 0.0,
    val tiene_certificado: Boolean = false,
    val calificacion_usuario: Double? = null,
    val rechazo_participacion: Boolean = false,
    val voluntariado_terminado: Boolean = false,
    val oportunidad_titulo: String = "",
    val oportunidad_estado: String = "",
    val organizacion_id: Int? = null,
    val organizacion_nombre: String = "",
    val created_at: String? = null,
    val updated_at: String? = null,
)

@Serializable
data class ConfirmacionBody(val estado_confirmacion: String)

@Serializable
data class Noticia(
    val id: Int,
    val titulo: String = "",
    val contenido: String = "",
    val resumen: String = "",
    val autor_nombre: String = "INJUV",
    /** Nombre del archivo; la URL es /noticias/imagen/{imagen}. */
    val imagen: String? = null,
    val fecha_publicacion: String? = null,
)

@Serializable
data class Tematica(val id: Int, val nombre: String)

/** Documentos de Biblioteca (GET /biblioteca/documentos) y Academia (GET /academia/documentos). */
@Serializable
data class Documento(
    val id: Int,
    /** "biblioteca" | "academia" */
    val seccion: String = "biblioteca",
    val nombre_archivo: String = "",
    val archivo_mime: String? = null,
    val archivo_tamano_bytes: Long? = null,
    val autor: String = "",
    val descripcion: String = "",
    val fecha_edicion: String? = null,
    val created_at: String? = null,
    val tematica_id: Int? = null,
    val tematica_nombre: String? = null,
    /** Solo Academia */
    val categoria: String? = null,
    val organizacion_nombre: String? = null,
)

/** GET /public/estadisticas */
@Serializable
data class Estadisticas(
    val voluntarios: Int = 0,
    val organizaciones: Int = 0,
    val oportunidades: Int = 0,
    val horas: Int = 0,
)
