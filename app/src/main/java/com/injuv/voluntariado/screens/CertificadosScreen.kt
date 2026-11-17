package com.injuv.voluntariado.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.injuv.voluntariado.components.descargarArchivo
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.data.api.PostulacionUsuario
import com.injuv.voluntariado.data.api.Usuario
import com.injuv.voluntariado.data.api.subirCertPersonal
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

/** Tipos de certificado personal que acepta la plataforma. */
private val TIPOS_PERSONALES = listOf(
    "antecedentes" to "Certificado de antecedentes",
    "inhabilidades" to "Certificado de inhabilidades",
)

/**
 * Mis certificados: los emitidos por las organizaciones al terminar un voluntariado y los
 * personales (antecedentes, inhabilidades) que la plataforma pide al postular.
 */
@Composable
fun CertificadosScreen(
    usuario: Usuario?,
    postulaciones: List<PostulacionUsuario>,
    onBack: () -> Unit,
    onUsuarioActualizado: (Usuario) -> Unit,
) {
    val T = LocalAppColors.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val conCertificado = postulaciones.filter { it.tiene_certificado }
    var subiendo by remember { mutableStateOf(false) }
    var tipoPendiente by remember { mutableStateOf<String?>(null) }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val tipo = tipoPendiente
        tipoPendiente = null
        if (uri == null || tipo == null) return@rememberLauncherForActivityResult
        scope.launch {
            subiendo = true
            runCatching {
                subirCertPersonal(
                    context,
                    tipo,
                    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                    uri,
                )
            }.onSuccess { actualizado ->
                onUsuarioActualizado(actualizado)
                Toast.makeText(context, "Certificado subido", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(context, it.message ?: "No se pudo subir el certificado", Toast.LENGTH_LONG).show()
            }
            subiendo = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(T.bgHeaderGradient))
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Column(modifier = Modifier.padding(end = 16.dp)) {
                Text(
                    "Mis certificados",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White,
                )
                Text(
                    "De voluntariados y documentos personales",
                    fontFamily = Outfit,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f),
                )
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            SeccionTitulo("De voluntariados")
            Spacer(Modifier.height(10.dp))
            if (conCertificado.isEmpty()) {
                Text(
                    "Cuando una organización emita tu certificado al terminar un voluntariado, aparecerá aquí.",
                    fontFamily = Outfit,
                    fontSize = 13.sp,
                    color = T.textMuted,
                )
            }
            conCertificado.forEach { p ->
                CertificadoVoluntariadoCard(
                    titulo = p.oportunidad_titulo,
                    organizacion = p.organizacion_nombre,
                    onDescargar = {
                        descargarArchivo(
                            context,
                            ApiClient.certificadoPostulacion(p.id),
                            "certificado_${p.oportunidad_titulo.take(40)}.pdf",
                        )
                    },
                )
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(22.dp))
            SeccionTitulo("Personales")
            Text(
                "La plataforma los pide al postular; deben estar emitidos hace menos de 6 meses.",
                fontFamily = Outfit,
                fontSize = 12.sp,
                color = T.textMuted,
            )
            Spacer(Modifier.height(10.dp))
            TIPOS_PERSONALES.forEach { (tipo, nombre) ->
                val cert = usuario?.certificados_personales?.find { it.tipo == tipo }
                CertificadoPersonalCard(
                    nombre = nombre,
                    nombreArchivo = cert?.nombre_archivo,
                    subidoEn = cert?.subido_en,
                    subiendo = subiendo,
                    onSubir = {
                        tipoPendiente = tipo
                        filePicker.launch("application/pdf")
                    },
                    onDescargar = {
                        descargarArchivo(context, ApiClient.certificadoPersonal(tipo), "$tipo.pdf")
                    },
                    onEliminar = {
                        scope.launch {
                            runCatching { ApiClient.call { eliminarCertificadoPersonal(tipo) } }
                                .onSuccess { actualizado ->
                                    onUsuarioActualizado(actualizado)
                                    Toast.makeText(context, "Certificado eliminado", Toast.LENGTH_SHORT).show()
                                }
                                .onFailure {
                                    Toast.makeText(context, it.message ?: "No se pudo eliminar", Toast.LENGTH_LONG).show()
                                }
                        }
                    },
                )
                Spacer(Modifier.height(10.dp))
            }
        }
    }

    // Al elegir "Subir" se abre directo el selector de PDF; la emisión se registra con la fecha de hoy.
}

@Composable
private fun SeccionTitulo(texto: String) {
    val T = LocalAppColors.current
    Text(
        texto,
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = T.textMuted,
    )
}

@Composable
private fun CertificadoVoluntariadoCard(titulo: String, organizacion: String, onDescargar: () -> Unit) {
    val T = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = T.yellow, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(titulo, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = T.textPrimary)
            }
            if (organizacion.isNotBlank()) {
                Text(organizacion, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
            }
        }
        IconButton(onClick = onDescargar) {
            Icon(Icons.Filled.Download, contentDescription = "Descargar certificado", tint = T.green)
        }
    }
}

@Composable
private fun CertificadoPersonalCard(
    nombre: String,
    nombreArchivo: String?,
    subidoEn: String?,
    subiendo: Boolean,
    onSubir: () -> Unit,
    onDescargar: () -> Unit,
    onEliminar: () -> Unit,
) {
    val T = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.PictureAsPdf, contentDescription = null, tint = T.coral, modifier = Modifier.size(22.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Text(nombre, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = T.textPrimary)
                Text(
                    if (nombreArchivo != null) {
                        listOfNotNull(nombreArchivo, subidoEn?.let { "subido el ${it.take(10)}" }).joinToString(" · ")
                    } else {
                        "No subido"
                    },
                    fontFamily = Outfit,
                    fontSize = 12.sp,
                    color = T.textMuted,
                )
            }
            if (subiendo) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = T.coral)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CertAccion(
                icono = { Icon(Icons.Filled.Upload, contentDescription = null, tint = T.coral, modifier = Modifier.size(16.dp)) },
                texto = if (nombreArchivo != null) "Reemplazar" else "Subir PDF",
                onClick = onSubir,
            )
            if (nombreArchivo != null) {
                CertAccion(
                    icono = { Icon(Icons.Filled.Download, contentDescription = null, tint = T.green, modifier = Modifier.size(16.dp)) },
                    texto = "Descargar",
                    onClick = onDescargar,
                )
                CertAccion(
                    icono = { Icon(Icons.Filled.Delete, contentDescription = null, tint = T.textMuted, modifier = Modifier.size(16.dp)) },
                    texto = "Eliminar",
                    onClick = onEliminar,
                )
            }
        }
    }
}

@Composable
private fun CertAccion(icono: @Composable () -> Unit, texto: String, onClick: () -> Unit) {
    val T = LocalAppColors.current
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(T.coral.copy(alpha = 0.12f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icono()
        Spacer(Modifier.width(6.dp))
        Text(
            texto,
            fontFamily = Outfit,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = T.textPrimary,
        )
    }
}
