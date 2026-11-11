package com.injuv.voluntariado.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.injuv.voluntariado.components.EstadoCarga
import com.injuv.voluntariado.components.EstadoPostulacionChip
import com.injuv.voluntariado.components.OportunidadImagen
import com.injuv.voluntariado.components.rememberCarga
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.data.api.ApiException
import com.injuv.voluntariado.data.api.MiPostulacion
import com.injuv.voluntariado.data.api.Oportunidad
import com.injuv.voluntariado.data.api.SessionManager
import com.injuv.voluntariado.data.api.formatearFecha
import com.injuv.voluntariado.data.api.formatearPeriodo
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk
import kotlinx.coroutines.launch

/** Detalle de una oportunidad (GET /oportunidades/{id}) con el botón real de postular. */
@Composable
fun ApplicationDetailScreen(oportunidadId: Int, onBack: () -> Unit) {
    val T = LocalAppColors.current
    val carga = rememberCarga(oportunidadId) { ApiClient.call { oportunidad(oportunidadId) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase),
    ) {
        if (carga.datos == null) {
            IconButton(onClick = onBack, modifier = Modifier.padding(8.dp)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = T.textPrimary)
            }
        }
        EstadoCarga(carga) { op ->
            DetalleOportunidad(op, onBack = onBack, onPostulado = carga::recargar)
        }
    }
}

@Composable
private fun DetalleOportunidad(opportunity: Oportunidad, onBack: () -> Unit, onPostulado: () -> Unit) {
    val T = LocalAppColors.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val usuario by SessionManager.user.collectAsState()
    var confirmando by remember { mutableStateOf(false) }
    var postulando by remember { mutableStateOf(false) }
    var miPostulacion by remember(opportunity.id) { mutableStateOf<MiPostulacion?>(opportunity.mi_postulacion) }

    fun enviarPostulacion() {
        postulando = true
        scope.launch {
            try {
                val r = ApiClient.call { this.postular(opportunity.id) }
                miPostulacion = MiPostulacion(r.id, r.estado)
                Toast.makeText(context, "¡Postulación enviada! Te avisaremos cuando la organización la revise.", Toast.LENGTH_LONG).show()
                onPostulado()
            } catch (e: ApiException) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            } finally {
                postulando = false
            }
        }
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Box {
            OportunidadImagen(
                oportunidad = opportunity,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, T.bgBase))),
            )
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.35f)),
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Row {
                listOfNotNull(opportunity.tipo_de_voluntariado.ifBlank { null }, if (opportunity.cerrada) "Cerrada" else "Abierta").forEach { tag ->
                    Text(
                        tag,
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        color = T.coral,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(T.coral.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                opportunity.titulo,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = T.textPrimary,
            )
            Text(opportunity.organizacion_nombre, fontFamily = Outfit, fontSize = 14.sp, color = T.textMuted)

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(T.bgCardGradient))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                DetailStat(Icons.Filled.LocationOn, opportunity.comuna.ifBlank { opportunity.region.ifBlank { "Chile" } }, "Lugar")
                DetailStat(Icons.Filled.Schedule, opportunity.horas_voluntariado?.let { "${it}h" } ?: "—", "Duración")
                DetailStat(Icons.Filled.People, opportunity.cupo_maximo?.toString() ?: "Abierto", "Cupos")
            }

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = T.textMuted, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    formatearPeriodo(opportunity.fecha_inicio_voluntariado, opportunity.fecha_fin_voluntariado),
                    fontFamily = Outfit,
                    fontSize = 13.sp,
                    color = T.textSecondary,
                )
            }

            Spacer(Modifier.height(20.dp))

            Text("Descripción", fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = T.textPrimary)
            Spacer(Modifier.height(6.dp))
            Text(opportunity.descripcion, fontFamily = Outfit, fontSize = 13.sp, lineHeight = 20.sp, color = T.textSecondary)

            val responsable = listOf(opportunity.responsable_nombre, opportunity.responsable_apellido).filter { it.isNotBlank() }.joinToString(" ")
            if (responsable.isNotBlank() || opportunity.responsable_email.isNotBlank()) {
                Spacer(Modifier.height(20.dp))
                Text("Responsable", fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = T.textPrimary)
                Spacer(Modifier.height(6.dp))
                if (responsable.isNotBlank()) Text(responsable, fontFamily = Outfit, fontSize = 13.sp, color = T.textSecondary)
                if (opportunity.responsable_email.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            runCatching { context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${opportunity.responsable_email}"))) }
                        },
                    ) {
                        Icon(Icons.Filled.Email, contentDescription = null, tint = T.coral, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(opportunity.responsable_email, fontFamily = Outfit, fontSize = 13.sp, color = T.coral)
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            val esVoluntario = usuario?.rol == "user"
            when {
                miPostulacion != null -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Ya postulaste · ", fontFamily = Outfit, fontSize = 13.sp, color = T.textSecondary)
                        EstadoPostulacionChip(miPostulacion!!.estado)
                    }
                    Text(
                        "Puedes seguir tu postulación en tu perfil.",
                        fontFamily = Outfit,
                        fontSize = 11.sp,
                        color = T.textMuted,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
                opportunity.cerrada -> AvisoSinPostular("Esta convocatoria está cerrada y ya no recibe postulaciones.")
                !esVoluntario -> AvisoSinPostular("Solo las cuentas de voluntario pueden postular.")
                else -> {
                    Button(
                        onClick = { confirmando = true },
                        enabled = !postulando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = T.coral),
                    ) {
                        if (postulando) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Postular ahora", fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Cierre de postulaciones: ${formatearFecha(opportunity.fecha_limite_postulacion, "sin fecha límite")}",
                fontFamily = Outfit,
                fontSize = 11.sp,
                color = T.textMuted,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }

    if (confirmando) {
        AlertDialog(
            onDismissRequest = { confirmando = false },
            title = { Text("¿Postular a este voluntariado?", fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold) },
            text = { Text("La organización verá tu perfil y tus certificados para revisar tu postulación.", fontFamily = Outfit) },
            confirmButton = {
                TextButton(onClick = {
                    confirmando = false
                    enviarPostulacion()
                }) { Text("Postular", color = T.coral) }
            },
            dismissButton = { TextButton(onClick = { confirmando = false }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun AvisoSinPostular(texto: String) {
    val T = LocalAppColors.current
    Text(
        texto,
        fontFamily = Outfit,
        fontSize = 13.sp,
        color = T.textSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(T.surface1)
            .padding(16.dp),
    )
}

@Composable
private fun DetailStat(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    val T = LocalAppColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = T.coral, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = T.textPrimary, maxLines = 1)
        Text(label, fontFamily = Outfit, fontSize = 10.sp, color = T.textMuted)
    }
}
