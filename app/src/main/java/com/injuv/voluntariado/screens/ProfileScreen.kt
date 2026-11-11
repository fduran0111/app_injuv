package com.injuv.voluntariado.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import com.injuv.voluntariado.data.api.SessionManager
import com.injuv.voluntariado.data.api.anioDe
import com.injuv.voluntariado.data.api.formatearFecha
import com.injuv.voluntariado.data.api.formatearHoras
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk
import com.injuv.voluntariado.ui.theme.bannerPresets
import kotlinx.coroutines.launch

private enum class ProfileTab { EnCurso, Postuladas, Terminadas }

private val ESTADOS_EN_PROCESO = setOf("Pendiente de revisión", "Pre-seleccionado", "Etapa de entrevista", "En lista de espera")

/** Seleccionado en un voluntariado que aún no termina. */
private fun PostulacionUsuario.enCurso() = estado == "Seleccionado" && !voluntariado_terminado && !rechazo_participacion
private fun PostulacionUsuario.enProceso() = estado in ESTADOS_EN_PROCESO && !voluntariado_terminado

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
    val terminadas = todas.filter { !it.enCurso() && !it.enProceso() }
    var retirando by remember { mutableStateOf<PostulacionUsuario?>(null) }

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
    val bannerGradient = if (profile.bannerIndex in 1..bannerPresets.size) {
        bannerPresets[profile.bannerIndex - 1]
    } else {
        T.bgHeaderGradient
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(bannerGradient))
                    .padding(horizontal = 12.dp, vertical = 16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    MenuIconButton(onClick = onToggleMenu)
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        val u = usuario
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (profile.photoUri != null) {
                                AsyncImage(
                                    model = android.net.Uri.parse(profile.photoUri),
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                )
                            } else {
                                Text(
                                    displayName.first().uppercase(),
                                    fontFamily = SpaceGrotesk,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    color = Color.White,
                                )
                                if (u?.tiene_foto == true) {
                                    AsyncImage(
                                        model = ApiClient.fotoUsuario(u.id, u.foto_version),
                                        contentDescription = "Foto de perfil",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.matchParentSize(),
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(displayName, fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        Text(
                            listOfNotNull(
                                anioDe(u?.created_at).ifBlank { null }?.let { "Voluntario/a desde $it" },
                                "${formatearHoras(u?.horas_voluntariado ?: 0.0)} horas",
                            ).joinToString(" · "),
                            fontFamily = Outfit,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                        )
                        if (profile.privacy.age && profile.age.isNotBlank()) {
                            Text(
                                "${profile.age} años",
                                fontFamily = Outfit,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f),
                            )
                        }
                        // Insignia de la organización a la que pertenece (si es admin o miembro de una)
                        u?.organizacion?.let { org ->
                            Spacer(Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFFFFD60A).copy(alpha = 0.22f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                            ) {
                                Icon(Icons.Filled.Business, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    "${org.nombre ?: "Organización"} · ${if (org.rol == "miembro") "Miembro" else "Administrador"}",
                                    fontFamily = Outfit,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                )
                            }
                        }
                        Spacer(Modifier.height(18.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ProfileStat("${enCurso.size}", "En curso")
                            ProfileStat("${postuladas.size}", "Postuladas")
                            ProfileStat("${terminadas.size}", "Terminadas")
                        }
                        Spacer(Modifier.height(14.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.18f))
                                .clickable(onClick = onEditProfile)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Editar perfil",
                                fontFamily = SpaceGrotesk,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = Color.White,
                            )
                        }
                    }
                    Column {
                        NotificationIconButton(onClick = onShowAlert)
                        IconButton(onClick = onOpenSettings) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Filled.Settings, contentDescription = "Configuración", tint = Color.White)
                            }
                        }
                    }
                }
            }
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
                    Text(profile.about, fontFamily = Outfit, fontSize = 13.sp, color = T.textSecondary)
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
                                    Text(skill.name, fontFamily = Outfit, fontSize = 12.sp, color = T.textSecondary)
                                    Text("${skill.level}%", fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
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
            Text(
                "Mis oportunidades",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = T.textMuted,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(50))
                    .background(T.surface1),
            ) {
                ProfileTabChip("En curso", isSelected = activeTab == ProfileTab.EnCurso, modifier = Modifier
                    .weight(1f)
                    .clickable { activeTab = ProfileTab.EnCurso })
                ProfileTabChip("Postuladas", isSelected = activeTab == ProfileTab.Postuladas, modifier = Modifier
                    .weight(1f)
                    .clickable { activeTab = ProfileTab.Postuladas })
                ProfileTabChip("Terminadas", isSelected = activeTab == ProfileTab.Terminadas, modifier = Modifier
                    .weight(1f)
                    .clickable { activeTab = ProfileTab.Terminadas })
            }
            Spacer(Modifier.height(8.dp))
        }

        if (!profile.privacy.volunteerExperience) {
            item {
                Text(
                    "Tu historial de voluntariados está oculto.",
                    fontFamily = Outfit,
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
            item {
                ProfileInfoCard(
                    title = "Reseñas y Referencias",
                    icon = Icons.Filled.Star,
                    iconTint = T.yellow,
                ) {
                    Text(
                        "Aún no tienes reseñas públicas de organizaciones.",
                        fontFamily = Outfit,
                        fontSize = 13.sp,
                        color = T.textMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(T.bgCardGradient))
                    .clickable { }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = T.coral, modifier = Modifier.size(22.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                ) {
                    Text("Centro de ayuda", fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
                    Text("Preguntas frecuentes y soporte", fontFamily = Outfit, fontSize = 11.sp, color = T.textMuted)
                }
            }
        }
    }

    retirando?.let { p ->
        AlertDialog(
            onDismissRequest = { retirando = null },
            title = { Text("¿Retirar tu postulación?", fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold) },
            text = { Text("Dejarás de participar en el proceso de \"${p.oportunidad_titulo}\". Esta acción no se puede deshacer.", fontFamily = Outfit) },
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
    content: @Composable () -> Unit,
) {
    val T = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint ?: T.coral,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(title, fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = T.textPrimary)
        }
        content()
    }
}

@Composable
private fun ContactRow(icon: ImageVector, value: String) {
    val T = LocalAppColors.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = T.coral, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(10.dp))
        Text(value, fontFamily = Outfit, fontSize = 13.sp, color = T.textSecondary)
    }
}

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFFFFD60A))
        Text(label, fontFamily = Outfit, fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
private fun ProfileTabChip(label: String, isSelected: Boolean, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) Brush.linearGradient(listOf(T.coral, T.coralDark)) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            fontFamily = SpaceGrotesk,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else T.textMuted,
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
            .background(Brush.linearGradient(T.bgCardGradient))
            .clickable(onClick = onAbrir)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(p.oportunidad_titulo, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
                Text(p.organizacion_nombre, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
            }
            EstadoPostulacionChip(p.estado)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "${formatearHoras(p.horas_voluntariado)} horas registradas · Participación: ${p.estado_confirmacion.lowercase()}",
            fontFamily = Outfit,
            fontSize = 11.sp,
            color = T.textMuted,
        )
        TextButton(onClick = onForo, contentPadding = PaddingValues(0.dp)) {
            Text("Ver foro de anuncios", fontFamily = Outfit, fontSize = 12.sp, color = T.blue)
        }
        if (p.estado_confirmacion == "Pendiente") {
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { onConfirmar(true) }, shape = RoundedCornerShape(12.dp)) {
                    Text("Confirmar participación", fontFamily = SpaceGrotesk, fontSize = 12.sp, color = T.green)
                }
                TextButton(onClick = { onConfirmar(false) }) {
                    Text("No puedo participar", fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
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
            .background(Brush.linearGradient(T.bgCardGradient))
            .clickable(onClick = onAbrir)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.HourglassTop, contentDescription = null, tint = T.yellow, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(p.oportunidad_titulo, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
            Text(p.organizacion_nombre, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
            Text("Postulaste el ${formatearFecha(p.created_at)}", fontFamily = Outfit, fontSize = 11.sp, color = T.textSubtle)
            TextButton(onClick = onRetirar, contentPadding = PaddingValues(0.dp)) {
                Text("Retirar postulación", fontFamily = Outfit, fontSize = 11.sp, color = T.coral)
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
            .background(Brush.linearGradient(T.bgCardGradient))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(p.oportunidad_titulo, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
            Text(
                listOf(p.organizacion_nombre, anioDe(p.created_at)).filter { it.isNotBlank() }.joinToString(" · "),
                fontFamily = Outfit,
                fontSize = 12.sp,
                color = T.textMuted,
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                EstadoPostulacionChip(if (p.estado == "Seleccionado" && p.voluntariado_terminado) "Finalizado" else p.estado)
                Spacer(Modifier.width(8.dp))
                if (p.horas_voluntariado > 0) Text("${formatearHoras(p.horas_voluntariado)}h", fontFamily = Outfit, fontSize = 11.sp, color = T.textMuted)
            }
        }
        if (p.tiene_certificado) {
            IconButton(onClick = onDescargar) {
                Icon(Icons.Filled.Download, contentDescription = "Descargar certificado", tint = T.green)
            }
        }
    }
}
