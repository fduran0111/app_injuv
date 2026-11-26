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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Business
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
import com.injuv.voluntariado.ui.theme.InstrumentSans
import com.injuv.voluntariado.ui.theme.Bricolage

@Composable
fun MenuIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) =
    BotonCirculo(Icons.Filled.Menu, "Recursos", onClick, modifier)

@Composable
fun NotificationIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) =
    BotonCirculo(Icons.Outlined.Notifications, "Alertas", onClick, modifier)

/**
 * Encabezado de las pantallas principales: menú, logotipo INJUV y alertas arriba; debajo el
 * título grande con su palabra clave marcada en ámbar (como los titulares de la web).
 */
@Composable
fun AppScreenHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onNotificationClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null,
    resaltado: String? = palabraClave(title),
    bottomContent: @Composable (() -> Unit)? = null,
) {
    val T = LocalAppColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(T.bgBase)
            .padding(top = 6.dp, bottom = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onMenuClick != null) MenuIconButton(onClick = onMenuClick) else Spacer(Modifier.size(48.dp))
            LogoInjuv(Modifier.height(26.dp))
            if (onNotificationClick != null) NotificationIconButton(onClick = onNotificationClick) else Spacer(Modifier.size(48.dp))
        }
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp)) {
            TituloMarcador(title, resaltado = resaltado, fontSize = 31.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                text = subtitle,
                fontFamily = InstrumentSans,
                fontSize = 14.sp,
                color = T.textMuted,
            )
        }
        if (bottomContent != null) {
            Spacer(Modifier.height(10.dp))
            Box(Modifier.padding(horizontal = 20.dp)) { bottomContent() }
        }
    }
}

/** Fila de fichas de filtro con scroll, usada en Postulaciones, Noticias, Documentos, Academia y Biblioteca. */
@Composable
fun FilterChipsRow(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    counts: Map<String, Int> = emptyMap(),
) = FilaFichas(options, selected, onSelect, modifier, counts)

/** Ítems de la barra de navegación inferior (coinciden con BottomNav.tsx). */
enum class BottomNavItem(val route: String, val label: String) {
    Home("home", "Inicio"),
    Applications("applications", "Postular"),
    News("news", "Noticias"),
    Tutor("tutor_list", "Tutoría"),
    Forums("forums", "Foros"),
    Org("org", "ONG"),
    Profile("profile", "Perfil"),
}

@Composable
fun bottomNavIcon(item: BottomNavItem, active: Boolean, tint: Color = Color.Unspecified) {
    val icon = when (item) {
        BottomNavItem.Home -> if (active) Icons.Filled.Home else Icons.Outlined.Home
        BottomNavItem.Applications -> if (active) Icons.Filled.Assignment else Icons.Outlined.Assignment
        BottomNavItem.News -> if (active) Icons.Filled.Article else Icons.Outlined.Article
        BottomNavItem.Tutor -> if (active) Icons.Filled.Groups else Icons.Outlined.Groups
        BottomNavItem.Forums -> if (active) Icons.Filled.Forum else Icons.Outlined.Forum
        BottomNavItem.Org -> if (active) Icons.Filled.Business else Icons.Outlined.Business
        BottomNavItem.Profile -> if (active) Icons.Filled.Person else Icons.Outlined.Person
    }
    Icon(imageVector = icon, contentDescription = item.label, tint = tint)
}
