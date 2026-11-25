package com.injuv.voluntariado.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.components.EstadoCarga
import com.injuv.voluntariado.components.EstadoPostulacionChip
import com.injuv.voluntariado.components.MenuIconButton
import com.injuv.voluntariado.components.NotificationIconButton
import com.injuv.voluntariado.components.Vacio
import com.injuv.voluntariado.components.descargarArchivo
import com.injuv.voluntariado.components.rememberCarga
import com.injuv.voluntariado.data.UserProfile
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.data.api.ApiException
import com.injuv.voluntariado.data.api.ConfirmacionBody
import com.injuv.voluntariado.data.api.PostulacionUsuario
import com.injuv.voluntariado.data.api.ResenaUsuarioBody
import com.injuv.voluntariado.data.api.SessionManager
import com.injuv.voluntariado.data.api.anioDe
import com.injuv.voluntariado.data.api.formatearFecha
import com.injuv.voluntariado.data.api.formatearHoras
import com.injuv.voluntariado.data.api.pendienteCalificarOrg
import com.injuv.voluntariado.components.CabeceraPerfil
import com.injuv.voluntariado.components.Avatar
import com.injuv.voluntariado.components.Tono
import com.injuv.voluntariado.components.DatoPerfil
import com.injuv.voluntariado.components.Etiqueta
import com.injuv.voluntariado.components.BotonCirculo
import com.injuv.voluntariado.components.Tarjeta
import com.injuv.voluntariado.components.TituloSeccion
import com.injuv.voluntariado.components.BotonPrimario
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.InstrumentSans
import com.injuv.voluntariado.ui.theme.Bricolage
import kotlinx.coroutines.launch

private enum class ProfileTab { EnCurso, Postuladas, Terminadas }

private val ESTADOS_EN_PROCESO = setOf("Pendiente de revisión", "Pre-seleccionado", "Etapa de entrevista", "En lista de espera")

/** Seleccionado en un voluntariado que aún no termina. */
private fun PostulacionUsuario.enCurso() = estado == "Seleccionado" && !voluntariado_terminado && !rechazo_participacion
private fun PostulacionUsuario.enProceso() = estado in ESTADOS_EN_PROCESO && !voluntariado_terminado

/** El usuario retiró su postulación o declinó participar: no se muestra en Terminadas. */
private fun PostulacionUsuario.retirada() = estado == "Declinada por usuario" || rechazo_participacion

