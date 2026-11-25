package com.injuv.voluntariado.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.components.EstadoCarga
import com.injuv.voluntariado.components.EstadoPostulacionChip
import com.injuv.voluntariado.components.descargarArchivo
import com.injuv.voluntariado.components.rememberCarga
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.data.api.ResenaOrganizacion
import com.injuv.voluntariado.data.api.formatearFecha
import com.injuv.voluntariado.components.TituloSeccion
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.InstrumentSans
import com.injuv.voluntariado.ui.theme.Bricolage

/**
 * Perfil público de una organización (GET /organizaciones/{id}): banner, logo, datos,
 * certificaciones, sus convocatorias y las reseñas que dejaron los voluntarios.
 */
@Composable
fun OrgProfileScreen(
    organizacionId: Int,
    onBack: () -> Unit,
    onOpenOportunidad: (Int) -> Unit = {},
) {
    val T = LocalAppColors.current
    val context = LocalContext.current
    val carga = rememberCarga(organizacionId) {
        val org = ApiClient.call { organizacionDetalle(organizacionId) }
        val resenas = runCatching { ApiClient.call { organizacionResenas(organizacionId) } }.getOrDefault(emptyList())
        val oportunidades = runCatching { ApiClient.call { oportunidades() } }
            .getOrDefault(emptyList())
            .filter { it.organizacion_id == organizacionId }
        Triple(org, resenas, oportunidades)
    }

    EstadoCarga(carga, modifier = Modifier.fillMaxSize()) { (org, resenas, oportunidades) ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(T.bgBase)
                .verticalScroll(rememberScrollState()),
        ) {
            // ---------- Portada: banner de la ONG o degradado, con el logo encima ----------
            Box {
                if (org.tiene_banner) {
                    AsyncImage(
                        model = ApiClient.bannerOrganizacion(org.id),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Brush.linearGradient(T.bgHeaderGradient)),
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Brush.verticalGradient(listOf(Color.Transparent, T.bgBase))),
                )
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.35f)),
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .offset(y = (-36).dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(T.surface1),
                    contentAlignment = Alignment.Center,
                ) {
                    if (org.tiene_logo) {
                        AsyncImage(
                            model = ApiClient.logoOrganizacion(org.id),
                            contentDescription = org.nombre,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                        )
                    } else {
                        Text(
                            org.nombre.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString("").uppercase(),
                            fontFamily = Bricolage,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = T.coral,
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
                Text(org.nombre, fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = T.textPrimary)
                if (org.siglas_nombre.isNotBlank()) {
                    Text(org.siglas_nombre, fontFamily = InstrumentSans, fontSize = 13.sp, color = T.textMuted)
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    listOfNotNull(
                        org.area_trabajo.ifBlank { null },
                        org.tipo_org.ifBlank { null },
                        listOf(org.comuna, org.region).filter { it.isNotBlank() }.joinToString(", ").ifBlank { null },
                    ).forEach { tag ->
                        Text(
                            tag,
                            fontFamily = InstrumentSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = T.coral,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(T.coral.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // ---------- Estadísticas ----------
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(T.card)
                        .border(1.dp, T.border1, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    OrgStat(Icons.Filled.Star, org.calificacion_promedio?.let { "%.1f".format(it) } ?: "—", "Calificación")
                    OrgStat(Icons.Filled.VolunteerActivism, org.num_oportunidades.toString(), "Convocatorias")
                    OrgStat(Icons.Filled.People, org.total_postulaciones.toString(), "Postulaciones")
                }

                if (org.descripcion.isNotBlank() || org.descripcion_breve.isNotBlank()) {
                    Spacer(Modifier.height(20.dp))
                    SeccionOrg("Sobre la organización")
                    Spacer(Modifier.height(6.dp))
                    Text(
                        org.descripcion.ifBlank { org.descripcion_breve },
                        fontFamily = InstrumentSans,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = T.textSecondary,
                    )
                }

                // ---------- Contacto ----------
                val hayContacto = org.email_contacto.isNotBlank() || org.telefono_contacto.isNotBlank() ||
                    org.sitio_web.isNotBlank() || org.redes_sociales.isNotEmpty()
                if (hayContacto) {
                    Spacer(Modifier.height(20.dp))
                    SeccionOrg("Contacto")
                    Spacer(Modifier.height(8.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(T.card)
                            .border(1.dp, T.border1, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                    ) {
                        if (org.email_contacto.isNotBlank()) {
                            ContactoFila(Icons.Filled.Email, org.email_contacto) {
                                runCatching { context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${org.email_contacto}"))) }
                            }
                        }
                        if (org.telefono_contacto.isNotBlank()) {
                            ContactoFila(Icons.Filled.Phone, org.telefono_contacto) {
                                runCatching { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${org.telefono_contacto}"))) }
                            }
                        }
                        if (org.sitio_web.isNotBlank()) {
                            ContactoFila(Icons.Filled.Language, org.sitio_web.replace(Regex("^https?://"), "")) {
                                val url = if (org.sitio_web.startsWith("http")) org.sitio_web else "https://${org.sitio_web}"
                                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
                            }
                        }
                        org.redes_sociales.forEach { red ->
                            ContactoFila(Icons.Filled.Share, red.replace(Regex("^https?://"), "")) {
                                val url = if (red.startsWith("http")) red else "https://$red"
                                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
                            }
                        }
                    }
                }

                // ---------- Certificaciones ----------
                if (org.certificacion.isNotEmpty()) {
                    Spacer(Modifier.height(20.dp))
                    SeccionOrg("Certificaciones")
                    Spacer(Modifier.height(8.dp))
                    org.certificacion.forEach { cert ->
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(T.card)
                                .border(1.dp, T.border1, RoundedCornerShape(14.dp))
                                .clickable {
                                    descargarArchivo(context, ApiClient.certificacionOrganizacion(org.id, cert.archivo), cert.nombre.ifBlank { cert.archivo })
                                }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Filled.Verified, contentDescription = null, tint = T.green, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(cert.nombre.ifBlank { cert.archivo }, fontFamily = InstrumentSans, fontSize = 13.sp, color = T.textPrimary, modifier = Modifier.weight(1f))
                            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = T.textSubtle)
                        }
                    }
                }

                // ---------- Sus convocatorias ----------
                if (oportunidades.isNotEmpty()) {
                    Spacer(Modifier.height(20.dp))
                    SeccionOrg("Sus convocatorias")
                    Spacer(Modifier.height(8.dp))
                    oportunidades.forEach { op ->
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(T.card)
                                .border(1.dp, T.border1, RoundedCornerShape(14.dp))
                                .clickable { onOpenOportunidad(op.id) }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Filled.Work, contentDescription = null, tint = T.coral, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(op.titulo, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = T.textPrimary)
                                if (op.comuna.isNotBlank() || op.region.isNotBlank()) {
                                    Text(
                                        listOf(op.comuna, op.region).filter { it.isNotBlank() }.joinToString(", "),
                                        fontFamily = InstrumentSans,
                                        fontSize = 11.sp,
                                        color = T.textMuted,
                                    )
                                }
                            }
                            EstadoPostulacionChip(if (op.cerrada) "Cerrada" else "Abierta")
                        }
                    }
                }

                // ---------- Reseñas públicas ----------
                Spacer(Modifier.height(20.dp))
                SeccionOrg("Reseñas de voluntarios")
                Spacer(Modifier.height(8.dp))
                if (resenas.isEmpty()) {
                    Text(
                        "Aún no hay reseñas públicas de voluntarios.",
                        fontFamily = InstrumentSans,
                        fontSize = 13.sp,
                        color = T.textMuted,
                    )
                } else {
                    resenas.forEach { r -> ResenaOrgCard(r) }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun OrgStat(icon: ImageVector, value: String, label: String) {
    val T = LocalAppColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = T.coral, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = T.textPrimary, maxLines = 1)
        Text(label, fontFamily = InstrumentSans, fontSize = 10.sp, color = T.textMuted)
    }
}

@Composable
private fun SeccionOrg(texto: String) {
    val T = LocalAppColors.current
    TituloSeccion(texto)
}

@Composable
private fun ContactoFila(icon: ImageVector, texto: String, onClick: () -> Unit) {
    val T = LocalAppColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
    ) {
        Icon(icon, contentDescription = null, tint = T.coral, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(10.dp))
        Text(texto, fontFamily = InstrumentSans, fontSize = 13.sp, color = T.textSecondary)
    }
}

@Composable
private fun ResenaOrgCard(r: ResenaOrganizacion) {
    val T = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(T.card)
            .border(1.dp, T.border1, RoundedCornerShape(14.dp))
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                r.usuario_nombre.ifBlank { "Voluntario" },
                fontFamily = InstrumentSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = T.textPrimary,
                modifier = Modifier.weight(1f),
            )
            r.calificacion?.let { nota ->
                Row {
                    repeat(5) { i ->
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (i < nota.toInt()) T.yellow else T.textSubtle.copy(alpha = 0.4f),
                            modifier = Modifier.size(13.dp),
                        )
                    }
                }
            }
        }
        if (r.oportunidad_titulo.isNotBlank()) {
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = T.textSubtle, modifier = Modifier.size(11.dp))
                Spacer(Modifier.width(4.dp))
                Text(r.oportunidad_titulo, fontFamily = InstrumentSans, fontSize = 11.sp, color = T.textMuted)
            }
        }
        if (r.resena.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(r.resena, fontFamily = InstrumentSans, fontSize = 13.sp, lineHeight = 18.sp, color = T.textSecondary)
        }
        r.fecha?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(6.dp))
            Text(formatearFecha(it), fontFamily = InstrumentSans, fontSize = 10.sp, color = T.textSubtle)
        }
    }
}
