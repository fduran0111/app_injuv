package com.injuv.voluntariado.components

import androidx.compose.foundation.background
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
import com.injuv.voluntariado.data.api.PostulacionUsuario
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

/**
 * Avisos de postulación. Muestra las postulaciones cuyo estado cambió desde la última vez que
 * se abrió este aviso; si no hay cambios, el estado actual de las postulaciones activas.
 * (Las notificaciones push quedan para una siguiente etapa.)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusAlertSheet(
    cambios: List<PostulacionUsuario>,
    postulaciones: List<PostulacionUsuario>,
    onClose: () -> Unit,
) {
    val T = LocalAppColors.current
    val sheetState = rememberModalBottomSheetState()
    val activas = postulaciones.filter { it.estado != "Declinada por usuario" && !it.voluntariado_terminado }.take(5)
    val lista = cambios.ifEmpty { activas }

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
                    text = if (cambios.isNotEmpty()) "Novedades en tus postulaciones" else "Tus postulaciones",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = T.textPrimary,
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = T.textMuted)
                }
            }

            if (cambios.isEmpty()) {
                Text(
                    "No hay cambios de estado desde tu última revisión.",
                    fontFamily = Outfit,
                    fontSize = 13.sp,
                    color = T.textMuted,
                )
            }

            if (lista.isEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text("Aún no tienes postulaciones activas.", fontFamily = Outfit, fontSize = 13.sp, color = T.textSecondary)
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
                        Text(p.oportunidad_titulo, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
                        Text(p.organizacion_nombre, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
                        Spacer(Modifier.height(4.dp))
                        EstadoPostulacionChip(p.estado)
                    }
                }
            }
        }
    }
}
