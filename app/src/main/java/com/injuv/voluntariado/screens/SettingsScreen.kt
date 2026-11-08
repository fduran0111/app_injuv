package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
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
import com.injuv.voluntariado.components.ResourceNavItem
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.LocalOnThemeChange
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val T = LocalAppColors.current
    val onThemeChange = LocalOnThemeChange.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(T.bgHeaderGradient))
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Text(
                "Configuración",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White,
            )
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Cuenta",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = T.textMuted,
            )

            ResourceNavItem(
                title = "Editar perfil",
                subtitle = "Foto, contacto, habilidades y privacidad",
                icon = Icons.Filled.Edit,
                accent = T.coral,
                onClick = { onNavigate("edit_profile") },
            )
            ResourceNavItem(
                title = "Mis certificados",
                subtitle = "4 certificados disponibles",
                icon = Icons.Filled.WorkspacePremium,
                accent = T.yellow,
                onClick = {},
            )
            ResourceNavItem(
                title = "Notificaciones",
                subtitle = "Alertas de postulaciones",
                icon = Icons.Filled.Notifications,
                accent = T.blue,
                onClick = {},
            )
            ResourceNavItem(
                title = "Ayuda",
                subtitle = "Preguntas frecuentes",
                icon = Icons.Filled.HelpOutline,
                accent = T.green,
                onClick = {},
            )

            Spacer(Modifier.height(8.dp))
            Text(
                "Apariencia",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = T.textMuted,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(T.bgCardGradient))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SettingIcon(if (T.isDark) Icons.Filled.DarkMode else Icons.Filled.LightMode)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        if (T.isDark) "Tema oscuro" else "Tema claro",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = T.textPrimary,
                    )
                    Text(
                        "Cambia entre modo oscuro y claro",
                        fontFamily = Outfit,
                        fontSize = 12.sp,
                        color = T.textMuted,
                    )
                }
                Switch(
                    checked = T.isDark,
                    onCheckedChange = onThemeChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = T.coral,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = T.textSubtle,
                    ),
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Acerca de",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = T.textMuted,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(T.bgCardGradient))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SettingIcon(Icons.Filled.Info)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "INJUV Voluntariado",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
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

            Spacer(Modifier.height(8.dp))
            ResourceNavItem(
                title = "Cerrar sesión",
                subtitle = "Salir de esta cuenta",
                icon = Icons.Filled.Logout,
                accent = T.coral,
                onClick = onLogout,
            )
        }
    }
}

@Composable
private fun SettingIcon(icon: ImageVector) {
    val T = LocalAppColors.current
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(T.coral.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = T.coral)
    }
}
