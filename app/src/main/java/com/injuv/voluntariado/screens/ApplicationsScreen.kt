package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.injuv.voluntariado.components.AppScreenHeader
import com.injuv.voluntariado.components.FilterChipsRow
import com.injuv.voluntariado.components.EstadoCarga
import com.injuv.voluntariado.components.OportunidadImagen
import com.injuv.voluntariado.components.Vacio
import com.injuv.voluntariado.components.rememberCarga
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.data.api.Oportunidad
import com.injuv.voluntariado.data.api.formatearFecha
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun ApplicationsScreen(
    onOpenDetail: (Int) -> Unit,
    onShowAlert: () -> Unit,
    onToggleMenu: () -> Unit,
) {
    val T = LocalAppColors.current
    var selectedCategory by remember { mutableStateOf("Todos") }
    val carga = rememberCarga { ApiClient.call { oportunidades(limit = 200) } }
    val todas = carga.datos.orEmpty()
    // Las categorías salen del "área de voluntariado" de las oportunidades publicadas.
    val categorias = listOf("Todos") + todas.map { it.tipo_de_voluntariado }.filter { it.isNotBlank() }.distinct().sorted()

    val filtered = if (selectedCategory == "Todos") todas else todas.filter { it.tipo_de_voluntariado == selectedCategory }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            AppScreenHeader(
                title = "Postulaciones",
                subtitle = if (carga.datos == null) "Cargando oportunidades…" else "${filtered.size} oportunidades disponibles",
                onNotificationClick = onShowAlert,
                onMenuClick = onToggleMenu,
                bottomContent = {
                    FilterChipsRow(
                        options = categorias,
                        selected = selectedCategory,
                        onSelect = { selectedCategory = it },
                    )
                },
            )
        }

        if (carga.datos == null) {
            item { EstadoCarga(carga) {} }
        } else if (filtered.isEmpty()) {
            item { Vacio("No hay oportunidades en esta categoría", "Prueba con otra área o vuelve más tarde.") }
        }
        items(filtered, key = { it.id }) { opportunity ->
            OpportunityListCard(
                opportunity = opportunity,
                onClick = { onOpenDetail(opportunity.id) },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun OpportunityListCard(opportunity: Oportunidad, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .clickable(onClick = onClick)
            .padding(12.dp),
    ) {
        OportunidadImagen(
            oportunidad = opportunity,
            modifier = Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(12.dp)),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                opportunity.titulo,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = T.textPrimary,
                maxLines = 1,
            )
            Text(opportunity.organizacion_nombre, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted, maxLines = 1)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = T.textMuted, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(3.dp))
                Text(opportunity.ubicacion, fontFamily = Outfit, fontSize = 11.sp, color = T.textMuted, maxLines = 1, modifier = Modifier.weight(1f, fill = false))
                Spacer(Modifier.width(10.dp))
                Icon(Icons.Filled.People, contentDescription = null, tint = T.textMuted, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(3.dp))
                Text(opportunity.cupo_maximo?.let { "$it cupos" } ?: "Cupo abierto", fontFamily = Outfit, fontSize = 11.sp, color = T.textMuted)
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = T.coral, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(3.dp))
                Text("Cierra: ${formatearFecha(opportunity.fecha_limite_postulacion, "sin fecha")}", fontFamily = Outfit, fontSize = 11.sp, color = T.coral, fontWeight = FontWeight.Medium)
            }
        }
    }
}
