package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.injuv.voluntariado.components.AppScreenHeader
import com.injuv.voluntariado.components.EstadoCarga
import com.injuv.voluntariado.components.FilaFichas
import com.injuv.voluntariado.components.TarjetaOportunidad
import com.injuv.voluntariado.components.Vacio
import com.injuv.voluntariado.components.rememberCarga
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.ui.theme.InstrumentSans
import com.injuv.voluntariado.ui.theme.LocalAppColors

@Composable
fun ApplicationsScreen(
    onOpenDetail: (Int) -> Unit,
    onShowAlert: () -> Unit,
    onToggleMenu: () -> Unit,
    /** Área elegida desde Inicio ("Explora por área"); se aplica una vez al entrar. */
    areaInicial: String? = null,
    onAreaAplicada: () -> Unit = {},
) {
    val T = LocalAppColors.current
    var selectedCategory by rememberSaveable { mutableStateOf("Todas") }
    var busqueda by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(areaInicial) {
        if (areaInicial != null) {
            selectedCategory = areaInicial
            onAreaAplicada()
        }
    }
    val carga = rememberCarga { ApiClient.call { oportunidades(limit = 200) } }
    val todas = carga.datos.orEmpty()
    // Las categorías salen del "área de voluntariado" de las oportunidades publicadas.
    val areas = todas.map { it.tipo_de_voluntariado }.filter { it.isNotBlank() }
    val categorias = listOf("Todas") + areas.distinct().sorted()
    val contadores = mapOf("Todas" to todas.size) + areas.groupingBy { it }.eachCount()

    val q = busqueda.trim().lowercase()
    val filtered = todas
        .filter { selectedCategory == "Todas" || it.tipo_de_voluntariado == selectedCategory }
        .filter { q.isEmpty() || listOf(it.titulo, it.organizacion_nombre, it.comuna, it.region).any { c -> c.lowercase().contains(q) } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            AppScreenHeader(
                title = "Encuentra tu voluntariado",
                subtitle = if (carga.datos == null) "Cargando oportunidades…" else "${filtered.size} de ${todas.size} convocatorias abiertas",
                onNotificationClick = onShowAlert,
                onMenuClick = onToggleMenu,
                bottomContent = { CampoBusqueda(busqueda, onChange = { busqueda = it }) },
            )
        }
        item {
            FilaFichas(
                categorias,
                selectedCategory,
                onSelect = { selectedCategory = it },
                contadores = contadores,
                contentPadding = PaddingValues(horizontal = 20.dp),
                modifier = Modifier.padding(bottom = 6.dp),
            )
        }

        if (carga.datos == null) {
            item { EstadoCarga(carga) {} }
        } else if (filtered.isEmpty()) {
            item {
                Vacio(
                    if (q.isNotEmpty()) "Sin resultados para \"${busqueda.trim()}\"" else "No hay oportunidades en esta área",
                    "Prueba con otra palabra o área, o vuelve más tarde.",
                )
            }
        }
        items(filtered, key = { it.id }) { op ->
            TarjetaOportunidad(
                op,
                onClick = { onOpenDetail(op.id) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

/** Campo de búsqueda de la web (`.campo-vivo`): anillo fino que se vuelve azul al enfocarse. */
@Composable
private fun CampoBusqueda(valor: String, onChange: (String) -> Unit) {
    val T = LocalAppColors.current
    var foco by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(T.card)
            .border(if (foco) 2.dp else 1.dp, if (foco) T.action else T.border2, RoundedCornerShape(12.dp))
            .padding(start = 14.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Outlined.Search, contentDescription = null, tint = T.textMuted, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f)) {
            if (valor.isEmpty()) {
                Text("Busca por nombre, organización o comuna", fontFamily = InstrumentSans, fontSize = 14.sp, color = T.textMuted, maxLines = 1)
            }
            BasicTextField(
                value = valor,
                onValueChange = onChange,
                singleLine = true,
                textStyle = TextStyle(fontFamily = InstrumentSans, fontSize = 15.sp, color = T.textPrimary),
                cursorBrush = SolidColor(T.action),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { foco = it.isFocused },
            )
        }
        if (valor.isNotEmpty()) {
            IconButton(onClick = { onChange("") }) {
                Icon(Icons.Outlined.Close, contentDescription = "Borrar búsqueda", tint = T.textMuted)
            }
        }
    }
}
