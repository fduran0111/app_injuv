package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.data.Opportunity
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun TutorDashboardScreen(
    program: Opportunity,
    enrolledCount: Int,
    presentToday: Int,
    onAttendance: () -> Unit,
    onOpenForum: () -> Unit,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val T = LocalAppColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(T.bgHeaderGradient))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Panel de tutor",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White,
                )
                Text(
                    "Gestiona tu oportunidad de voluntariado",
                    fontFamily = Outfit,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                )
            }
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Filled.Settings, contentDescription = "Configuración", tint = Color.White)
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(T.bgCardGradient))
                    .clickable(onClick = onAttendance),
            ) {
                AsyncImage(
                    model = program.imageUrl,
                    contentDescription = program.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        program.title,
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = T.textPrimary,
                    )
                    Text(program.org, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        TutorStat("${program.spots}", "Cupos", T.coral)
                        TutorStat("$enrolledCount", "Inscritos", T.blue)
                        TutorStat("${program.hours}h", "Horas", T.yellow)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            TutorMiniStat("Presentes hoy", "$presentToday", T.green, Modifier.fillMaxWidth())

            Spacer(Modifier.height(22.dp))
            Text(
                "Acciones",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = T.textMuted,
            )
            Spacer(Modifier.height(10.dp))

            TutorActionCard(
                icon = Icons.Filled.CheckCircle,
                iconTint = T.green,
                title = "Pasar lista",
                subtitle = "Registrar asistencia de hoy",
                onClick = onAttendance,
            )
            Spacer(Modifier.height(10.dp))
            TutorActionCard(
                icon = Icons.Filled.Groups,
                iconTint = T.coral,
                title = "Foro de anuncios",
                subtitle = "Enviar anuncios a tus voluntarios",
                onClick = onOpenForum,
            )
        }
    }
}

@Composable
private fun TutorStat(value: String, label: String, color: Color) {
    Column {
        Text(value, fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
        Text(label, fontFamily = Outfit, fontSize = 11.sp, color = LocalAppColors.current.textMuted)
    }
}

@Composable
private fun TutorMiniStat(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(LocalAppColors.current.bgCardGradient))
            .padding(14.dp),
    ) {
        Text(value, fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = color)
        Text(label, fontFamily = Outfit, fontSize = 11.sp, color = LocalAppColors.current.textMuted)
    }
}

@Composable
private fun TutorActionCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)?,
) {
    val T = LocalAppColors.current
    val enabled = onClick != null
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .then(if (enabled) Modifier.clickable(onClick = onClick!!) else Modifier)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            Text(title, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = T.textPrimary)
            Text(subtitle, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
        }
        if (enabled) {
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = T.textSubtle)
        }
    }
}
