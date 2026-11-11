package com.injuv.voluntariado.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MenuBook
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
import com.injuv.voluntariado.data.api.anioDe
import com.injuv.voluntariado.data.api.tipoArchivo
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

private const val PREFS = "injuv_prefs"
private const val KEY_GUARDADOS = "biblioteca_guardados"

/*
 * Biblioteca en grilla (GET /biblioteca/documentos). "Guardar" se almacena solo en el
 * teléfono: el backend todavía no tiene favoritos por usuario.
 */
@Composable
fun LibraryScreen(onShowAlert: () -> Unit, onToggleMenu: () -> Unit) {
    val T = LocalAppColors.current
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS, Context.MODE_PRIVATE) }
    var guardados by remember { mutableStateOf(prefs.getStringSet(KEY_GUARDADOS, emptySet()).orEmpty()) }
    var selectedCategory by remember { mutableStateOf("Todos") }
    val carga = rememberCarga { ApiClient.call { biblioteca() } }
    val docs = carga.datos.orEmpty()
    val categorias = listOf("Todos", "Guardados") + docs.mapNotNull { it.tematica_nombre?.ifBlank { null } }.distinct().sorted()
    val filtered = when (selectedCategory) {
        "Todos" -> docs
        "Guardados" -> docs.filter { it.id.toString() in guardados }
        else -> docs.filter { it.tematica_nombre == selectedCategory }
    }

    fun alternar(doc: Documento) {
        val id = doc.id.toString()
        guardados = if (id in guardados) guardados - id else guardados + id
        prefs.edit().putStringSet(KEY_GUARDADOS, guardados).apply()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase),
        contentPadding = PaddingValues(bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(span = { GridItemSpan(2) }) {
            AppScreenHeader(
                title = "Biblioteca",
                subtitle = "Lecturas y materiales sobre voluntariado",
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
            item(span = { GridItemSpan(2) }) { EstadoCarga(carga) {} }
        } else if (filtered.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Vacio(if (selectedCategory == "Guardados") "Aún no guardas documentos" else "No hay documentos en esta temática")
            }
        }
        items(filtered, key = { it.id }) { doc ->
            BookCard(
                doc,
                guardado = doc.id.toString() in guardados,
                onGuardar = { alternar(doc) },
                onAbrir = { descargarArchivo(context, ApiClient.descargaDocumento(doc), doc.nombre_archivo) },
                modifier = Modifier.padding(horizontal = 6.dp),
            )
        }
    }
}

@Composable
private fun BookCard(
    doc: Documento,
    guardado: Boolean,
    onGuardar: () -> Unit,
    onAbrir: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val T = LocalAppColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .clickable(onClick = onAbrir)
            .padding(10.dp),
    ) {
        Box {
            // Portada: los documentos no traen imagen, se usa una portada con el tipo de archivo.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(listOf(T.blue.copy(alpha = 0.55f), T.coral.copy(alpha = 0.55f)))),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.MenuBook, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
                    Spacer(Modifier.height(6.dp))
                    Text(tipoArchivo(doc.archivo_mime, doc.nombre_archivo), fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color.White)
                }
            }
            IconButton(
                onClick = onGuardar,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.35f))
                    .size(30.dp),
            ) {
                Icon(
                    if (guardado) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = if (guardado) "Quitar de guardados" else "Guardar",
                    tint = if (guardado) T.yellow else Color.White,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(doc.nombre_archivo, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = T.textPrimary, maxLines = 2)
        Text(doc.autor.ifBlank { doc.tematica_nombre ?: "INJUV" }, fontFamily = Outfit, fontSize = 11.sp, color = T.textMuted, maxLines = 1)
        Spacer(Modifier.height(4.dp))
        Text(anioDe(doc.fecha_edicion ?: doc.created_at), fontFamily = Outfit, fontSize = 10.sp, color = T.textSubtle)
    }
}
