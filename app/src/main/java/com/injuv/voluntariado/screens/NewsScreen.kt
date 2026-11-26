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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.components.AppScreenHeader
import com.injuv.voluntariado.components.TituloSeccion
import com.injuv.voluntariado.components.TarjetaNoticia
import com.injuv.voluntariado.components.EstadoCarga
import com.injuv.voluntariado.components.Vacio
import com.injuv.voluntariado.components.rememberCarga
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.data.api.Noticia
import com.injuv.voluntariado.data.api.formatearFecha
import com.injuv.voluntariado.data.api.minutosLectura
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.InstrumentSans

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
                title = "Noticias de la red",
                resaltado = "red",
                subtitle = "Novedades del voluntariado juvenil INJUV",
                onNotificationClick = onShowAlert,
                onMenuClick = onToggleMenu,
            )
        }

        if (carga.datos == null) {
            item { EstadoCarga(carga) {} }
        } else if (noticias.isEmpty()) {
            item { Vacio("Aún no hay noticias publicadas", "Cuando INJUV o las organizaciones publiquen novedades aparecerán aquí.") }
        }
        itemsIndexed(noticias, key = { _, n -> n.id }) { index, news ->
            if (index == 1) {
                TituloSeccion("Más noticias", colorTrazo = LocalAppColors.current.coral, modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 4.dp))
            }
            TarjetaNoticia(
                news,
                destacada = index == 0,
                onClick = { onOpenDetail(news.id) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}
