package com.injuv.voluntariado.components

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
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
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

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
        CircularProgressIndicator(color = T.coral, modifier = Modifier.size(32.dp))
        Spacer(Modifier.height(12.dp))
        Text(texto, fontFamily = Outfit, fontSize = 13.sp, color = T.textMuted)
    }
}

@Composable
fun ErrorCarga(mensaje: String, onReintentar: () -> Unit, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Filled.CloudOff, contentDescription = null, tint = T.textMuted, modifier = Modifier.size(36.dp))
        Spacer(Modifier.height(10.dp))
        Text(mensaje, fontFamily = Outfit, fontSize = 13.sp, color = T.textSecondary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(14.dp))
        Button(
            onClick = onReintentar,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = T.coral),
        ) { Text("Reintentar", fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold) }
    }
}

@Composable
fun Vacio(titulo: String, descripcion: String? = null, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Filled.Inbox, contentDescription = null, tint = T.textSubtle, modifier = Modifier.size(36.dp))
        Spacer(Modifier.height(10.dp))
        Text(titulo, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary, textAlign = TextAlign.Center)
        if (descripcion != null) {
            Spacer(Modifier.height(4.dp))
            Text(descripcion, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted, textAlign = TextAlign.Center)
        }
    }
}

/** Etiqueta con el estado de una postulación, con el color según la etapa. */
@Composable
fun EstadoPostulacionChip(estado: String, modifier: Modifier = Modifier) {
    val color = colorEstado(estado, LocalAppColors.current)
    Text(
        estado,
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        color = color,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

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
 * Imagen de una oportunidad. El backend no guarda fotos por oportunidad: se usa el logo de la
 * organización si tiene, y si no un fondo con el ícono de voluntariado.
 */
@Composable
fun OportunidadImagen(oportunidad: Oportunidad, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Box(
        modifier = modifier.background(Brush.linearGradient(listOf(T.coral.copy(alpha = 0.85f), T.coralDark))),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Filled.VolunteerActivism, contentDescription = null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(34.dp))
        if (oportunidad.organizacion_tiene_logo) {
            AsyncImage(
                model = ApiClient.logoOrganizacion(oportunidad.organizacion_id),
                contentDescription = oportunidad.organizacion_nombre,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White)
                    .padding(12.dp),
            )
        }
    }
}
