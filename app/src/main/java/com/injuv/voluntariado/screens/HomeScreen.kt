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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
) {
    val T = LocalAppColors.current
    val featured = SampleData.opportunities.take(2)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        // Encabezado degradado
        item {
            Box(
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
                    Column {
                        Text(
                            text = "Hola, Francisco 👋",
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

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                Text(
                    text = "Accesos rápidos",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = T.textPrimary,
                )
                Spacer(Modifier.height(12.dp))

                val quickLinks = listOf(
                    Triple("Noticias", "news", T.blue),
                    Triple("Documentos", "documents", T.yellow),
                    Triple("Academia", "academy", T.green),
                    Triple("Biblioteca", "library", T.coral),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    quickLinks.forEach { (label, route, color) ->
                        QuickLinkCard(
                            label = label,
                            accent = color,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigate(route) },
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Acceso a vista de administrador (rol institucional)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(T.bgCardGradient))
                        .clickable { onNavigate("admin") }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Groups, contentDescription = null, tint = T.blue)
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Panel de administrador",
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = T.textPrimary,
                        )
                        Text(
                            "Gestiona postulantes y cupos",
                            fontFamily = Outfit,
                            fontSize = 12.sp,
                            color = T.textMuted,
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

@Composable
private fun QuickLinkCard(label: String, accent: Color, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(T.surface1)
            .padding(vertical = 16.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(accent),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            label,
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = T.textPrimary,
        )
    }
}
