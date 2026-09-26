package com.injuv.voluntariado.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

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
    Academy("academy", "Academia"),
    Profile("profile", "Perfil"),
}

@Composable
fun bottomNavIcon(item: BottomNavItem, active: Boolean) {
    val icon = when (item) {
        BottomNavItem.Home -> if (active) Icons.Filled.Home else Icons.Outlined.Home
        BottomNavItem.Applications -> if (active) Icons.Filled.Assignment else Icons.Outlined.Assignment
        BottomNavItem.News -> if (active) Icons.Filled.Article else Icons.Outlined.Article
        BottomNavItem.Academy -> if (active) Icons.Filled.School else Icons.Outlined.School
        BottomNavItem.Profile -> if (active) Icons.Filled.Person else Icons.Outlined.Person
    }
    Icon(imageVector = icon, contentDescription = item.label)
}
