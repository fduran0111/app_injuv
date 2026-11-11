package com.injuv.voluntariado.data.api

import java.util.Locale

/*
 * Utilidades de formato sin java.time (la app soporta Android 7, API 24).
 * La API entrega fechas como "2026-11-10" o "2026-11-10T14:30:00".
 */

private val MESES = listOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")

/** "2026-11-10" → "10 nov 2026". Devuelve [vacio] si no hay fecha válida. */
fun formatearFecha(iso: String?, vacio: String = "—"): String {
    val partes = iso?.take(10)?.split("-") ?: return vacio
    if (partes.size != 3) return vacio
    val anio = partes[0]
    val mes = partes[1].toIntOrNull()?.takeIf { it in 1..12 } ?: return vacio
    val dia = partes[2].toIntOrNull() ?: return vacio
    return "$dia ${MESES[mes - 1]} $anio"
}

/** Año de una fecha ISO ("2026-11-10" → "2026"). */
fun anioDe(iso: String?): String = iso?.take(4)?.takeIf { it.length == 4 } ?: ""

/** "10 nov – 20 nov 2026" o una sola fecha si inicio = término. */
fun formatearPeriodo(inicio: String?, fin: String?): String = when {
    inicio == null && fin == null -> "Fechas por confirmar"
    inicio == null || inicio == fin -> formatearFecha(fin)
    fin == null -> "Desde ${formatearFecha(inicio)}"
    else -> "${formatearFecha(inicio)} – ${formatearFecha(fin)}"
}

/** 1536000 → "1,5 MB". */
fun formatearTamano(bytes: Long?): String {
    if (bytes == null || bytes <= 0) return ""
    val kb = bytes / 1024.0
    return if (kb < 1024) String.format(Locale("es", "CL"), "%.0f KB", kb)
    else String.format(Locale("es", "CL"), "%.1f MB", kb / 1024)
}

/** Tipo de archivo legible a partir del MIME o del nombre ("PDF", "DOCX", "MP4"…). */
fun tipoArchivo(mime: String?, nombre: String): String {
    val ext = nombre.substringAfterLast('.', "").uppercase()
    if (ext.isNotBlank() && ext.length <= 5) return ext
    return when {
        mime == null -> "Archivo"
        "pdf" in mime -> "PDF"
        mime.startsWith("video") -> "Video"
        mime.startsWith("image") -> "Imagen"
        else -> "Archivo"
    }
}

/** Minutos de lectura estimados (≈200 palabras por minuto). */
fun minutosLectura(texto: String): Int = (texto.split(Regex("\\s+")).size / 200).coerceAtLeast(1)

fun formatearHoras(horas: Double): String = if (horas % 1.0 == 0.0) horas.toInt().toString() else String.format(Locale("es", "CL"), "%.1f", horas)

/** Normaliza el RUT a solo dígitos y K (el backend lo valida igual). */
fun limpiarRut(rut: String): String = rut.filter { it.isDigit() || it == 'k' || it == 'K' }.uppercase()

/** Valida el dígito verificador del RUT chileno. */
fun rutValido(rut: String): Boolean {
    val limpio = limpiarRut(rut)
    if (limpio.length !in 8..9) return false
    val cuerpo = limpio.dropLast(1)
    if (!cuerpo.all { it.isDigit() }) return false
    var suma = 0
    var factor = 2
    for (c in cuerpo.reversed()) {
        suma += (c - '0') * factor
        factor = if (factor == 7) 2 else factor + 1
    }
    val esperado = when (val r = 11 - suma % 11) {
        11 -> '0'
        10 -> 'K'
        else -> ('0' + r)
    }
    return limpio.last() == esperado
}
