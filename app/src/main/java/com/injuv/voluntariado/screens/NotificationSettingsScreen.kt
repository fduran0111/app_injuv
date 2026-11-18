package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.injuv.voluntariado.data.NotificationPreferences
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

/**
 * Preferencias de notificaciones: interruptor general y un tipo por cada sección de la
 * pantalla de Notificaciones (cambios de estado, anuncios, calificar y resumen).
 */
@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit,
    onVerNotificaciones: () -> Unit = {},
) {
    val T = LocalAppColors.current
    val context = LocalContext.current
    val prefs = remember { NotificationPreferences(context) }

    var activas by remember { mutableStateOf(prefs.activas) }
    var estados by remember { mutableStateOf(prefs.estados) }
    var anuncios by remember { mutableStateOf(prefs.anuncios) }
    var calificar by remember { mutableStateOf(prefs.calificar) }
    var resumen by remember { mutableStateOf(prefs.resumen) }

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
                    "Qué alertas quieres recibir",
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
            PrefToggle(
                icon = if (activas) Icons.Filled.Notifications else Icons.Filled.NotificationsOff,
                title = "Recibir notificaciones",
                subtitle = if (activas) "Verás las alertas activadas abajo" else "No verás ninguna alerta",
                checked = activas,
                enabled = true,
                onChange = {
                    activas = it
                    prefs.activas = it
                },
            )

            Spacer(Modifier.height(22.dp))
            Text(
                "Tipos de notificación",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = T.textMuted,
            )
            Spacer(Modifier.height(10.dp))
            PrefToggle(
                icon = Icons.Filled.SwapHoriz,
                title = "Cambios de estado",
                subtitle = "Cuando una postulación pasa de revisión, selección o etapa",
                checked = estados,
                enabled = activas,
                onChange = {
                    estados = it
                    prefs.estados = it
                },
            )
            Spacer(Modifier.height(10.dp))
            PrefToggle(
                icon = Icons.Filled.Campaign,
                title = "Anuncios del foro",
                subtitle = "Mensajes que publican los tutores de tus voluntariados",
                checked = anuncios,
                enabled = activas,
                onChange = {
                    anuncios = it
                    prefs.anuncios = it
                },
            )
            Spacer(Modifier.height(10.dp))
            PrefToggle(
                icon = Icons.Filled.RateReview,
                title = "Recordatorios de calificación",
                subtitle = "Cuando un voluntariado termina y puedes evaluar a la organización",
                checked = calificar,
                enabled = activas,
                onChange = {
                    calificar = it
                    prefs.calificar = it
                },
            )
            Spacer(Modifier.height(10.dp))
            PrefToggle(
                icon = Icons.Filled.ListAlt,
                title = "Resumen de postulaciones",
                subtitle = "El estado actual de tus postulaciones activas",
                checked = resumen,
                enabled = activas,
                onChange = {
                    resumen = it
                    prefs.resumen = it
                },
            )

            Spacer(Modifier.height(22.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(T.bgCardGradient))
                    .clickable(onClick = onVerNotificaciones)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Notifications, contentDescription = null, tint = T.coral, modifier = Modifier.size(22.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                ) {
                    Text("Ver notificaciones", fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = T.textPrimary)
                    Text("Revisa las alertas recibidas", fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
                }
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = T.textSubtle)
            }
        }
    }
}

@Composable
private fun PrefToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean,
    onChange: (Boolean) -> Unit,
) {
    val T = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = if (enabled) T.coral else T.textSubtle, modifier = Modifier.size(22.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            Text(title, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = if (enabled) T.textPrimary else T.textMuted)
            Text(subtitle, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
        }
        Switch(
            checked = checked && enabled,
            onCheckedChange = onChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = T.coral,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = T.textSubtle,
            ),
        )
    }
}
