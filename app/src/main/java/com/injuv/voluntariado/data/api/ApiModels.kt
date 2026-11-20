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
    val sexo: String = "",
    val fecha_nacimiento: String? = null,
    /** "user" | "organizacion" | "admin" */
    val rol: String = "user",
    val horas_voluntariado: Double = 0.0,
    val tiene_foto: Boolean = false,
    val foto_version: String? = null,
    val created_at: String? = null,
    val organizacion: OrganizacionRef? = null,
    val certificados_personales: List<CertificadoPersonal> = emptyList(),
) {
    val nombreCompleto: String get() = listOf(nombre, apellido).filter { it.isNotBlank() }.joinToString(" ").ifBlank { email }
}

/** Certificado personal del usuario (en `Usuario.certificados_personales`). */
@Serializable
data class CertificadoPersonal(
    /** "antecedentes" | "inhabilidades" */
    val tipo: String = "",
    val fecha_emision: String = "",
    val nombre_archivo: String = "",
    val subido_en: String? = null,
)

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
data class MiPostulacion(val id: Int, val estado: String, val estado_confirmacion: String = "Pendiente")

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
    val tiene_imagen: Boolean = false,
    /** Preguntas que la organización pide responder al postular. */
    val preguntas: List<PreguntaItem> = emptyList(),
    /** IDs de los encargados/tutores (los mismos que se designan en la web). */
    val encargados_ids: List<Int> = emptyList(),
    /** Solo viene en GET /oportunidades/{id} cuando hay sesión. */
    val mi_postulacion: MiPostulacion? = null,
) {
    val ubicacion: String get() = listOf(comuna, region).filter { it.isNotBlank() }.joinToString(", ").ifBlank { "Chile" }
    val cerrada: Boolean get() = estado == "cerrada"
}

/** GET/PUT /oportunidades/{id}/encargados. `miembro = false` = tutor externo a la organización. */
@Serializable
data class Encargado(
    val id: Int,
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val miembro: Boolean = false,
) {
    val nombreCompleto: String get() = listOf(nombre, apellido).filter { it.isNotBlank() }.joinToString(" ").ifBlank { email }
}

@Serializable
data class EncargadosBody(val encargados_ids: List<Int>)

@Serializable
data class PostularResponse(val id: Int, val estado: String)

/** Pregunta que la organización agrega a la convocatoria para filtrar postulantes. */
@Serializable
data class PreguntaItem(
    val id: String = "",
    val texto: String = "",
    /** "si_no" | "alternativas" | "texto" */
    val tipo: String = "si_no",
    val opciones: List<String> = emptyList(),
    val obligatoria: Boolean = true,
)

/** POST /oportunidades/{id}/postular — respuestas a las preguntas: {"<id pregunta>": "respuesta"}. */
@Serializable
data class PostularBody(
    val respuestas: Map<String, String> = emptyMap(),
)

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
    /** Reseña y nota que la organización dejó sobre el voluntario (visibles si `resena_org_publica`). */
    val resena_org: String = "",
    val resena_org_publica: Boolean = false,
    val calificacion_org: Double? = null,
    /** Reseña y nota que el voluntario dejó sobre la organización. */
    val resena_usuario: String = "",
    val resena_usuario_publica: Boolean = true,
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

/** PUT /postulaciones/{id}/resena-usuario: evaluación del voluntario hacia la organización. */
@Serializable
data class ResenaUsuarioBody(val resena: String, val calificacion: Double, val es_publica: Boolean)

/** Voluntariado terminado donde la ONG ya evaluó y el voluntario aún no deja su reseña. */
fun PostulacionUsuario.pendienteCalificarOrg(): Boolean =
    voluntariado_terminado && estado == "Seleccionado" && !rechazo_participacion &&
        resena_usuario.isBlank() && (resena_org.isNotBlank() || calificacion_org != null)

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

/** PUT /usuarios/me (todos los campos se reemplazan). */
@Serializable
data class PerfilUsuarioBody(
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String?,
    val region: String?,
    val ciudad: String?,
    val comuna: String?,
    val sexo: String?,
    val fecha_nacimiento: String?,
)

/**
 * GET/PUT /organizaciones/mia. Se usa también como cuerpo del PUT: el backend reemplaza todos
 * los campos, así que se edita una copia de lo que devolvió el GET.
 */
