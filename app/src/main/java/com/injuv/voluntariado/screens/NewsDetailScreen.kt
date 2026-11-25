package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.injuv.voluntariado.components.BarraBicolor
import com.injuv.voluntariado.components.ImagenNoticia
import com.injuv.voluntariado.components.TituloMarcador
import com.injuv.voluntariado.components.Tono
import com.injuv.voluntariado.components.Etiqueta
import com.injuv.voluntariado.components.BotonCirculo
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.components.EstadoCarga
import com.injuv.voluntariado.components.Vacio
import com.injuv.voluntariado.components.rememberCarga
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.data.api.Noticia
import com.injuv.voluntariado.data.api.formatearFecha
import com.injuv.voluntariado.data.api.minutosLectura
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.InstrumentSans
import com.injuv.voluntariado.ui.theme.Bricolage

/** Detalle de una noticia. La API no expone GET /noticias/{id}, así que se busca en el listado. */
@Composable
fun NewsDetailScreen(newsId: Int, onBack: () -> Unit) {
    val T = LocalAppColors.current
    val carga = rememberCarga(newsId) { ApiClient.call { noticias() }.firstOrNull { it.id == newsId } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase),
    ) {
        EstadoCarga(carga) { news ->
            if (news == null) {
                BackButton(onBack)
                Vacio("La noticia ya no está disponible")
            } else {
                NewsDetailContent(news, onBack)
            }
        }
    }
}

@Composable
private fun BackButton(onBack: () -> Unit, modifier: Modifier = Modifier) {
    BotonCirculo(Icons.AutoMirrored.Filled.ArrowBack, "Volver", onBack, modifier.padding(8.dp))
}

@Composable
private fun NewsDetailContent(news: Noticia, onBack: () -> Unit) {
    val T = LocalAppColors.current
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        BackButton(onBack)
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Etiqueta(news.autor_nombre, Tono.Rojo)
                Etiqueta("${minutosLectura(news.contenido)} min de lectura", Tono.Neutral, icono = Icons.Filled.Schedule)
            }
            Spacer(Modifier.height(14.dp))
            TituloMarcador(news.titulo, fontSize = 28.sp, maxLines = 6)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = T.textMuted, modifier = Modifier.size(14.dp))
                Text(
                    "Publicada el ${formatearFecha(news.fecha_publicacion)}",
                    fontFamily = InstrumentSans,
                    fontSize = 13.sp,
                    color = T.textMuted,
                    modifier = Modifier.padding(start = 6.dp),
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, T.border1, RoundedCornerShape(18.dp)),
        ) {
            ImagenNoticia(news, Modifier.fillMaxSize())
        }
        Spacer(Modifier.height(12.dp))
        BarraBicolor(Modifier.padding(horizontal = 16.dp).fillMaxWidth().clip(RoundedCornerShape(50)), alto = 4.dp)

        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp)) {
            if (news.resumen.isNotBlank() && news.contenido.isNotBlank()) {
                Text(
                    news.resumen,
                    fontFamily = Bricolage,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    lineHeight = 25.sp,
                    color = T.textPrimary,
                )
                Spacer(Modifier.height(16.dp))
            }
            news.contenido.ifBlank { news.resumen }.split("\n\n").forEach { paragraph ->
                Text(
                    paragraph,
                    fontFamily = InstrumentSans,
                    fontSize = 16.sp,
                    lineHeight = 26.sp,
                    color = T.textSecondary,
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
