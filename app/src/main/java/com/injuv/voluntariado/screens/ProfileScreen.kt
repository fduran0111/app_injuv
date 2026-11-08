package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
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
import com.injuv.voluntariado.data.ActiveProgram
import com.injuv.voluntariado.data.HistoryProgram
import com.injuv.voluntariado.data.SampleData
import com.injuv.voluntariado.data.UserProfile
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk
import com.injuv.voluntariado.ui.theme.bannerPresets

@Composable
fun ProfileScreen(
    onNavigate: (String) -> Unit = {},
    profile: UserProfile = UserProfile(),
) {
    val T = LocalAppColors.current
    var activeTab by remember { mutableStateOf("activas") }
    val displayName = profile.name.ifBlank { "Voluntario" }
    val bannerGradient = if (profile.bannerIndex in 1..bannerPresets.size) {
        bannerPresets[profile.bannerIndex - 1]
    } else {
        T.bgHeaderGradient
    }
    val showHistory = profile.privacy.volunteerExperience

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(bannerGradient))
                    .padding(20.dp),
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable { onNavigate("settings") },
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Configuración",
                        tint = Color.White,
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
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
                        if (profile.privacy.volunteerHours) {
                            ProfileStat("178h", "Acumuladas")
                        }
                        ProfileStat("6", "Programas")
                        ProfileStat("4", "Certificados")
                    }

                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.18f))
                            .clickable { onNavigate("edit_profile") }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        androidx.compose.material3.Icon(
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

        if (profile.privacy.skills && profile.skills.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(T.bgCardGradient))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        "Habilidades",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = T.textPrimary,
                    )
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

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(T.surface1),
            ) {
                ProfileTab("Activas", isSelected = activeTab == "activas" || !showHistory, modifier = Modifier
                    .weight(1f)
                    .clickable { activeTab = "activas" })
                if (showHistory) {
                    ProfileTab("Historial", isSelected = activeTab == "historial", modifier = Modifier
                        .weight(1f)
                        .clickable { activeTab = "historial" })
                }
            }
        }

        if (showHistory && activeTab == "historial") {
            items(SampleData.historyPrograms) { program ->
                HistoryProgramCard(program, modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
            }
        } else {
            items(SampleData.activePrograms) { program ->
                ActiveProgramCard(program, modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
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
                androidx.compose.material3.Icon(
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
        androidx.compose.material3.Icon(icon, contentDescription = null, tint = T.coral, modifier = Modifier.size(16.dp))
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
private fun ProfileTab(label: String, isSelected: Boolean, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) Brush.linearGradient(listOf(T.coral, T.coralDark)) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)))
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            fontFamily = SpaceGrotesk,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            fontSize = 13.sp,
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
                    androidx.compose.material3.Icon(Icons.Filled.Star, contentDescription = null, tint = T.yellow, modifier = Modifier.size(13.dp))
                }
                Spacer(Modifier.width(8.dp))
                Text("${program.hours}h", fontFamily = Outfit, fontSize = 11.sp, color = T.textMuted)
            }
        }
        if (program.certificate) {
            androidx.compose.material3.Icon(Icons.Filled.Verified, contentDescription = "Certificado", tint = T.green)
        }
    }
}