@Serializable
data class OrganizacionDetalle(
    val id: Int = 0,
    val nombre: String = "",
    val siglas_nombre: String = "",
    val email_contacto: String = "",
    val telefono_contacto: String = "",
    val region: String = "",
    val ciudad: String = "",
    val comuna: String = "",
    val descripcion: String = "",
    val descripcion_breve: String = "",
    val area_trabajo: String = "",
    val tipo_org: String = "",
    val sitio_web: String = "",
    val redes_sociales: List<String> = emptyList(),
    val experiencia_anios: Int? = null,
    val voluntarios_anuales: String = "",
    val tiene_logo: Boolean = false,
    /** Campos del perfil público (GET /organizaciones/{id}). */
    val tiene_banner: Boolean = false,
    val logo_version: String? = null,
    val banner_version: String? = null,
    val rut: String = "",
    val num_oportunidades: Int = 0,
    val total_postulaciones: Int = 0,
    val calificacion_promedio: Double? = null,
    val certificacion: List<CertificacionOrg> = emptyList(),
    val fecha_creacion: String? = null,
)

/** Certificación/documento oficial que la organización subió a su perfil. */
@Serializable
data class CertificacionOrg(
    val nombre: String = "",
    val archivo: String = "",
    val subido_en: String? = null,
)

/** GET /organizaciones/{id}/resenas — reseña pública que un voluntario dejó a la ONG. */
@Serializable
data class ResenaOrganizacion(
    val postulacion_id: Int = 0,
    val oportunidad_id: Int = 0,
    val oportunidad_titulo: String = "",
    val usuario_nombre: String = "",
    val resena: String = "",
    val calificacion: Double? = null,
    val es_publica: Boolean = true,
    val fecha: String? = null,
)

/** GET /organizaciones/mia/integrantes: administrador y miembros de la organización. */
@Serializable
data class Integrante(
    val id: Int,
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String = "",
    /** "admin" | "miembro" */
    val rol: String = "miembro",
) {
    val nombreCompleto: String get() = listOf(nombre, apellido).filter { it.isNotBlank() }.joinToString(" ").ifBlank { email }
}

/** GET /oportunidades/{id}/postulaciones (vista de la organización). */
@Serializable
data class PostulacionOrg(
    val id: Int,
    val usuario_id: Int? = null,
    val estado: String = "Pendiente de revisión",
    val estado_confirmacion: String = "Pendiente",
    /** "SI" | "No" | "No aplica" */
    val asistencia_capacitacion: String = "No aplica",
    val asistencia_actividad: String = "No aplica",
    val horas_voluntariado: Double = 0.0,
    val rechazo_participacion: Boolean = false,
    val usuario_nombre: String = "",
    val usuario_email: String = "",
) {
    /** Participa del voluntariado: seleccionado y no se retiró ni rechazó. */
    val participa: Boolean get() = estado == "Seleccionado" && !rechazo_participacion
}

/** PATCH /postulaciones/{id}/asistencia — cada campo "SI" | "No" | "No aplica"; null = no cambiar. */
@Serializable
data class AsistenciaBody(
    val asistencia_capacitacion: String? = null,
    val asistencia_actividad: String? = null,
)

/** GET/PUT /oportunidades/{id}/asistencias — historial de asistencia por fecha. */
@Serializable
data class AsistenciaRegistro(
    val postulacion_id: Int,
    val presente: Boolean = true,
)

@Serializable
data class AsistenciaDiaBody(
    val fecha: String,
    val registros: List<AsistenciaRegistro> = emptyList(),
)

@Serializable
data class AsistenciaDiaResponse(
    val fecha: String = "",
    val registrados: Int = 0,
)

/** GET /foros: voluntariados con foro (los que tutorea o en los que participa el usuario). */
@Serializable
data class ForoCanal(
    val oportunidad_id: Int,
    val titulo: String = "",
    val organizacion_nombre: String = "",
    val es_tutor: Boolean = false,
    val ultimo_mensaje: String? = null,
    val ultima_fecha: String? = null,
)

/** Mensaje del foro de un voluntariado; canal "anuncios" (solo tutores) o "chat". */
@Serializable
data class ForoMensaje(
    val id: Int,
    val canal: String = "chat",
    val autor_id: Int? = null,
    val autor_nombre: String = "",
    val es_tutor: Boolean = false,
    val texto: String = "",
    val created_at: String? = null,
)

@Serializable
data class ForoMensajeBody(val canal: String, val texto: String)
