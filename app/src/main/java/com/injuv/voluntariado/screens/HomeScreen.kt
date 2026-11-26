package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.R
import com.injuv.voluntariado.components.AppScreenHeader
import com.injuv.voluntariado.components.BarraBicolor
import com.injuv.voluntariado.components.BloqueArea
import com.injuv.voluntariado.components.BotonPrimario
import com.injuv.voluntariado.components.EstadoCarga
import com.injuv.voluntariado.components.Tarjeta
import com.injuv.voluntariado.components.TarjetaNoticia
import com.injuv.voluntariado.components.TarjetaOportunidad
import com.injuv.voluntariado.components.TituloSeccion
import com.injuv.voluntariado.components.Vacio
import com.injuv.voluntariado.components.rememberCarga
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.data.api.SessionManager
import com.injuv.voluntariado.data.api.formatearHoras
import com.injuv.voluntariado.ui.theme.Bricolage
import com.injuv.voluntariado.ui.theme.InstrumentSans
import com.injuv.voluntariado.ui.theme.LocalAppColors

@Composable
fun HomeScreen(
    onOpenDetail: (Int) -> Unit,
    onShowAlert: () -> Unit,
    onToggleMenu: () -> Unit,
    userName: String = "Voluntario",
    onVerTodasOportunidades: () -> Unit = {},
    onOpenNewsDetail: (Int) -> Unit = {},
    onVerTodasNoticias: () -> Unit = {},
    onBuscarArea: (String) -> Unit = { onVerTodasOportunidades() },
) {
    val T = LocalAppColors.current
    val usuario by SessionManager.user.collectAsState()
    val stats = rememberCarga { ApiClient.call { estadisticas() } }
    val portada = rememberCarga { runCatching { ApiClient.call { portada() } }.getOrNull() }
    val oportunidades = rememberCarga { ApiClient.call { oportunidades(limit = 10) } }
    val noticias = rememberCarga { ApiClient.call { noticias(limit = 6) } }
    val nombre = usuario?.nombre?.ifBlank { null } ?: userName.split(" ").first().ifBlank { "voluntario/a" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            AppScreenHeader(
                title = "Hola, $nombre",
                resaltado = nombre,
                subtitle = "Encuentra tu próxima experiencia de voluntariado",
                onNotificationClick = onShowAlert,
                onMenuClick = onToggleMenu,
            )
        }

        // Portada: foto administrada desde la web, titular con la palabra clave en ámbar y acción.
        item {
            val foto = portada.datos?.fotos?.inicio?.firstOrNull()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .height(236.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF0F1B2D)),
            ) {
                AsyncImage(
                    model = foto?.let { ApiClient.fotoPortada(it.archivo) } ?: R.drawable.foto_acceso,
                    contentDescription = foto?.alt ?: "Voluntariado juvenil INJUV",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color(0x1A0A1220), Color(0x990A1220), Color(0xF20A1220)))),
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(18.dp),
                ) {
                    Text(
                        buildAnnotatedString {
                            append("Chile en ")
                            withStyle(SpanStyle(color = Color(0xFFF2A93B))) { append("movimiento") }
                        },
                        fontFamily = Bricolage,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp,
                        lineHeight = 32.sp,
                        letterSpacing = (-0.03).em,
                        color = Color.White,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Tu tiempo puede cambiar una comunidad.",
                        fontFamily = InstrumentSans,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.85f),
                    )
                    Spacer(Modifier.height(14.dp))
                    BotonPrimario(
                        "Explorar voluntariados",
                        onClick = onVerTodasOportunidades,
                        icono = Icons.AutoMirrored.Filled.ArrowForward,
                        color = Color(0xFFF2A93B),
                        colorTexto = Color(0xFF0F1B2D),
                        alto = 46.dp,
                    )
                }
            }
        }

        // Cifras reales de la red (GET /public/estadisticas) y las horas propias.
        item {
            val e = stats.datos
            Tarjeta(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
            ) {
                Row(Modifier.padding(vertical = 16.dp)) {
                    Cifra(e?.voluntarios?.let(::miles) ?: "—", "Voluntarios", Modifier.weight(1f))
                    SeparadorCifra()
                    Cifra(e?.organizaciones?.let(::miles) ?: "—", "Organizaciones", Modifier.weight(1f))
                    SeparadorCifra()
                    Cifra(usuario?.horas_voluntariado?.let { formatearHoras(it) } ?: "0", "Tus horas", Modifier.weight(1f), acento = true)
                }
                BarraBicolor(Modifier.fillMaxWidth(), alto = 4.dp)
            }
        }

        // Áreas presentes en las convocatorias abiertas: atajo al filtro de Postular.
        item {
            val areas = oportunidades.datos.orEmpty().map { it.tipo_de_voluntariado }.filter { it.isNotBlank() }.distinct()
            if (areas.isNotEmpty()) {
                TituloSeccion("Explora por área", modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(areas) { area ->
                        BloqueArea(
                            area,
                            tamanoNombre = 16.sp,
                            modifier = Modifier
                                .size(width = 132.dp, height = 104.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, T.border1, RoundedCornerShape(16.dp))
                                .clickable(role = Role.Button) { onBuscarArea(area) },
                        )
                    }
                }
            }
        }

        item {
            TituloSeccion(
                "Oportunidades",
                accion = "Ver todas",
                onAccion = onVerTodasOportunidades,
                modifier = Modifier.padding(start = 20.dp, end = 12.dp, top = 22.dp, bottom = 10.dp),
            )
            EstadoCarga(oportunidades) { lista ->
                if (lista.isEmpty()) {
                    Vacio("No hay oportunidades abiertas", "Vuelve pronto: las organizaciones publican nuevas convocatorias seguido.")
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(lista, key = { it.id }) { op ->
                            TarjetaOportunidad(op, onClick = { onOpenDetail(op.id) }, modifier = Modifier.width(292.dp), compacta = true)
                        }
                    }
                }
            }
        }

        item {
            TituloSeccion(
                "Últimas noticias",
                accion = "Ver todas",
                onAccion = onVerTodasNoticias,
                colorTrazo = T.coral,
                modifier = Modifier.padding(start = 20.dp, end = 12.dp, top = 24.dp, bottom = 10.dp),
            )
            EstadoCarga(noticias) { lista ->
                if (lista.isEmpty()) {
                    Vacio("Aún no hay noticias publicadas")
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(lista, key = { it.id }) { news ->
                            TarjetaNoticia(news, onClick = { onOpenNewsDetail(news.id) }, modifier = Modifier.width(272.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Cifra(valor: String, etiqueta: String, modifier: Modifier = Modifier, acento: Boolean = false) {
    val T = LocalAppColors.current
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            valor,
            fontFamily = Bricolage,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 25.sp,
            letterSpacing = (-0.02).em,
            color = if (acento) (if (T.isDark) T.ambar else T.coral) else T.textPrimary,
            style = TextStyle(fontFeatureSettings = "tnum"),
            maxLines = 1,
        )
        Text(etiqueta, fontFamily = InstrumentSans, fontSize = 12.sp, color = T.textMuted, maxLines = 1)
    }
}

@Composable
private fun SeparadorCifra() {
    val T = LocalAppColors.current
    Box(
        Modifier
            .padding(vertical = 4.dp)
            .width(1.dp)
            .height(40.dp)
            .background(T.border2),
    )
}

private fun miles(n: Int): String = "%,d".format(n).replace(',', '.')
