package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.components.AppScreenHeader
import com.injuv.voluntariado.components.EstadoCarga
import com.injuv.voluntariado.components.Vacio
import com.injuv.voluntariado.components.rememberCarga
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.data.api.Noticia
import com.injuv.voluntariado.data.api.formatearFecha
import com.injuv.voluntariado.data.api.minutosLectura
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

/** Noticias publicadas (GET /noticias). Tocar una tarjeta abre el detalle. */
@Composable
fun NewsScreen(
    onShowAlert: () -> Unit,
    onToggleMenu: () -> Unit,
    onOpenDetail: (Int) -> Unit = {},
) {
    val carga = rememberCarga { ApiClient.call { noticias() } }
    val noticias = carga.datos.orEmpty()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            AppScreenHeader(
                title = "Noticias",
                subtitle = "Novedades del programa de voluntariado",
                onNotificationClick = onShowAlert,
                onMenuClick = onToggleMenu,
            )
        }

        if (carga.datos == null) {
            item { EstadoCarga(carga) {} }
        } else if (noticias.isEmpty()) {
            item { Vacio("Aún no hay noticias publicadas") }
        }
        itemsIndexed(noticias, key = { _, n -> n.id }) { index, news ->
            NewsCard(
                news,
                destacada = index == 0,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                onClick = { onOpenDetail(news.id) },
            )
        }
    }
}

@Composable
private fun NewsCard(news: Noticia, destacada: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val T = LocalAppColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .clickable(onClick = onClick),
    ) {
        news.imagen?.let { archivo ->
            AsyncImage(
                model = ApiClient.imagenNoticia(archivo),
                contentDescription = news.titulo,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (destacada) 170.dp else 120.dp),
            )
        }
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                news.autor_nombre.uppercase(),
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                color = T.coral,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                news.titulo,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = if (destacada) 16.sp else 14.sp,
                color = T.textPrimary,
            )
            Spacer(Modifier.height(4.dp))
            Text(news.resumen.ifBlank { news.contenido }, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted, maxLines = 2)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatearFecha(news.fecha_publicacion), fontFamily = Outfit, fontSize = 11.sp, color = T.textSubtle)
                Text(
                    "${minutosLectura(news.contenido)} min de lectura",
                    fontFamily = Outfit,
                    fontSize = 11.sp,
                    color = T.textSubtle,
                )
            }
        }
    }
}
