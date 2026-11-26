package com.injuv.voluntariado.components

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.data.api.ApiException
import com.injuv.voluntariado.data.api.Oportunidad
import com.injuv.voluntariado.data.api.SessionManager
import com.injuv.voluntariado.ui.theme.AppColors
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Bricolage
import com.injuv.voluntariado.ui.theme.InstrumentSans

/** Estado de una carga desde la API. */
sealed interface Carga<out T> {
    data object Cargando : Carga<Nothing>
    data class Lista<T>(val datos: T) : Carga<T>
    data class Error(val mensaje: String) : Carga<Nothing>
}

@Stable
class CargaState<T>(private val reintentarFn: () -> Unit) {
    var estado: Carga<T> by mutableStateOf(Carga.Cargando)
        internal set
    val datos: T? get() = (estado as? Carga.Lista<T>)?.datos
    fun recargar() = reintentarFn()
}

/**
 * Carga datos de la API al entrar a la pantalla (y cada vez que cambia [key] o se llama a
 * `recargar()`). Equivale al hook `useApi` del frontend web.
 */
@Composable
fun <T> rememberCarga(vararg key: Any?, cargar: suspend () -> T): CargaState<T> {
    var intento by remember { mutableIntStateOf(0) }
    val state = remember(*key) { CargaState<T> { intento++ } }
    LaunchedEffect(*key, intento) {
        if (state.datos == null) state.estado = Carga.Cargando
        state.estado = try {
            Carga.Lista(cargar())
        } catch (e: ApiException) {
            Carga.Error(e.message ?: "Error")
        } catch (e: Exception) {
            Carga.Error("Ocurrió un error inesperado.")
        }
    }
    return state
}

/** Muestra spinner, error con "Reintentar" o el contenido según el estado. */
@Composable
fun <T> EstadoCarga(
    state: CargaState<T>,
    modifier: Modifier = Modifier,
    contenido: @Composable (T) -> Unit,
) {
    when (val e = state.estado) {
        is Carga.Cargando -> Cargando(modifier)
        is Carga.Error -> ErrorCarga(e.mensaje, onReintentar = state::recargar, modifier = modifier)
        is Carga.Lista -> contenido(e.datos)
    }
}

@Composable
fun Cargando(modifier: Modifier = Modifier, texto: String = "Cargando…") {
    val T = LocalAppColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(color = if (T.isDark) T.ambar else T.action, strokeWidth = 3.dp, modifier = Modifier.size(30.dp))
        Spacer(Modifier.height(12.dp))
        Text(texto, fontFamily = InstrumentSans, fontSize = 13.sp, color = T.textMuted)
    }
}

@Composable
fun ErrorCarga(mensaje: String, onReintentar: () -> Unit, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    val c = coloresTono(Tono.Rojo, T)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(c.fondo)
            .border(1.5.dp, c.anillo, RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CloudOff, contentDescription = null, tint = c.texto, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(mensaje, fontFamily = InstrumentSans, fontSize = 14.sp, color = c.texto, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        BotonContorno("Reintentar", onReintentar, alto = 44.dp, color = c.texto)
    }
}

/** Estado vacío de la web (EmptyState): caja punteada con ícono en recuadro de tinta. */
@Composable
fun Vacio(titulo: String, descripcion: String? = null, modifier: Modifier = Modifier, icono: ImageVector = Icons.Outlined.Inbox) {
    val T = LocalAppColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .drawBehind {
                drawRoundRect(
                    color = T.border2,
                    cornerRadius = CornerRadius(14.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(7.dp.toPx(), 5.dp.toPx()))),
                )
            }
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(T.card)
                .border(1.5.dp, T.textPrimary.copy(alpha = 0.7f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icono, contentDescription = null, tint = T.textPrimary, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(14.dp))
        Text(titulo, fontFamily = Bricolage, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = T.textPrimary, textAlign = TextAlign.Center)
        if (descripcion != null) {
            Spacer(Modifier.height(4.dp))
            Text(descripcion, fontFamily = InstrumentSans, fontSize = 13.sp, color = T.textMuted, textAlign = TextAlign.Center)
        }
    }
}

/** Etiqueta con el estado de una postulación, con el tono de la web según la etapa. */
@Composable
fun EstadoPostulacionChip(estado: String, modifier: Modifier = Modifier) = EtiquetaEstado(estado, modifier)

fun colorEstado(estado: String, T: AppColors): Color = when (estado) {
    "Seleccionado", "Finalizado" -> T.green
    "No seleccionado", "Declinada por usuario" -> T.textMuted
    "Pre-seleccionado", "Etapa de entrevista" -> T.blue
    else -> T.yellow
}

/**
 * Descarga un archivo de la API con el gestor de descargas de Android (queda en "Descargas"
 * y avisa con una notificación). Envía el token, así sirve también para certificados privados.
 */
fun descargarArchivo(context: Context, url: String, nombreArchivo: String) {
    try {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(nombreArchivo)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nombreArchivo.replace(Regex("[\\\\/:*?\"<>|]"), "_"))
        SessionManager.token?.let { request.addRequestHeader("Authorization", "Bearer $it") }
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
        Toast.makeText(context, "Descargando $nombreArchivo…", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "No se pudo iniciar la descarga", Toast.LENGTH_LONG).show()
    }
}

/**
 * Imagen de una oportunidad: se usa la foto propia de la oportunidad si tiene; si no, el logo
 * de la organización; y si tampoco, un fondo con el ícono de voluntariado.
 */
@Composable
fun OportunidadImagen(oportunidad: Oportunidad, modifier: Modifier = Modifier, mostrarArea: Boolean = false) {
    BloqueArea(oportunidad.tipo_de_voluntariado, modifier = modifier, mostrarNombre = mostrarArea && !oportunidad.tiene_imagen && !oportunidad.organizacion_tiene_logo) {
        if (oportunidad.tiene_imagen) {
            AsyncImage(
                model = ApiClient.imagenOportunidad(oportunidad.id),
                contentDescription = oportunidad.titulo,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        } else if (oportunidad.organizacion_tiene_logo) {
            AsyncImage(
                model = ApiClient.logoOrganizacion(oportunidad.organizacion_id),
                contentDescription = oportunidad.organizacion_nombre,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White)
                    .padding(14.dp),
            )
        }
    }
}
