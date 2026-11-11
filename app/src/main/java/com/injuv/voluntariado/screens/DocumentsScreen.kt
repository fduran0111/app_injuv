package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.injuv.voluntariado.components.AppScreenHeader
import com.injuv.voluntariado.components.EstadoCarga
import com.injuv.voluntariado.components.FilterChipsRow
import com.injuv.voluntariado.components.Vacio
import com.injuv.voluntariado.components.descargarArchivo
import com.injuv.voluntariado.components.rememberCarga
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.data.api.Documento
import com.injuv.voluntariado.data.api.formatearFecha
import com.injuv.voluntariado.data.api.formatearTamano
import com.injuv.voluntariado.data.api.tipoArchivo
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

/** Biblioteca de documentos (GET /biblioteca/documentos), filtrable por temática. */
@Composable
fun DocumentsScreen(onShowAlert: () -> Unit, onToggleMenu: () -> Unit) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("Todos") }
    val carga = rememberCarga { ApiClient.call { biblioteca() } }
    val docs = carga.datos.orEmpty()
    val categorias = listOf("Todos") + docs.mapNotNull { it.tematica_nombre?.ifBlank { null } }.distinct().sorted()
    val filtered = if (selectedCategory == "Todos") docs else docs.filter { it.tematica_nombre == selectedCategory }
    val filtros: (@Composable () -> Unit)? = if (categorias.size > 1) {
        { FilterChipsRow(options = categorias, selected = selectedCategory, onSelect = { selectedCategory = it }) }
    } else {
        null
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            AppScreenHeader(
                title = "Documentos",
                subtitle = "Reglamentos, formularios y guías descargables",
                onNotificationClick = onShowAlert,
                onMenuClick = onToggleMenu,
                bottomContent = filtros,
            )
        }

        if (carga.datos == null) {
            item { EstadoCarga(carga) {} }
        } else if (filtered.isEmpty()) {
            item { Vacio("No hay documentos disponibles") }
        }
        items(filtered, key = { it.id }) { doc ->
            DocumentCard(
                doc,
                onDescargar = { descargarArchivo(context, ApiClient.descargaDocumento(doc), doc.nombre_archivo) },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
            )
        }
    }
}

@Composable
internal fun DocumentCard(doc: Documento, onDescargar: () -> Unit, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    val tipo = tipoArchivo(doc.archivo_mime, doc.nombre_archivo)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(T.blue.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Description, contentDescription = tipo, tint = T.blue)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(doc.nombre_archivo, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = T.textPrimary, maxLines = 2)
            if (doc.descripcion.isNotBlank()) {
                Text(doc.descripcion, fontFamily = Outfit, fontSize = 11.sp, color = T.textMuted, maxLines = 2)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                listOf(tipo, formatearTamano(doc.archivo_tamano_bytes), formatearFecha(doc.fecha_edicion ?: doc.created_at, ""))
                    .filter { it.isNotBlank() }.joinToString(" · "),
                fontFamily = Outfit,
                fontSize = 11.sp,
                color = T.textSubtle,
            )
        }
        IconButton(onClick = onDescargar) {
            Icon(Icons.Filled.Download, contentDescription = "Descargar", tint = T.coral)
        }
    }
}