@Composable
fun ProfileScreen(
    onShowAlert: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleMenu: () -> Unit,
    onOpenOportunidad: (Int) -> Unit,
    onEditProfile: () -> Unit = {},
    profile: UserProfile = UserProfile(),
    onOpenForum: (Int) -> Unit = {},
) {
    val T = LocalAppColors.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val usuario by SessionManager.user.collectAsState()
    var activeTab by remember { mutableStateOf(ProfileTab.EnCurso) }
    val carga = rememberCarga { ApiClient.call { misPostulaciones() } }
    val todas = carga.datos.orEmpty()
    val enCurso = todas.filter { it.enCurso() }
    val postuladas = todas.filter { it.enProceso() }
    val terminadas = todas.filter { !it.enCurso() && !it.enProceso() && !it.retirada() }
    var retirando by remember { mutableStateOf<PostulacionUsuario?>(null) }
    // Voluntariados terminados donde la ONG ya evaluó y falta la reseña del voluntario.
    val pendientesCalificar = todas.filter { it.pendienteCalificarOrg() }
    var eligiendoCalificar by remember { mutableStateOf(false) }
    var calificando by remember { mutableStateOf<PostulacionUsuario?>(null) }

    fun accion(mensajeOk: String, llamada: suspend () -> Unit) {
        scope.launch {
            try {
                llamada()
                Toast.makeText(context, mensajeOk, Toast.LENGTH_SHORT).show()
                carga.recargar()
            } catch (e: ApiException) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    val displayName = usuario?.nombreCompleto?.ifBlank { null } ?: profile.name.ifBlank { "Voluntario" }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            val u = usuario
            CabeceraPerfil(
                nombre = displayName,
                onMenu = onToggleMenu,
                onAlertas = onShowAlert,
                accionesExtra = { BotonCirculo(Icons.Filled.Settings, "Configuración", onOpenSettings) },
                avatar = {
                    Avatar(
                        displayName,
                        url = profile.photoUri ?: u?.takeIf { it.tiene_foto }?.let { ApiClient.fotoUsuario(it.id, it.foto_version) },
                        size = 70.dp,
                    )
                },
                rol = "Voluntario/a",
                detalle = listOfNotNull(
                    anioDe(u?.created_at).ifBlank { null }?.let { "En la red desde $it" },
                    "${formatearHoras(u?.horas_voluntariado ?: 0.0)} horas de voluntariado",
                    if (profile.privacy.age && profile.age.isNotBlank()) "${profile.age} años" else null,
                ).joinToString(" · "),
                debajoRol = {
                    // Insignia de la organización a la que pertenece (si es admin o miembro de una)
                    u?.organizacion?.let { org ->
                        Spacer(Modifier.height(6.dp))
                        Etiqueta(
                            "${org.nombre ?: "Organización"} · ${if (org.rol == "miembro") "Miembro" else "Administrador"}",
                            Tono.Ambar,
                            icono = Icons.Filled.Business,
                        )
                    }
                },
                datos = listOf(
                    DatoPerfil("${enCurso.size}", "En curso"),
                    DatoPerfil("${postuladas.size}", "Postuladas"),
                    DatoPerfil("${terminadas.size}", "Terminadas"),
                ),
                accion = "Editar perfil",
                iconoAccion = Icons.Filled.Edit,
                onAccion = onEditProfile,
            )
        }

        val locationText = listOf(profile.commune, profile.city, profile.region)
            .filter { it.isNotBlank() }
            .joinToString(", ")
        val showEmail = profile.privacy.email && profile.email.isNotBlank()
        val showPhone = profile.privacy.phone && profile.phone.isNotBlank()
        val showLocation = profile.privacy.location && locationText.isNotBlank()

        if (showEmail || showPhone || showLocation) {
            item {
                ProfileInfoCard("Información de Contacto") {
                    if (showEmail) ContactRow(Icons.Filled.Email, profile.email)
                    if (showPhone) ContactRow(Icons.Filled.Phone, profile.phone)
                    if (showLocation) ContactRow(Icons.Filled.LocationOn, locationText)
                }
            }
        }

        if (profile.privacy.aboutMe && profile.about.isNotBlank()) {
            item {
                ProfileInfoCard("Acerca de mí") {
                    Text(profile.about, fontFamily = InstrumentSans, fontSize = 13.sp, color = T.textSecondary)
                }
            }
        }

        if (profile.privacy.skills && profile.skills.any { it.name.isNotBlank() }) {
            item {
                ProfileInfoCard("Habilidades") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        profile.skills.filter { it.name.isNotBlank() }.forEach { skill ->
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(skill.name, fontFamily = InstrumentSans, fontSize = 12.sp, color = T.textSecondary)
                                    Text("${skill.level}%", fontFamily = InstrumentSans, fontSize = 12.sp, color = T.textMuted)
                                }
                                Spacer(Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { skill.level / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(50)),
                                    color = T.blue,
                                    trackColor = T.progressTrack,
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            TituloSeccion("Mis voluntariados", modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(T.surface1)
                    .border(1.5.dp, T.border1, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                ProfileTabChip("En curso", enCurso.size, isSelected = activeTab == ProfileTab.EnCurso, onClick = { activeTab = ProfileTab.EnCurso }, modifier = Modifier.weight(1f))
                ProfileTabChip("Postuladas", postuladas.size, isSelected = activeTab == ProfileTab.Postuladas, onClick = { activeTab = ProfileTab.Postuladas }, modifier = Modifier.weight(1f))
                ProfileTabChip("Terminadas", terminadas.size, isSelected = activeTab == ProfileTab.Terminadas, onClick = { activeTab = ProfileTab.Terminadas }, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
        }

        if (!profile.privacy.volunteerExperience) {
            item {
                Text(
                    "Tu historial de voluntariados está oculto.",
                    fontFamily = InstrumentSans,
                    fontSize = 13.sp,
                    color = T.textMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                )
            }
        } else if (carga.datos == null) {
            item { EstadoCarga(carga) {} }
        } else {
            val lista = when (activeTab) {
                ProfileTab.EnCurso -> enCurso
                ProfileTab.Postuladas -> postuladas
                ProfileTab.Terminadas -> terminadas
            }
            if (lista.isEmpty()) {
                item {
                    Vacio(
                        when (activeTab) {
                            ProfileTab.EnCurso -> "No tienes voluntariados en curso"
                            ProfileTab.Postuladas -> "No tienes postulaciones en revisión"
                            ProfileTab.Terminadas -> "Aún no terminas ningún voluntariado"
                        },
                        if (activeTab == ProfileTab.Terminadas) null else "Explora las oportunidades en la pestaña Postular.",
                    )
                }
            }
            items(lista, key = { it.id }) { p ->
                val mod = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                when (activeTab) {
                    ProfileTab.EnCurso -> ActiveProgramCard(
                        p,
                        onAbrir = { onOpenOportunidad(p.oportunidad_id) },
                        onForo = { onOpenForum(p.oportunidad_id) },
                        onConfirmar = { confirmado ->
                            accion(if (confirmado) "Participación confirmada" else "Avisamos a la organización que no participarás") {
                                ApiClient.call { confirmar(p.id, ConfirmacionBody(if (confirmado) "Confirmado" else "No confirmado")) }
                            }
                        },
                        modifier = mod,
                    )
                    ProfileTab.Postuladas -> AppliedProgramCard(
                        p,
                        onAbrir = { onOpenOportunidad(p.oportunidad_id) },
                        onRetirar = { retirando = p },
                        modifier = mod,
                    )
                    ProfileTab.Terminadas -> HistoryProgramCard(
                        p,
                        onDescargar = {
                            descargarArchivo(context, ApiClient.certificadoPostulacion(p.id), "certificado_${p.oportunidad_titulo.take(40)}.pdf")
                        },
                        modifier = mod,
                    )
                }
            }
        }

        if (profile.privacy.reviews) {
            val resenas = todas.filter { it.resena_org_publica && (it.resena_org.isNotBlank() || it.calificacion_org != null) }
            item {
                ProfileInfoCard(
                    title = "Reseñas y Referencias",
                    icon = Icons.Filled.Star,
                    iconTint = T.yellow,
                    trailing = if (pendientesCalificar.isNotEmpty()) {
                        {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(T.action)
                                    .clickable {
                                        if (pendientesCalificar.size == 1) calificando = pendientesCalificar.first()
                                        else eligiendoCalificar = true
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                            ) {
                                Text("Calificar", fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color.White)
                            }
                        }
                    } else {
                        null
                    },
                ) {
                    if (resenas.isEmpty()) {
                        Text(
                            "Aún no tienes reseñas públicas de organizaciones.",
                            fontFamily = InstrumentSans,
                            fontSize = 13.sp,
                            color = T.textMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        resenas.forEachIndexed { i, p ->
                            if (i > 0) Spacer(Modifier.height(10.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(T.bgBase)
                                    .padding(12.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        p.organizacion_nombre,
                                        fontFamily = InstrumentSans,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = T.textPrimary,
                                        modifier = Modifier.weight(1f),
                                    )
                                    p.calificacion_org?.let { nota -> EstrellasResena(nota) }
                                }
                                Text(p.oportunidad_titulo, fontFamily = InstrumentSans, fontSize = 11.sp, color = T.textMuted)
                                if (p.resena_org.isNotBlank()) {
                                    Spacer(Modifier.height(6.dp))
                                    Text(p.resena_org, fontFamily = InstrumentSans, fontSize = 13.sp, color = T.textSecondary)
                                }
                                if (p.resena_usuario.isNotBlank() || p.calificacion_usuario != null) {
                                    Spacer(Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Tu evaluación", fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = T.textMuted)
                                        Spacer(Modifier.width(8.dp))
                                        p.calificacion_usuario?.let { EstrellasResena(it) }
                                    }
                                    if (p.resena_usuario.isNotBlank()) {
                                        Text(p.resena_usuario, fontFamily = InstrumentSans, fontSize = 12.sp, color = T.textSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
        }
    }

    // Selector de voluntariado cuando hay más de uno pendiente de calificar.
    if (eligiendoCalificar) {
        AlertDialog(
            onDismissRequest = { eligiendoCalificar = false },
            title = { Text("¿A qué organización quieres calificar?", fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold) },
            text = {
                Column {
                    pendientesCalificar.forEach { p ->
                        Text(
                            "${p.organizacion_nombre} · ${p.oportunidad_titulo}",
                            fontFamily = InstrumentSans,
                            fontSize = 14.sp,
                            color = T.textPrimary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    eligiendoCalificar = false
                                    calificando = p
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                        )
                    }
                }
            },
            confirmButton = { TextButton(onClick = { eligiendoCalificar = false }) { Text("Cancelar") } },
        )
    }

    calificando?.let { p ->
        CalificarOrgDialog(
            postulacion = p,
            onDismiss = { calificando = null },
            onEnviar = { texto, nota, publica ->
                calificando = null
                scope.launch {
                    try {
                        ApiClient.call { resenaUsuario(p.id, ResenaUsuarioBody(texto, nota, publica)) }
                        Toast.makeText(context, "Evaluación enviada", Toast.LENGTH_SHORT).show()
                        carga.recargar()
                    } catch (e: ApiException) {
                        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            },
        )
    }

    retirando?.let { p ->
        AlertDialog(
            onDismissRequest = { retirando = null },
            title = { Text("¿Retirar tu postulación?", fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold) },
            text = { Text("Dejarás de participar en el proceso de \"${p.oportunidad_titulo}\". Esta acción no se puede deshacer.", fontFamily = InstrumentSans) },
            confirmButton = {
                TextButton(onClick = {
                    retirando = null
                    accion("Postulación retirada") { ApiClient.call { declinar(p.id) } }
                }) { Text("Retirar", color = T.coral) }
            },
            dismissButton = { TextButton(onClick = { retirando = null }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun ProfileInfoCard(
    title: String,
    icon: ImageVector? = null,
    iconTint: Color? = null,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val T = LocalAppColors.current
    Tarjeta(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 7.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = iconTint ?: T.action, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                }
                Text(title, fontFamily = Bricolage, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = T.textPrimary, modifier = Modifier.weight(1f))
                trailing?.invoke()
            }
            content()
        }
    }
}

@Composable
private fun ContactRow(icon: ImageVector, value: String) {
    val T = LocalAppColors.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(T.action.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = if (T.isDark) T.blue else T.action, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(value, fontFamily = InstrumentSans, fontSize = 14.sp, color = T.textSecondary)
    }
}

/** Pestaña segmentada de la web (Tabs): se entinta al activarse y muestra el conteo. */
@Composable
private fun ProfileTabChip(label: String, count: Int, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Row(
        modifier = modifier
            .heightIn(min = 44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) T.ink else Color.Transparent)
            .selectable(selected = isSelected, role = Role.Tab, onClick = onClick)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            fontFamily = InstrumentSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = if (isSelected) T.onInk else T.textMuted,
            maxLines = 1,
        )
        Spacer(Modifier.width(5.dp))
        Text(
            "$count",
            fontFamily = InstrumentSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            color = if (isSelected) T.onInk else T.textMuted,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background((if (isSelected) T.onInk else T.textMuted).copy(alpha = 0.16f))
                .padding(horizontal = 5.dp),
        )
    }
}

/** Seleccionado y en curso: confirmar o avisar que no participará. */
@Composable
private fun ActiveProgramCard(
    p: PostulacionUsuario,
    onAbrir: () -> Unit,
    onForo: () -> Unit,
    onConfirmar: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val T = LocalAppColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(T.card)
            .border(1.dp, T.border1, RoundedCornerShape(16.dp))
            .clickable(onClick = onAbrir)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(p.oportunidad_titulo, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
                Text(p.organizacion_nombre, fontFamily = InstrumentSans, fontSize = 12.sp, color = T.textMuted)
            }
            EstadoPostulacionChip(p.estado)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "${formatearHoras(p.horas_voluntariado)} horas registradas · Participación: ${p.estado_confirmacion.lowercase()}",
            fontFamily = InstrumentSans,
            fontSize = 11.sp,
            color = T.textMuted,
        )
        TextButton(onClick = onForo, contentPadding = PaddingValues(0.dp)) {
            Text("Ver foro de anuncios →", fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = if (T.isDark) T.blue else T.action)
        }
        if (p.estado_confirmacion == "Pendiente") {
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                BotonPrimario("Confirmar participación", onClick = { onConfirmar(true) }, alto = 44.dp, modifier = Modifier.weight(1f))
                TextButton(onClick = { onConfirmar(false) }) {
                    Text("No puedo", fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = T.textMuted)
                }
            }
        }
    }
}

/** En revisión: estado actual y opción de retirarse. */
@Composable
private fun AppliedProgramCard(
    p: PostulacionUsuario,
    onAbrir: () -> Unit,
    onRetirar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val T = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(T.card)
            .border(1.dp, T.border1, RoundedCornerShape(16.dp))
            .clickable(onClick = onAbrir)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.HourglassTop, contentDescription = null, tint = T.yellow, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(p.oportunidad_titulo, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
            Text(p.organizacion_nombre, fontFamily = InstrumentSans, fontSize = 12.sp, color = T.textMuted)
            Text("Postulaste el ${formatearFecha(p.created_at)}", fontFamily = InstrumentSans, fontSize = 11.sp, color = T.textSubtle)
            TextButton(onClick = onRetirar, contentPadding = PaddingValues(0.dp)) {
                Text("Retirar postulación", fontFamily = InstrumentSans, fontSize = 11.sp, color = T.coral)
            }
        }
        EstadoPostulacionChip(p.estado)
    }
}

/** Terminadas, no seleccionadas o retiradas; descarga del certificado si la organización lo emitió. */
@Composable
private fun HistoryProgramCard(p: PostulacionUsuario, onDescargar: () -> Unit, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(T.card)
            .border(1.dp, T.border1, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(p.oportunidad_titulo, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
            Text(
                listOf(p.organizacion_nombre, anioDe(p.created_at)).filter { it.isNotBlank() }.joinToString(" · "),
                fontFamily = InstrumentSans,
                fontSize = 12.sp,
                color = T.textMuted,
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                EstadoPostulacionChip(if (p.estado == "Seleccionado" && p.voluntariado_terminado) "Finalizado" else p.estado)
                Spacer(Modifier.width(8.dp))
                if (p.horas_voluntariado > 0) Text("${formatearHoras(p.horas_voluntariado)}h", fontFamily = InstrumentSans, fontSize = 12.sp, color = T.textMuted)
            }
        }
        if (p.tiene_certificado) {
            IconButton(onClick = onDescargar) {
                Icon(Icons.Filled.Download, contentDescription = "Descargar certificado", tint = T.green)
            }
        }
    }
}

/** Fila de 5 estrellas con la nota que la organización le dio al voluntario. */
@Composable
private fun EstrellasResena(nota: Double) {
    val T = LocalAppColors.current
    Row {
        repeat(5) { i ->
            Icon(
                Icons.Filled.Star,
                contentDescription = null,
                tint = if (i < nota.toInt()) T.yellow else T.textSubtle.copy(alpha = 0.4f),
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

/** Diálogo para evaluar a la organización al terminar el voluntariado (nota + texto + visibilidad). */
@Composable
private fun CalificarOrgDialog(
    postulacion: PostulacionUsuario,
    onDismiss: () -> Unit,
    onEnviar: (texto: String, nota: Double, publica: Boolean) -> Unit,
) {
    val T = LocalAppColors.current
    var nota by remember { mutableStateOf(0) }
    var texto by remember { mutableStateOf("") }
    var publica by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Califica a la organización", fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold)
                Text(postulacion.organizacion_nombre, fontFamily = InstrumentSans, fontSize = 12.sp, color = T.textMuted)
            }
        },
        text = {
            Column {
                Row {
                    repeat(5) { i ->
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "${i + 1} estrellas",
                            tint = if (i < nota) T.yellow else T.textSubtle.copy(alpha = 0.4f),
                            modifier = Modifier
                                .size(34.dp)
                                .clickable { nota = i + 1 },
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it.take(2000) },
                    label = { Text("Tu reseña") },
                    placeholder = { Text("Cuenta cómo fue tu experiencia") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Reseña pública", fontFamily = InstrumentSans, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = T.textPrimary)
                        Text("Visible en el perfil de la organización", fontFamily = InstrumentSans, fontSize = 11.sp, color = T.textMuted)
                    }
                    Switch(
                        checked = publica,
                        onCheckedChange = { publica = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = T.action),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = nota > 0 && texto.trim().length >= 3,
                onClick = { onEnviar(texto.trim(), nota.toDouble(), publica) },
            ) { Text("Enviar", color = T.coral) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}
