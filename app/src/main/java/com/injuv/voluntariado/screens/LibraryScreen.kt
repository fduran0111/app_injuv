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
import com.injuv.voluntariado.components.tonoArea
import androidx.compose.ui.unit.em
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.aspectRatio
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
import com.injuv.voluntariado.ui.theme.Bricolage
import com.injuv.voluntariado.ui.theme.InstrumentSans

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
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(span = { GridItemSpan(2) }) {
            AppScreenHeader(
                title = "Biblioteca abierta",
                resaltado = "Biblioteca",
                subtitle = "Lecturas y materiales sobre voluntariado",
                onNotificationClick = onShowAlert,
                onMenuClick = onToggleMenu,
                bottomContent = {
                    FilterChipsRow(
                        options = categorias,
                        selected = selectedCategory,
                        onSelect = { selectedCategory = it },
                        counts = mapOf("Todos" to docs.size, "Guardados" to guardados.size),
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

/** Portada de libro: bloque de color de la temática, ícono como marca de agua y título impreso. */
@Composable
private fun BookCard(
    doc: Documento,
    guardado: Boolean,
    onGuardar: () -> Unit,
    onAbrir: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val T = LocalAppColors.current
    val (fondo, texto, marca) = tonoArea(doc.tematica_nombre.orEmpty(), T)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(role = Role.Button, onClick = onAbrir),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .shadow(8.dp, RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 12.dp, bottomEnd = 12.dp), ambientColor = Color(0x330F1B2D), spotColor = Color(0x660F1B2D))
                .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 12.dp, bottomEnd = 12.dp))
                .background(fondo),
        ) {
            // Lomo del libro
            Box(Modifier.width(8.dp).fillMaxHeight().background(Color.Black.copy(alpha = 0.18f)))
            Icon(
                Icons.Filled.MenuBook,
                contentDescription = null,
                tint = marca,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 18.dp, y = 18.dp)
                    .size(110.dp)
                    .rotate(-12f),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 18.dp, end = 12.dp, top = 14.dp, bottom = 14.dp),
            ) {
                Text(
                    tipoArchivo(doc.archivo_mime, doc.nombre_archivo),
                    fontFamily = InstrumentSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 0.08.em,
                    color = texto.copy(alpha = 0.8f),
                )
                Spacer(Modifier.weight(1f))
                Text(
                    doc.nombre_archivo.substringBeforeLast('.'),
                    fontFamily = Bricolage,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    lineHeight = 18.sp,
                    color = texto,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(
                onClick = onGuardar,
                modifier = Modifier.align(Alignment.TopEnd),
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF4EFE6).copy(alpha = 0.92f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        if (guardado) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = if (guardado) "Quitar de guardados" else "Guardar",
                        tint = if (guardado) Color(0xFFC81E25) else Color(0xFF0F1B2D),
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            doc.autor.ifBlank { doc.tematica_nombre ?: "INJUV" },
            fontFamily = InstrumentSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = T.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            listOfNotNull(doc.tematica_nombre, anioDe(doc.fecha_edicion ?: doc.created_at).ifBlank { null }).joinToString(" · "),
            fontFamily = InstrumentSans,
            fontSize = 12.sp,
            color = T.textMuted,
            maxLines = 1,
        )
    }
}
