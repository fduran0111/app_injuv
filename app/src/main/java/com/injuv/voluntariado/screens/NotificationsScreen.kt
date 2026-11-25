package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.injuv.voluntariado.data.api.Notificacion
import com.injuv.voluntariado.data.api.PostulacionUsuario
import com.injuv.voluntariado.data.api.formatearFecha
import com.injuv.voluntariado.components.SubHeader
import com.injuv.voluntariado.components.TituloSeccion
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.InstrumentSans
import com.injuv.voluntariado.ui.theme.Bricolage

/**
 * Notificaciones: las del servidor (las mismas de la campana web: postulaciones, cambios de
 * estado, anuncios del foro, tutorías asignadas) más el estado actual de las postulaciones activas.
 */
@Composable
fun NotificationsScreen(
    recientes: List<Notificacion>,
    postulaciones: List<PostulacionUsuario>,
    onBack: () -> Unit,
    onOpen: (Notificacion) -> Unit = {},
    /** Terminados donde la ONG ya evaluó y falta la reseña del voluntario. */
    pendientesCalificar: List<PostulacionUsuario> = emptyList(),
    onCalificar: () -> Unit = {},
    /** Preferencia "resumen": muestra el estado actual de las postulaciones activas. */
    mostrarEstado: Boolean = true,
    /** Preferencia general apagada: se avisa y no se muestra nada. */
    desactivadas: Boolean = false,
) {
    val T = LocalAppColors.current
    val activas = if (mostrarEstado) postulaciones.filter { it.estado != "Declinada por usuario" && !it.voluntariado_terminado } else emptyList()
    val hayAlgo = !desactivadas && (recientes.isNotEmpty() || activas.isNotEmpty() || pendientesCalificar.isNotEmpty())

    Column(modifier = Modifier.fillMaxSize()) {
        SubHeader(
            titulo = "Notificaciones",
            onBack = onBack,
            subtitulo = "Alertas de postulaciones y anuncios",
        )

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            if (desactivadas) {
                Text(
                    "Tienes las notificaciones desactivadas. Actívalas en Configuración → Notificaciones para recibir alertas.",
                    fontFamily = InstrumentSans,
                    fontSize = 13.sp,
                    color = T.textMuted,
                )
            } else if (!hayAlgo) {
                Text(
                    "No tienes notificaciones por ahora. Cuando cambie el estado de una postulación o un tutor publique un anuncio, aparecerá aquí.",
                    fontFamily = InstrumentSans,
                    fontSize = 13.sp,
                    color = T.textMuted,
                )
            }

            if (pendientesCalificar.isNotEmpty()) {
                Seccion("Por calificar")
                pendientesCalificar.forEach { p ->
                    Spacer(Modifier.height(10.dp))
                    NotifCard(onClick = onCalificar) {
                        Text("Tu voluntariado terminó", fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
                        Text("${p.oportunidad_titulo} · ${p.organizacion_nombre}", fontFamily = InstrumentSans, fontSize = 12.sp, color = T.textMuted)
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(T.action)
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                            ) {
                                Text("Calificar a la organización", fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            if (recientes.isNotEmpty() && !desactivadas) {
                if (pendientesCalificar.isNotEmpty()) Spacer(Modifier.height(20.dp))
                Seccion("Recientes")
                recientes.forEach { n ->
                    Spacer(Modifier.height(10.dp))
                    NotifCard(onClick = { onOpen(n) }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                n.titulo,
                                fontFamily = InstrumentSans,
                                fontWeight = if (n.leida) FontWeight.Medium else FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = if (n.leida) T.textSecondary else T.textPrimary,
                                modifier = Modifier.weight(1f),
                            )
                            if (!n.leida) BadgeNuevo()
                        }
                        if (!n.texto.isNullOrBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(n.texto, fontFamily = InstrumentSans, fontSize = 13.sp, color = T.textSecondary, maxLines = 3)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(formatearFecha(n.created_at, ""), fontFamily = InstrumentSans, fontSize = 11.sp, color = T.textSubtle)
                    }
                }
            }

            if (activas.isNotEmpty() && !desactivadas) {
                Spacer(Modifier.height(20.dp))
                Seccion("Estado de tus postulaciones")
                activas.forEach { p ->
                    Spacer(Modifier.height(10.dp))
                    NotifCard {
                        Text(p.oportunidad_titulo, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
                        Text(p.organizacion_nombre, fontFamily = InstrumentSans, fontSize = 12.sp, color = T.textMuted)
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
    TituloSeccion(texto)
}

@Composable
private fun NotifCard(onClick: (() -> Unit)? = null, contenido: @Composable () -> Unit) {
    val T = LocalAppColors.current
    val base = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(T.card)
        .border(1.dp, T.border1, RoundedCornerShape(16.dp))
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
        Text("Nuevo", fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = T.coral)
    }
}
