package com.injuv.voluntariado.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.data.api.Documento
import com.injuv.voluntariado.data.api.Noticia
import com.injuv.voluntariado.data.api.Oportunidad
import com.injuv.voluntariado.data.api.formatearFecha
import com.injuv.voluntariado.data.api.formatearTamano
import com.injuv.voluntariado.data.api.minutosLectura
import com.injuv.voluntariado.data.api.tipoArchivo
import com.injuv.voluntariado.ui.theme.Bricolage
import com.injuv.voluntariado.ui.theme.InstrumentSans
import com.injuv.voluntariado.ui.theme.LocalAppColors

/*
 * Tarjetas de contenido de la web (OportunidadCard, NoticiaCard, DocumentoCard) en Compose.
 */

/** Tarjeta de oportunidad: franja 16:9 (foto o bloque del área) con etiquetas, y ficha técnica. */
@Composable
fun TarjetaOportunidad(
    oportunidad: Oportunidad,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    postulado: Boolean = oportunidad.mi_postulacion != null,
    compacta: Boolean = false,
) {
    val T = LocalAppColors.current
    val op = oportunidad
    Tarjeta(modifier = modifier, onClick = onClick) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
        ) {
            OportunidadImagen(op, modifier = Modifier.fillMaxSize(), mostrarArea = true)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                if (op.tipo_de_voluntariado.isNotBlank() && (op.tiene_imagen || op.organizacion_tiene_logo)) {
                    Etiqueta(op.tipo_de_voluntariado, Tono.Azul, solida = true, icono = iconoArea(op.tipo_de_voluntariado))
                } else {
                    Spacer(Modifier.size(1.dp))
                }
                EtiquetaPlazo(op.fecha_limite_postulacion, op.estado, postulado = postulado, solida = true)
            }
        }
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(
                op.titulo,
                fontFamily = Bricolage,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                lineHeight = 21.sp,
                letterSpacing = (-0.015).em,
                color = T.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (!compacta && op.descripcion.isNotBlank()) {
                Spacer(Modifier.height(5.dp))
                Text(
                    op.descripcion,
                    fontFamily = InstrumentSans,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = T.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(
                    op.organizacion_nombre.ifBlank { "Organización" },
                    url = if (op.organizacion_tiene_logo) ApiClient.logoOrganizacion(op.organizacion_id) else null,
                    size = 26.dp,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    op.organizacion_nombre,
                    fontFamily = InstrumentSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = T.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.height(12.dp))
            Divisor()
            Spacer(Modifier.height(12.dp))
            DatoIcono(Icons.Outlined.LocationOn, op.ubicacion)
            Spacer(Modifier.height(7.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                DatoIcono(
                    Icons.Outlined.CalendarMonth,
                    op.fecha_limite_postulacion?.let { "Hasta ${formatearFecha(it)}" } ?: "Sin fecha límite",
                    destacado = true,
                    modifier = Modifier.weight(1f, fill = false),
                )
                DatoIcono(Icons.Outlined.People, op.cupo_maximo?.let { "$it cupos" } ?: "Cupos abiertos")
                if (!compacta) op.horas_voluntariado?.let { DatoIcono(Icons.Outlined.Schedule, "$it h") }
            }
        }
    }
}

/** Imagen de noticia o, si no tiene, un panel azul suave con el ícono de periódico como marca de agua. */
@Composable
fun ImagenNoticia(news: Noticia, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Box(modifier = modifier.background(if (T.isDark) Color(0xFF16305C) else Color(0xFFE8F0FD))) {
        if (news.imagen != null) {
            AsyncImage(
                model = ApiClient.imagenNoticia(news.imagen),
                contentDescription = news.titulo,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Icon(
                Icons.Outlined.Newspaper,
                contentDescription = null,
                tint = Color(0xFF0052CC).copy(alpha = if (T.isDark) 0.35f else 0.15f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 18.dp, y = 22.dp)
                    .size(130.dp)
                    .rotate(-12f),
            )
            Text(
                "Noticia",
                fontFamily = Bricolage,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = if (T.isDark) Color(0xFFC9DCFF) else Color(0xFF003D99),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp),
            )
        }
    }
}

/** Píldora de fecha sobre las fotos (fecha de la noticia). */
@Composable
fun PildoraFecha(texto: String, modifier: Modifier = Modifier) {
    Text(
        texto,
        fontFamily = InstrumentSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        color = Color(0xFF0F1B2D),
        style = TextStyle(fontFeatureSettings = "tnum"),
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFF4EFE6).copy(alpha = 0.95f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

/** Tarjeta de noticia (NoticiaCard): imagen con fecha, título, resumen y "Leer noticia ↗". */
@Composable
fun TarjetaNoticia(news: Noticia, onClick: () -> Unit, modifier: Modifier = Modifier, destacada: Boolean = false) {
    val T = LocalAppColors.current
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Tarjeta(modifier = modifier) {
        Column(Modifier.clickable(interactionSource = interaction, indication = androidx.compose.material.ripple.rememberRipple(), role = Role.Button, onClick = onClick)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(if (destacada) 16f / 10f else 4f / 3f),
            ) {
                ImagenNoticia(news, Modifier.fillMaxSize())
                PildoraFecha(formatearFecha(news.fecha_publicacion), Modifier.padding(12.dp))
            }
            Column(Modifier.padding(16.dp)) {
                Text(
                    news.titulo,
                    fontFamily = Bricolage,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = if (destacada) 21.sp else 18.sp,
                    lineHeight = if (destacada) 24.sp else 21.sp,
                    letterSpacing = (-0.015).em,
                    color = if (pressed) T.action else T.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                val resumen = news.resumen.ifBlank { news.contenido }
                if (resumen.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        resumen,
                        fontFamily = InstrumentSans,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = T.textSecondary,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.height(14.dp))
                Divisor()
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Leer noticia", fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
                        Text(
                            "${news.autor_nombre} · ${minutosLectura(news.contenido)} min",
                            fontFamily = InstrumentSans,
                            fontSize = 12.sp,
                            color = T.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    FlechaCircular(activa = pressed)
                }
            }
        }
    }
}

/** Tarjeta de documento (DocumentoCard): ícono en recuadro, etiqueta, divisor punteado y "Descargar". */
@Composable
fun TarjetaDocumento(
    doc: Documento,
    onDescargar: () -> Unit,
    modifier: Modifier = Modifier,
    etiqueta: String? = doc.tematica_nombre,
    tonoEtiqueta: Tono = if (doc.seccion == "academia") Tono.Ambar else Tono.Azul,
    meta: String? = null,
) {
    val T = LocalAppColors.current
    val esVideo = doc.archivo_mime?.startsWith("video/") == true
    Tarjeta(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                val c = coloresTono(if (esVideo) Tono.Ambar else Tono.Azul, T)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(c.fondo)
                        .border(1.5.dp, T.textPrimary.copy(alpha = 0.75f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(if (esVideo) Icons.Outlined.PlayCircle else Icons.Outlined.Description, contentDescription = null, tint = c.texto, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    if (!etiqueta.isNullOrBlank()) {
                        Etiqueta(etiqueta, tonoEtiqueta)
                        Spacer(Modifier.height(6.dp))
                    }
                    Text(
                        doc.nombre_archivo,
                        fontFamily = Bricolage,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        lineHeight = 19.sp,
                        color = T.textPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (doc.descripcion.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(doc.descripcion, fontFamily = InstrumentSans, fontSize = 13.sp, lineHeight = 18.sp, color = T.textMuted, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(14.dp))
            Divisor(punteado = true)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    meta ?: listOf(
                        doc.organizacion_nombre ?: doc.autor,
                        tipoArchivo(doc.archivo_mime, doc.nombre_archivo),
                        formatearTamano(doc.archivo_tamano_bytes),
                        formatearFecha(doc.fecha_edicion ?: doc.created_at, ""),
                    ).filter { it.isNotBlank() }.joinToString(" · "),
                    fontFamily = InstrumentSans,
                    fontSize = 12.sp,
                    color = T.textMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(10.dp))
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(T.ink)
                        .clickable(role = Role.Button, onClick = onDescargar)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.Download, contentDescription = null, tint = T.onInk, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Descargar", fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = T.onInk)
                }
            }
        }
    }
}
