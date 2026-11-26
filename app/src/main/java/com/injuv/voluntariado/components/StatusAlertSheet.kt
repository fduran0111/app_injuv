package com.injuv.voluntariado.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.injuv.voluntariado.data.api.Notificacion
import com.injuv.voluntariado.data.api.PostulacionUsuario
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.InstrumentSans
import com.injuv.voluntariado.ui.theme.Bricolage

/**
 * Aviso emergente: notificaciones nuevas del servidor (las mismas de la campana web); si no hay,
 * el estado actual de las postulaciones activas. (Las notificaciones push quedan para una siguiente etapa.)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusAlertSheet(
    nuevas: List<Notificacion>,
    postulaciones: List<PostulacionUsuario>,
    onClose: () -> Unit,
    /** Tocar una notificación (navega a su destino). */
    onOpen: (Notificacion) -> Unit = {},
    /** Preferencia "resumen": sin novedades, muestra el estado de las activas. */
    mostrarResumen: Boolean = true,
    /** Terminados pendientes de tu reseña a la organización. */
    porCalificar: List<PostulacionUsuario> = emptyList(),
    /** Ir al perfil para calificar (se invoca al tocar la tarjeta de "Por calificar"). */
    onCalificar: (() -> Unit)? = null,
) {
    val T = LocalAppColors.current
    val sheetState = rememberModalBottomSheetState()
    val activas = if (mostrarResumen) postulaciones.filter { it.estado != "Declinada por usuario" && !it.voluntariado_terminado }.take(5) else emptyList()
    val hayNovedad = nuevas.isNotEmpty() || porCalificar.isNotEmpty()
    val lista = if (hayNovedad) emptyList() else activas

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = T.bgCardGradient.first(),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (hayNovedad) "Novedades" else "Tus postulaciones",
                    fontFamily = Bricolage,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = T.textPrimary,
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = T.textMuted)
                }
            }

            if (!hayNovedad) {
                Text(
                    if (lista.isNotEmpty()) "Así van tus postulaciones activas:" else "No hay novedades desde tu última revisión.",
                    fontFamily = InstrumentSans,
                    fontSize = 13.sp,
                    color = T.textMuted,
                )
            }

            if (porCalificar.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                Text(
                    "Por calificar",
                    fontFamily = InstrumentSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = T.textMuted,
                )
                porCalificar.forEach { p ->
                    Spacer(Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = if (onCalificar != null) Modifier.clickable(onClick = onCalificar) else Modifier,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(T.yellow.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.NotificationsActive, contentDescription = null, tint = T.yellow)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tu voluntariado terminó", fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
                            Text("${p.oportunidad_titulo} · ${p.organizacion_nombre}", fontFamily = InstrumentSans, fontSize = 12.sp, color = T.textMuted)
                            Spacer(Modifier.height(4.dp))
                            Text("Calificar a la organización ›", fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = T.coral)
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }

            if (nuevas.isNotEmpty()) {
                nuevas.forEach { n ->
                    Spacer(Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().clickable { onOpen(n) },
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(T.coral.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.NotificationsActive, contentDescription = null, tint = T.coral)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(n.titulo, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
                            if (!n.texto.isNullOrBlank()) {
                                Spacer(Modifier.height(2.dp))
                                Text(n.texto, fontFamily = InstrumentSans, fontSize = 13.sp, color = T.textSecondary, maxLines = 2)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }

            if (!hayNovedad && lista.isEmpty() && mostrarResumen) {
                Spacer(Modifier.height(12.dp))
                Text("Aún no tienes postulaciones activas.", fontFamily = InstrumentSans, fontSize = 13.sp, color = T.textSecondary)
            }

            lista.forEach { p ->
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val color = colorEstado(p.estado, T)
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.NotificationsActive, contentDescription = null, tint = color)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(p.oportunidad_titulo, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
                        Text(p.organizacion_nombre, fontFamily = InstrumentSans, fontSize = 12.sp, color = T.textMuted)
                        Spacer(Modifier.height(4.dp))
                        EstadoPostulacionChip(p.estado)
                    }
                }
            }
        }
    }
}
