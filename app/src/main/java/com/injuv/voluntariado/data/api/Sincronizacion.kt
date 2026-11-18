package com.injuv.voluntariado.data.api

import android.content.Context
import android.net.Uri
import com.injuv.voluntariado.data.OrgProfile
import com.injuv.voluntariado.data.ForumMessage
import com.injuv.voluntariado.data.UserProfile
import java.text.SimpleDateFormat
import java.util.Locale
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

/*
 * Operaciones que antes se guardaban solo en el teléfono y ahora van al backend de la
 * plataforma (misma API y base de datos que la web).
 */

/** Registra la asistencia a la actividad ("SI" / "No") solo de quienes cambiaron. */
suspend fun guardarAsistencia(postulantes: List<PostulacionOrg>, presentes: Set<Int>) {
    for (p in postulantes) {
        val nuevo = if (p.id in presentes) "SI" else "No"
        if (p.asistencia_actividad != nuevo) {
            ApiClient.call { registrarAsistencia(p.id, AsistenciaBody(asistencia_actividad = nuevo)) }
        }
    }
}

/** Presentes según el backend; si aún no se pasa lista, todos parten marcados (como antes en la app). */
fun presentesIniciales(postulantes: List<PostulacionOrg>): Set<Int> =
    if (postulantes.all { it.asistencia_actividad == "No aplica" }) postulantes.map { it.id }.toSet()
    else postulantes.filter { it.asistencia_actividad == "SI" }.map { it.id }.toSet()

/**
 * Presentes guardados en el historial de una fecha; `null` si ese día nunca se pasó lista.
 * Solo devuelve ids que siguen en la lista actual.
 */
suspend fun asistenciaDelDia(oportunidadId: Int, fecha: String, roster: List<PostulacionOrg>): Set<Int>? {
    val registros = ApiClient.call { asistencias(oportunidadId, fecha) }
    if (registros.isEmpty()) return null
    val validos = roster.map { it.id }.toSet()
    return registros.filter { it.presente && it.postulacion_id in validos }.map { it.postulacion_id }.toSet()
}

/** Guarda la lista de un día en el historial; el backend además actualiza la asistencia a la actividad. */
suspend fun guardarAsistenciaDia(oportunidadId: Int, fecha: String, roster: List<PostulacionOrg>, presentes: Set<Int>) {
    ApiClient.call {
        guardarAsistencias(
            oportunidadId,
            AsistenciaDiaBody(fecha, roster.map { AsistenciaRegistro(it.id, it.id in presentes) }),
        )
    }
}

/**
 * Guarda en el backend los datos de la cuenta (nombre, correo, teléfono, ubicación y foto).
 * "Sobre mí", habilidades y privacidad no existen en la plataforma y siguen solo en el teléfono.
 */
suspend fun guardarPerfil(context: Context, usuario: Usuario, antes: UserProfile, despues: UserProfile): Usuario {
    val (nombre, apellido) = separarNombre(despues.name.trim(), usuario)
    var actualizado = ApiClient.call {
        actualizarPerfil(
            PerfilUsuarioBody(
                nombre = nombre,
                apellido = apellido,
                email = despues.email.trim(),
                telefono = despues.phone.trim().ifBlank { null },
                region = despues.region.trim().ifBlank { null },
                ciudad = despues.city.trim().ifBlank { null },
                comuna = despues.commune.trim().ifBlank { null },
                sexo = usuario.sexo.ifBlank { null },
                fecha_nacimiento = usuario.fecha_nacimiento,
            ),
        )
    }
    if (despues.photoUri != antes.photoUri) {
        val uri = despues.photoUri
        actualizado = if (uri != null) {
            ApiClient.call { subirFoto(fotoComoParte(context, Uri.parse(uri))) }
        } else if (usuario.tiene_foto) {
            ApiClient.call { eliminarFoto() }
        } else {
            actualizado
        }
    }
    return actualizado
}

private fun separarNombre(completo: String, usuario: Usuario): Pair<String, String> {
    if (completo == usuario.nombreCompleto) return usuario.nombre to usuario.apellido
    val apellido = usuario.apellido.trim()
    if (apellido.isNotEmpty() && completo.endsWith(" $apellido")) {
        return completo.removeSuffix(" $apellido").trim() to apellido
    }
    val partes = completo.split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        partes.size >= 2 -> partes.first() to partes.drop(1).joinToString(" ")
        else -> completo to apellido.ifBlank { completo }
    }
}

/**
 * Sube un certificado personal (`antecedentes` / `inhabilidades`) como PDF. La plataforma
 * exige `fecha_emision` (yyyy-MM-dd) de los últimos 6 meses y reemplaza el del mismo tipo.
 */
suspend fun subirCertPersonal(context: Context, tipo: String, fechaEmision: String, uri: Uri): Usuario {
    val resolver = context.contentResolver
    val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
        ?: throw ApiException("No se pudo leer el archivo seleccionado.")
    val nombre = resolver.query(uri, null, null, null, null)?.use { cursor ->
        val idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        if (idx >= 0 && cursor.moveToFirst()) cursor.getString(idx) else null
    }?.takeIf { it.isNotBlank() } ?: "certificado.pdf"
    val texto = "text/plain".toMediaTypeOrNull()
    val parte = MultipartBody.Part.createFormData("archivo", nombre, bytes.toRequestBody("application/pdf".toMediaTypeOrNull()))
    return ApiClient.call {
        subirCertificadoPersonal(tipo.toRequestBody(texto), fechaEmision.toRequestBody(texto), parte)
    }
}

private fun fotoComoParte(context: Context, uri: Uri): MultipartBody.Part {
    val resolver = context.contentResolver
    val mime = resolver.getType(uri) ?: "image/jpeg"
    val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
        ?: throw ApiException("No se pudo leer la foto seleccionada.")
    val extension = mime.substringAfter("/", "jpg").replace("jpeg", "jpg")
    return MultipartBody.Part.createFormData("imagen", "foto.$extension", bytes.toRequestBody(mime.toMediaTypeOrNull()))
}

fun OrganizacionDetalle.aOrgProfile(): OrgProfile = OrgProfile(
    name = nombre,
    about = descripcion,
    email = email_contacto,
    phone = telefono_contacto,
    website = sitio_web,
)

fun OrganizacionDetalle.conCambios(org: OrgProfile): OrganizacionDetalle = copy(
    nombre = org.name,
    descripcion = org.about,
    email_contacto = org.email,
    telefono_contacto = org.phone,
    sitio_web = org.website,
)

/** Mensaje del foro (API) al modelo de la pantalla; marca a los tutores junto a su nombre. */
fun ForoMensaje.aForumMessage(): ForumMessage = ForumMessage(
    id = id,
    author = if (es_tutor) "$autor_nombre · Tutor" else autor_nombre,
    text = texto,
    date = fechaCorta(created_at),
)

private fun fechaCorta(iso: String?): String {
    if (iso.isNullOrBlank()) return ""
    return runCatching {
        val fecha = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(iso.take(19))!!
        SimpleDateFormat("d MMM, HH:mm", Locale("es", "CL")).format(fecha)
    }.getOrDefault(iso)
}
