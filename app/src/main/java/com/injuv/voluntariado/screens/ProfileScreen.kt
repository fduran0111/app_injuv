package com.injuv.voluntariado.screens

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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.components.MenuIconButton
import com.injuv.voluntariado.components.NotificationIconButton
import com.injuv.voluntariado.data.ActiveProgram
import com.injuv.voluntariado.data.AppliedProgram
import com.injuv.voluntariado.data.HistoryProgram
import com.injuv.voluntariado.data.SampleData
import com.injuv.voluntariado.data.UserProfile
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk
import com.injuv.voluntariado.ui.theme.bannerPresets

private enum class ProfileTab { EnCurso, Postuladas, Terminadas }

@Composable
fun ProfileScreen(
    onShowAlert: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleMenu: () -> Unit,
    onEditProfile: () -> Unit = {},
    profile: UserProfile = UserProfile(),
) {
    val T = LocalAppColors.current
    var activeTab by remember { mutableStateOf(ProfileTab.EnCurso) }
    val displayName = profile.name.ifBlank { "Voluntario" }
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
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(displayName, fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        Text("Voluntario desde 2024", fontFamily = Outfit, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        if (profile.privacy.age && profile.age.isNotBlank()) {
                            Text(
                                "${profile.age} años",
                                fontFamily = Outfit,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f),
                            )
                        }
                        Spacer(Modifier.height(18.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ProfileStat("${SampleData.activePrograms.size}", "En curso")
                            ProfileStat("${SampleData.appliedPrograms.size}", "Postuladas")
                            ProfileStat("${SampleData.historyPrograms.size}", "Terminadas")
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

        if (profile.privacy.volunteerExperience) {
            when (activeTab) {
                ProfileTab.EnCurso -> items(SampleData.activePrograms) { program ->
                    ActiveProgramCard(program, modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
                }
                ProfileTab.Postuladas -> items(SampleData.appliedPrograms) { program ->
                    AppliedProgramCard(program, modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
                }
                ProfileTab.Terminadas -> items(SampleData.historyPrograms) { program ->
                    HistoryProgramCard(program, modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
                }
            }
        } else {
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
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = T.textSubtle)
            }
        }
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

@Composable
private fun ActiveProgramCard(program: ActiveProgram, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .padding(16.dp),
    ) {
        Text(program.title, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
        Text(program.org, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { program.progress / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50)),
            color = T.coral,
            trackColor = T.progressTrack,
        )
        Spacer(Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${program.hours}/${program.totalHours} horas", fontFamily = Outfit, fontSize = 11.sp, color = T.textMuted)
            Text("Cierra ${program.deadline}", fontFamily = Outfit, fontSize = 11.sp, color = T.coral)
        }
    }
}

@Composable
private fun AppliedProgramCard(program: AppliedProgram, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.HourglassTop, contentDescription = null, tint = T.yellow, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(program.title, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
            Text(program.org, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
            Text("Postuló el ${program.appliedDate}", fontFamily = Outfit, fontSize = 11.sp, color = T.textSubtle)
        }
        Text(
            program.status,
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            color = T.yellow,
        )
    }
}

@Composable
private fun HistoryProgramCard(program: HistoryProgram, modifier: Modifier = Modifier) {
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
            Text(program.title, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
            Text("${program.org} · ${program.year}", fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(program.rating) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = T.yellow, modifier = Modifier.size(13.dp))
                }
                Spacer(Modifier.width(8.dp))
                Text("${program.hours}h", fontFamily = Outfit, fontSize = 11.sp, color = T.textMuted)
            }
        }
        if (program.certificate) {
            Icon(Icons.Filled.Verified, contentDescription = "Certificado", tint = T.green)
        }
    }
}
