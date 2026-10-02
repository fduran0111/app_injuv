package com.injuv.voluntariado.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun ResourcesMenuCard(
    selectedRoute: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val T = LocalAppColors.current
    Column(
        modifier = modifier
            .width(268.dp)
            .shadow(18.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(T.navBg)
            .padding(16.dp),
    ) {
        Text(
            "Recursos",
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = T.textPrimary,
        )
        Text(
            "Elige una sección",
            fontFamily = Outfit,
            fontSize = 12.sp,
            color = T.textMuted,
        )
        Spacer(Modifier.height(14.dp))
        ResourceMenuItem(
            label = "Documentos",
            subtitle = "Reglamentos y formularios",
            icon = Icons.Filled.Description,
            accent = T.yellow,
            selected = selectedRoute == "documents",
            onClick = { onSelect("documents") },
        )
        Spacer(Modifier.height(8.dp))
        ResourceMenuItem(
            label = "Academia",
            subtitle = "Cursos para voluntarios",
            icon = Icons.Filled.School,
            accent = T.green,
            selected = selectedRoute == "academy",
            onClick = { onSelect("academy") },
        )
        Spacer(Modifier.height(8.dp))
        ResourceMenuItem(
            label = "Biblioteca",
            subtitle = "Lecturas recomendadas",
            icon = Icons.Filled.LocalLibrary,
            accent = T.blue,
            selected = selectedRoute == "library",
            onClick = { onSelect("library") },
        )
    }
}

@Composable
private fun ResourceMenuItem(
    label: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val T = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) accent.copy(alpha = 0.14f) else T.surface1)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = T.textPrimary,
            )
            Text(
                subtitle,
                fontFamily = Outfit,
                fontSize = 11.sp,
                color = T.textMuted,
            )
        }
        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = T.textSubtle,
            modifier = Modifier.size(18.dp),
        )
    }
}
