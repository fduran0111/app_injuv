package com.injuv.voluntariado.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun MenuIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Menu, contentDescription = "Recursos", tint = Color.White)
        }
    }
}

@Composable
fun NotificationIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Notifications, contentDescription = "Alertas", tint = Color.White)
        }
    }
}

/** Encabezado unificado: título, subtítulo y botón de notificaciones alineado en todas las pantallas principales. */
@Composable
fun AppScreenHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onNotificationClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null,
    bottomContent: @Composable (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(LocalAppColors.current.bgHeaderGradient))
            .padding(horizontal = 12.dp, vertical = 24.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                if (onMenuClick != null) {
                    MenuIconButton(onClick = onMenuClick)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = if (onMenuClick != null) 4.dp else 8.dp, end = 12.dp, top = 4.dp),
                ) {
                    Text(
                        text = title,
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        fontFamily = Outfit,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f),
                    )
                }
                if (onNotificationClick != null) {
                    NotificationIconButton(onClick = onNotificationClick)
                }
            }
            if (bottomContent != null) {
                Spacer(Modifier.height(16.dp))
                bottomContent()
            }
        }
    }
}

/** Fila de chips horizontales con scroll, usada en Postulaciones, Noticias, Documentos, Academia y Biblioteca. */
@Composable
fun FilterChipsRow(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val T = LocalAppColors.current
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
    ) {
        options.forEachIndexed { index, option ->
            val isActive = option == selected
            Text(
                text = option,
                fontFamily = SpaceGrotesk,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
                fontSize = 12.sp,
                color = if (isActive) Color.White else Color.White.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(start = if (index == 0) 0.dp else 8.dp)
                    .clip(RoundedCornerShape(50))
                    .then(
                        if (isActive) {
                            Modifier.background(
                                Brush.linearGradient(listOf(T.coral, T.coralDark))
                            )
                        } else {
                            Modifier
                                .background(Color.White.copy(alpha = 0.12f))
                                .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(50))
                        }
                    )
                    .clickable { onSelect(option) }
                    .padding(horizontal = 16.dp, vertical = 7.dp),
            )
        }
    }
}

/** Ítems de la barra de navegación inferior (coinciden con BottomNav.tsx). */
enum class BottomNavItem(val route: String, val label: String) {
    Home("home", "Inicio"),
    Applications("applications", "Postular"),
    News("news", "Noticias"),
    Tutor("tutor_home", "Tutoría"),
    Forums("forums", "Foros"),
    Profile("profile", "Perfil"),
}

@Composable
fun bottomNavIcon(item: BottomNavItem, active: Boolean) {
    val icon = when (item) {
        BottomNavItem.Home -> if (active) Icons.Filled.Home else Icons.Outlined.Home
        BottomNavItem.Applications -> if (active) Icons.Filled.Assignment else Icons.Outlined.Assignment
        BottomNavItem.News -> if (active) Icons.Filled.Article else Icons.Outlined.Article
        BottomNavItem.Tutor -> if (active) Icons.Filled.Groups else Icons.Outlined.Groups
        BottomNavItem.Forums -> if (active) Icons.Filled.Forum else Icons.Outlined.Forum
        BottomNavItem.Profile -> if (active) Icons.Filled.Person else Icons.Outlined.Person
    }
    Icon(imageVector = icon, contentDescription = item.label)
}
