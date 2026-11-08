package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.components.ResourceNavItem
import com.injuv.voluntariado.data.Opportunity
import com.injuv.voluntariado.data.SampleData
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    onOpenDetail: (Int) -> Unit,
    onShowAlert: () -> Unit,
    onOpenMenu: () -> Unit,
    userName: String = "Voluntario",
) {
    val T = LocalAppColors.current
    val featured = SampleData.opportunities.take(2)
    var resourcesOpen by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        // Encabezado degradado
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(T.bgHeaderGradient))
                    .padding(horizontal = 20.dp, vertical = 28.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    IconButton(onClick = onOpenMenu) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menú", tint = Color.White)
                        }
                    }
                    Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                        Text(
                            text = "Hola, $userName 👋",
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Encuentra tu próxima experiencia de voluntariado",
                            fontFamily = Outfit,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f),
                        )
                    }
                    IconButton(onClick = onShowAlert) {
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

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatPill(value = "10,000+", label = "Voluntarios", modifier = Modifier.weight(1f))
                    StatPill(value = "140+", label = "Comunas", modifier = Modifier.weight(1f))
                }
            }
        }

        // Destacados
        item {
            Column(modifier = Modifier.padding(top = 24.dp, start = 20.dp, end = 20.dp)) {
                Text(
                    text = "Oportunidades destacadas",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = T.textPrimary,
                )
            }
            Spacer(Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items(featured) { opportunity ->
                    FeaturedOpportunityCard(
                        opportunity = opportunity,
                        onClick = { onOpenDetail(opportunity.id) },
                    )
                }
            }
        }

        // Accesos rápidos
        item {
            Column(modifier = Modifier.padding(top = 28.dp, start = 20.dp, end = 20.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(T.bgCardGradient))
                        .clickable { resourcesOpen = !resourcesOpen }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Recursos",
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = T.textPrimary,
                        )
                        Text(
                            "Documentos, academia y biblioteca",
                            fontFamily = Outfit,
                            fontSize = 12.sp,
                            color = T.textMuted,
                        )
                    }
                    Icon(
                        if (resourcesOpen) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (resourcesOpen) "Cerrar" else "Abrir",
                        tint = T.coral,
                    )
                }

                if (resourcesOpen) {
                    Spacer(Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ResourceNavItem(
                            title = "Documentos",
                            subtitle = "Reglamentos y formularios",
                            icon = Icons.Filled.Description,
                            accent = T.yellow,
                            onClick = { onNavigate("documents") },
                        )
                        ResourceNavItem(
                            title = "Academia",
                            subtitle = "Cursos de voluntariado",
                            icon = Icons.Filled.School,
                            accent = T.green,
                            onClick = { onNavigate("academy") },
                        )
                        ResourceNavItem(
                            title = "Biblioteca",
                            subtitle = "Lecturas recomendadas",
                            icon = Icons.Filled.MenuBook,
                            accent = T.coral,
                            onClick = { onNavigate("library") },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatPill(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(vertical = 14.dp, horizontal = 12.dp),
    ) {
        Text(value, fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFFFFD60A))
        Text(label, fontFamily = Outfit, fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
    }
}

@Composable
private fun FeaturedOpportunityCard(opportunity: Opportunity, onClick: () -> Unit) {
    val T = LocalAppColors.current
    Column(
        modifier = Modifier
            .width(230.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = opportunity.imageUrl,
            contentDescription = opportunity.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
        )
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                opportunity.title,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = T.textPrimary,
                maxLines = 1,
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = T.textMuted, modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(3.dp))
                Text(opportunity.location, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "${opportunity.hours}h · ${opportunity.spots} cupos",
                fontFamily = Outfit,
                fontSize = 11.sp,
                color = T.coral,
            )
        }
    }
}
