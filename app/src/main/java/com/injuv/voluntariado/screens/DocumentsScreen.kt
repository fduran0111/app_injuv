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
import com.injuv.voluntariado.components.TarjetaDocumento
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
import com.injuv.voluntariado.ui.theme.InstrumentSans

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
                title = "Documentos oficiales",
                resaltado = "oficiales",
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
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
            )
        }
    }
}

private val CATEGORIAS_ACADEMIA = mapOf(
    "guias_manuales" to "Guías y manuales",
    "plantillas_formatos" to "Plantillas y formatos",
    "videos_tutoriales" to "Videos tutoriales",
)

@Composable
internal fun DocumentCard(doc: Documento, onDescargar: () -> Unit, modifier: Modifier = Modifier) {
    val etiqueta = if (doc.seccion == "academia") {
        doc.categoria?.let { CATEGORIAS_ACADEMIA[it] ?: it.replaceFirstChar { c -> c.uppercase() } }
    } else {
        doc.tematica_nombre
    }
    TarjetaDocumento(doc, onDescargar = onDescargar, modifier = modifier, etiqueta = etiqueta)
}
