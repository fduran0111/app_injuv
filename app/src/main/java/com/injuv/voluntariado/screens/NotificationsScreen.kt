package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.injuv.voluntariado.components.EstadoPostulacionChip
import com.injuv.voluntariado.data.AvisoItem
import com.injuv.voluntariado.data.api.PostulacionUsuario
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

/**
 * Notificaciones: cambios de estado de tus postulaciones, anuncios de los tutores en los
 * foros y el estado actual de las postulaciones activas.
 */
@Composable
fun NotificationsScreen(
    cambios: List<PostulacionUsuario>,
    postulaciones: List<PostulacionUsuario>,
    avisos: List<AvisoItem>,
    onBack: () -> Unit,
    onOpenAviso: (Int) -> Unit = {},
    /** Terminados donde la ONG ya evaluó y falta la reseña del voluntario. */
    pendientesCalificar: List<PostulacionUsuario> = emptyList(),
    onCalificar: () -> Unit = {},
    /** Estado anterior de cada postulación con novedad (para mostrar "de X a Y"). */
    previos: Map<Int, String> = emptyMap(),
    /** Preferencia "resumen": muestra el estado actual de las postulaciones activas. */
    mostrarEstado: Boolean = true,
    /** Preferencia general apagada: se avisa y no se muestra nada. */
    desactivadas: Boolean = false,
) {
    val T = LocalAppColors.current
    val activas = if (mostrarEstado) postulaciones.filter { it.estado != "Declinada por usuario" && !it.voluntariado_terminado } else emptyList()
    val hayAlgo = !desactivadas && (cambios.isNotEmpty() || avisos.isNotEmpty() || activas.isNotEmpty() || pendientesCalificar.isNotEmpty())

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(T.bgHeaderGradient))
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Column(modifier = Modifier.padding(end = 16.dp)) {
                Text(
                    "Notificaciones",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White,
                )
                Text(
                    "Alertas de postulaciones y anuncios",
                    fontFamily = Outfit,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f),
                )
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            if (desactivadas) {
                Text(
                    "Tienes las notificaciones desactivadas. Actívalas en Configuración → Notificaciones para recibir alertas.",
                    fontFamily = Outfit,
                    fontSize = 13.sp,
                    color = T.textMuted,
                )
            } else if (!hayAlgo) {
                Text(
                    "No tienes notificaciones por ahora. Cuando cambie el estado de una postulación o un tutor publique un anuncio, aparecerá aquí.",
                    fontFamily = Outfit,
                    fontSize = 13.sp,
                    color = T.textMuted,
                )
            }

            if (pendientesCalificar.isNotEmpty()) {
                Seccion("Por calificar")
                pendientesCalificar.forEach { p ->
                    Spacer(Modifier.height(10.dp))
                    NotifCard(onClick = onCalificar) {
                        Text("Tu voluntariado terminó", fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
                        Text("${p.oportunidad_titulo} · ${p.organizacion_nombre}", fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(T.coral)
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                            ) {
                                Text("Calificar a la organización", fontFamily = Outfit, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            if (cambios.isNotEmpty()) {
                if (pendientesCalificar.isNotEmpty()) Spacer(Modifier.height(20.dp))
                Seccion("Novedades")
                cambios.forEach { p ->
                    Spacer(Modifier.height(10.dp))
                    NotifCard {
                        Text(p.oportunidad_titulo, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
                        Text(p.organizacion_nombre, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            EstadoPostulacionChip(p.estado)
                            Spacer(Modifier.width(8.dp))
                            BadgeNuevo()
                        }
                        val anterior = previos[p.id].orEmpty()
                        if (anterior.isNotBlank() && anterior != p.estado) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "De “$anterior” a “${p.estado}”",
                                fontFamily = Outfit,
                                fontSize = 11.sp,
                                color = T.coral,
                            )
                        }
                    }
                }
            }

            if (avisos.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Seccion("Anuncios de tus voluntariados")
                avisos.forEach { a ->
                    Spacer(Modifier.height(10.dp))
                    NotifCard(onClick = { onOpenAviso(a.oportunidadId) }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(a.titulo, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary, modifier = Modifier.weight(1f))
                            if (a.esNuevo) BadgeNuevo()
                        }
                        Text(
                            listOfNotNull(a.organizacion.ifBlank { null }, a.autor.ifBlank { null }).joinToString(" · "),
                            fontFamily = Outfit,
                            fontSize = 12.sp,
                            color = T.textMuted,
                        )
                        if (a.texto.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(a.texto, fontFamily = Outfit, fontSize = 13.sp, color = T.textSecondary, maxLines = 2)
                        }
                        if (a.fecha.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(a.fecha, fontFamily = Outfit, fontSize = 11.sp, color = T.textSubtle)
                        }
                    }
                }
            }

            if (activas.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Seccion("Estado de tus postulaciones")
                activas.forEach { p ->
                    Spacer(Modifier.height(10.dp))
                    NotifCard {
                        Text(p.oportunidad_titulo, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
                        Text(p.organizacion_nombre, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
                        Spacer(Modifier.height(6.dp))
                        EstadoPostulacionChip(p.estado)
                    }
                }
            }
        }
    }
}

@Composable
private fun Seccion(texto: String) {
    val T = LocalAppColors.current
    Text(texto, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textMuted)
}

@Composable
private fun NotifCard(onClick: (() -> Unit)? = null, contenido: @Composable () -> Unit) {
    val T = LocalAppColors.current
    val base = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(Brush.linearGradient(T.bgCardGradient))
    Column(
        modifier = (if (onClick != null) base.clickable(onClick = onClick) else base).padding(16.dp),
    ) {
        contenido()
    }
}

@Composable
private fun BadgeNuevo() {
    val T = LocalAppColors.current
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(T.coral.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text("Nuevo", fontFamily = Outfit, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = T.coral)
    }
}
