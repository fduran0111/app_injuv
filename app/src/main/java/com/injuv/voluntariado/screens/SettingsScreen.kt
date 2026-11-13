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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.injuv.voluntariado.components.FilterChipsRow
import com.injuv.voluntariado.data.SampleData
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    isTutor: Boolean = false,
    onTutorChange: (Boolean) -> Unit = {},
    tutorProgramId: Int = 0,
    onTutorProgramChange: (Int) -> Unit = {},
    isOrg: Boolean = false,
    onOrgChange: (Boolean) -> Unit = {},
) {
    val T = LocalAppColors.current

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
                    "Configuración",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White,
                )
                Text(
                    "Cuenta, apariencia y sesión",
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
            Text(
                "Rol",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = T.textMuted,
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(T.bgCardGradient))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Groups, contentDescription = null, tint = T.coral)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                ) {
                    Text(
                        "Tutor de voluntariado",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = T.textPrimary,
                    )
                    Text(
                        if (isTutor) "Panel de tutor activado" else "Gestiona tu oportunidad",
                        fontFamily = Outfit,
                        fontSize = 12.sp,
                        color = T.textMuted,
                    )
                }
                Switch(
                    checked = isTutor,
                    onCheckedChange = onTutorChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = T.coral,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = T.textSubtle,
                    ),
                )
            }
            if (isTutor) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "Tu oportunidad",
                    fontFamily = Outfit,
                    fontSize = 12.sp,
                    color = T.textMuted,
                )
                Spacer(Modifier.height(8.dp))
                val selectedProgram = SampleData.opportunities.find { it.id == tutorProgramId }
                FilterChipsRow(
                    options = SampleData.opportunities.map { it.title },
                    selected = selectedProgram?.title ?: SampleData.opportunities.first().title,
                    onSelect = { title ->
                        SampleData.opportunities.find { it.title == title }?.let { onTutorProgramChange(it.id) }
                    },
                )
            }

            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(T.bgCardGradient))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Business, contentDescription = null, tint = T.coral)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                ) {
                    Text(
                        "Organización",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = T.textPrimary,
                    )
                    Text(
                        if (isOrg) "Perfil de ONG activado" else "Administra el perfil de tu ONG",
                        fontFamily = Outfit,
                        fontSize = 12.sp,
                        color = T.textMuted,
                    )
                }
                Switch(
                    checked = isOrg,
                    onCheckedChange = onOrgChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = T.coral,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = T.textSubtle,
                    ),
                )
            }

            Spacer(Modifier.height(22.dp))
            Text(
                "Cuenta",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = T.textMuted,
            )
            Spacer(Modifier.height(10.dp))
            SettingsRow(
                icon = Icons.Filled.Edit,
                title = "Editar perfil",
                subtitle = "Foto, contacto, habilidades y privacidad",
                onClick = { onNavigate("edit_profile") },
            )
            Spacer(Modifier.height(10.dp))
            SettingsRow(
                icon = Icons.Filled.WorkspacePremium,
                title = "Mis certificados",
                subtitle = "4 certificados disponibles",
            )
            Spacer(Modifier.height(10.dp))
            SettingsRow(
                icon = Icons.Filled.Notifications,
                title = "Notificaciones",
                subtitle = "Alertas de postulaciones",
            )
            Spacer(Modifier.height(10.dp))
            SettingsRow(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                title = "Ayuda",
                subtitle = "Preguntas frecuentes",
            )

            Spacer(Modifier.height(22.dp))
            Text(
                "Apariencia",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = T.textMuted,
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(T.bgCardGradient))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    if (isDarkTheme) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                    contentDescription = null,
                    tint = T.coral,
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                ) {
                    Text(
                        "Tema oscuro",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = T.textPrimary,
                    )
                    Text(
                        if (isDarkTheme) "Activado" else "Modo claro activado",
                        fontFamily = Outfit,
                        fontSize = 12.sp,
                        color = T.textMuted,
                    )
                }
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = onDarkThemeChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = T.coral,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = T.textSubtle,
                    ),
                )
            }

            Spacer(Modifier.height(22.dp))
            Text(
                "Acerca de",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = T.textMuted,
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(T.bgCardGradient))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = T.coral, modifier = Modifier.size(22.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                ) {
                    Text(
                        "INJUV Voluntariado",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = T.textPrimary,
                    )
                    Text(
                        "Versión 1.0",
                        fontFamily = Outfit,
                        fontSize = 12.sp,
                        color = T.textMuted,
                    )
                }
            }

            Spacer(Modifier.height(22.dp))
            SettingsRow(
                icon = Icons.AutoMirrored.Filled.Logout,
                title = "Cerrar sesión",
                subtitle = "Salir de la aplicación",
                onClick = onLogout,
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {},
) {
    val T = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = T.coral, modifier = Modifier.size(22.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            Text(title, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = T.textPrimary)
            Text(subtitle, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = T.textSubtle)
    }
}
