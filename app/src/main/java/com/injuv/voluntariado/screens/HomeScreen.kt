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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.injuv.voluntariado.components.AppScreenHeader
import com.injuv.voluntariado.components.EstadoCarga
import com.injuv.voluntariado.components.OportunidadImagen
import com.injuv.voluntariado.components.Vacio
import com.injuv.voluntariado.components.rememberCarga
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.data.api.Oportunidad
import com.injuv.voluntariado.data.api.SessionManager
import com.injuv.voluntariado.data.api.formatearFecha
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun HomeScreen(
    onOpenDetail: (Int) -> Unit,
    onShowAlert: () -> Unit,
    onToggleMenu: () -> Unit,
) {
    val T = LocalAppColors.current
    val usuario by SessionManager.user.collectAsState()
    val stats = rememberCarga { ApiClient.call { estadisticas() } }
    val oportunidades = rememberCarga { ApiClient.call { oportunidades(limit = 10) } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            AppScreenHeader(
                title = "Hola, ${usuario?.nombre?.ifBlank { null } ?: "voluntario/a"} 👋",
                subtitle = "Encuentra tu próxima experiencia de voluntariado",
                onNotificationClick = onShowAlert,
                onMenuClick = onToggleMenu,
            ) {
                val e = stats.datos
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatPill(value = e?.voluntarios?.let { miles(it) } ?: "—", label = "Voluntarios", modifier = Modifier.weight(1f))
                    StatPill(value = e?.oportunidades?.let { miles(it) } ?: "—", label = "Oportunidades", modifier = Modifier.weight(1f))
                    StatPill(value = e?.horas?.let { miles(it) } ?: "—", label = "Horas", modifier = Modifier.weight(1f))
                }
            }
        }

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
            EstadoCarga(oportunidades) { lista ->
                if (lista.isEmpty()) {
                    Vacio("No hay oportunidades abiertas", "Vuelve pronto: las organizaciones publican nuevas convocatorias seguido.")
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        items(lista, key = { it.id }) { opportunity ->
                            FeaturedOpportunityCard(
                                opportunity = opportunity,
                                onClick = { onOpenDetail(opportunity.id) },
                            )
                        }
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
private fun miles(n: Int): String = "%,d".format(n).replace(',', '.')

@Composable
private fun FeaturedOpportunityCard(opportunity: Oportunidad, onClick: () -> Unit) {
    val T = LocalAppColors.current
    Column(
        modifier = Modifier
            .width(230.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .clickable(onClick = onClick),
    ) {
        OportunidadImagen(
            oportunidad = opportunity,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
        )
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                opportunity.titulo,
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
                Text(opportunity.ubicacion, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted, maxLines = 1)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                listOfNotNull(
                    opportunity.horas_voluntariado?.let { "${it}h" },
                    opportunity.cupo_maximo?.let { "$it cupos" },
                    opportunity.fecha_limite_postulacion?.let { "cierra ${formatearFecha(it)}" },
                ).joinToString(" · ").ifBlank { opportunity.organizacion_nombre },
                fontFamily = Outfit,
                fontSize = 11.sp,
                color = T.coral,
            )
        }
    }
}
