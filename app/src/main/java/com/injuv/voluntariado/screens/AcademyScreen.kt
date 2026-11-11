package com.injuv.voluntariado.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.injuv.voluntariado.components.AppScreenHeader
import com.injuv.voluntariado.components.EstadoCarga
import com.injuv.voluntariado.components.FilterChipsRow
import com.injuv.voluntariado.components.Vacio
import com.injuv.voluntariado.components.descargarArchivo
import com.injuv.voluntariado.components.rememberCarga
import com.injuv.voluntariado.data.api.ApiClient

/*
 * Academia (GET /academia/documentos): material de formación aprobado por INJUV, publicado
 * por las organizaciones. En la plataforma son documentos y videos, no cursos con progreso,
 * por eso la tarjeta es de documento (el diseño original de "cursos" quedó pendiente de
 * definir con el equipo).
 */
@Composable
fun AcademyScreen(onShowAlert: () -> Unit, onToggleMenu: () -> Unit) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf("Todos") }
    val carga = rememberCarga { ApiClient.call { academia() } }
    val docs = carga.datos.orEmpty()
    val categorias = listOf("Todos") + docs.mapNotNull { it.categoria?.ifBlank { null } }.distinct().sorted().map(::capitalizar)
    val filtered = if (selected == "Todos") docs else docs.filter { capitalizar(it.categoria.orEmpty()) == selected }
    val filtros: (@Composable () -> Unit)? = if (categorias.size > 1) {
        { FilterChipsRow(options = categorias, selected = selected, onSelect = { selected = it }) }
    } else {
        null
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            AppScreenHeader(
                title = "Academia",
                subtitle = "Material para potenciar tu voluntariado",
                onNotificationClick = onShowAlert,
                onMenuClick = onToggleMenu,
                bottomContent = filtros,
            )
        }

        if (carga.datos == null) {
            item { EstadoCarga(carga) {} }
        } else if (filtered.isEmpty()) {
            item { Vacio("Aún no hay material en la Academia", "Las organizaciones lo publican y INJUV lo aprueba.") }
        }
        items(filtered, key = { it.id }) { doc ->
            DocumentCard(
                doc.copy(descripcion = listOfNotNull(doc.organizacion_nombre, doc.descripcion.ifBlank { null }).joinToString(" · ")),
                onDescargar = { descargarArchivo(context, ApiClient.descargaDocumento(doc), doc.nombre_archivo) },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
            )
        }
    }
}

private fun capitalizar(s: String) = s.replaceFirstChar { it.uppercase() }
