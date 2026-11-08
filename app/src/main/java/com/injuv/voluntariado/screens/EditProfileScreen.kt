package com.injuv.voluntariado.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.data.ChileLocations
import com.injuv.voluntariado.data.PrivacySettings
import com.injuv.voluntariado.data.Skill
import com.injuv.voluntariado.data.UserProfile
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk
import com.injuv.voluntariado.ui.theme.bannerPresets
import kotlin.math.roundToInt

private enum class EditSection(val label: String, val icon: ImageVector) {
    Photo("Foto", Icons.Outlined.PhotoCamera),
    Banner("Banner", Icons.Outlined.Palette),
    Contact("Contacto", Icons.Outlined.MailOutline),
    About("Acerca de mí", Icons.Outlined.Description),
    Skills("Habilidades", Icons.Outlined.EmojiEvents),
    Privacy("Privacidad", Icons.Outlined.Lock),
}

private fun Modifier.dashedBorder(width: Dp, color: Color, cornerRadius: Dp) = drawBehind {
    val stroke = Stroke(
        width = width.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f),
    )
    drawRoundRect(color = color, style = stroke, cornerRadius = CornerRadius(cornerRadius.toPx()))
}

@Composable
fun EditProfileScreen(
    profile: UserProfile,
    onSave: (UserProfile) -> Unit,
    onBack: () -> Unit,
) {
    val T = LocalAppColors.current
    var draft by remember { mutableStateOf(profile) }
    var section by remember { mutableStateOf(EditSection.Photo) }

    val context = LocalContext.current
    val pickPhoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            draft = draft.copy(photoUri = uri.toString())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(T.bgHeaderGradient))
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Text(
                "Editar perfil",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            EditSection.values().forEach { item ->
                val isActive = item == section
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .then(
                            if (isActive) {
                                Modifier.background(Brush.linearGradient(listOf(T.coral, T.coralDark)))
                            } else {
                                Modifier
                                    .background(T.surface1)
                                    .border(1.dp, T.border2, RoundedCornerShape(50))
                            }
                        )
                        .clickable { section = item }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        item.icon,
                        contentDescription = null,
                        tint = if (isActive) Color.White else T.textMuted,
                        modifier = Modifier.size(15.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        item.label,
                        fontFamily = SpaceGrotesk,
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (isActive) Color.White else T.textMuted,
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (section) {
                EditSection.Photo -> PhotoSection(
                    photoUri = draft.photoUri,
                    onPick = {
                        pickPhoto.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onRemove = { draft = draft.copy(photoUri = null) },
                )
                EditSection.Banner -> BannerSection(
                    selected = draft.bannerIndex,
                    onSelect = { draft = draft.copy(bannerIndex = it) },
                )
                EditSection.Contact -> ContactSection(
                    draft = draft,
                    onChange = { draft = it },
                )
                EditSection.About -> AboutSection(
                    about = draft.about,
                    onChange = { draft = draft.copy(about = it) },
                )
                EditSection.Skills -> SkillsSection(
                    skills = draft.skills,
                    onChange = { draft = draft.copy(skills = it) },
                )
                EditSection.Privacy -> PrivacySection(
                    privacy = draft.privacy,
                    onChange = { draft = draft.copy(privacy = it) },
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(T.navBg)
                .border(1.dp, T.navBorder)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text("Cancelar", fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
            Button(
                onClick = { onSave(draft) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = T.coral, contentColor = Color.White),
            ) {
                Text("Guardar", fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun SectionBody(
    title: String,
    description: String,
    content: @Composable () -> Unit,
) {
    val T = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(title, fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = T.textPrimary)
        Text(description, fontFamily = Outfit, fontSize = 13.sp, color = T.textMuted)
        content()
        Spacer(Modifier.height(8.dp))
    }
}

// ---------------------------------------------------------------------------
// Editar foto
// ---------------------------------------------------------------------------

@Composable
private fun PhotoSection(photoUri: String?, onPick: () -> Unit, onRemove: () -> Unit) {
    val T = LocalAppColors.current
    SectionBody(
        title = "Editar foto",
        description = "Cambia tu foto de perfil. La imagen se mostrará de forma circular.",
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .dashedBorder(1.5.dp, T.inputBorder, 16.dp)
                .clickable(onClick = onPick)
                .padding(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (photoUri == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.CloudUpload,
                        contentDescription = null,
                        tint = T.textSubtle,
                        modifier = Modifier.size(44.dp),
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Toca para seleccionar una imagen",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = T.blue,
                    )
                    Text(
                        "PNG, JPG hasta 10MB",
                        fontFamily = Outfit,
                        fontSize = 12.sp,
                        color = T.textMuted,
                    )
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = Uri.parse(photoUri),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Toca para cambiar la imagen",
                        fontFamily = Outfit,
                        fontSize = 12.sp,
                        color = T.textMuted,
                    )
                }
            }
        }
        if (photoUri != null) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(onClick = onRemove) {
                    Text("Quitar foto", fontFamily = SpaceGrotesk, fontSize = 13.sp, color = T.coral)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Color del banner
// ---------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BannerSection(selected: Int, onSelect: (Int) -> Unit) {
    val T = LocalAppColors.current
    val options = listOf(T.bgHeaderGradient) + bannerPresets
    SectionBody(
        title = "Color del banner",
        description = "Elige el color del encabezado de tu perfil.",
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            options.forEachIndexed { index, gradient ->
                val isActive = index == selected
                Box(
                    modifier = Modifier
                        .width(104.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(gradient))
                        .then(
                            if (isActive) Modifier.border(2.dp, T.coral, RoundedCornerShape(12.dp))
                            else Modifier.border(1.dp, T.border2, RoundedCornerShape(12.dp))
                        )
                        .clickable { onSelect(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    if (isActive) {
                        Icon(Icons.Filled.Check, contentDescription = "Seleccionado", tint = Color.White)
                    } else if (index == 0) {
                        Text(
                            "Auto",
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Información de contacto
// ---------------------------------------------------------------------------

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = LocalAppColors.current.inputBg,
    unfocusedContainerColor = LocalAppColors.current.inputBg,
    disabledContainerColor = LocalAppColors.current.inputBg,
    focusedBorderColor = LocalAppColors.current.coral,
    unfocusedBorderColor = LocalAppColors.current.inputBorder,
    disabledBorderColor = LocalAppColors.current.inputBorder,
    focusedLabelColor = LocalAppColors.current.textMuted,
    unfocusedLabelColor = LocalAppColors.current.textMuted,
    disabledLabelColor = LocalAppColors.current.textSubtle,
    focusedTextColor = LocalAppColors.current.textPrimary,
    unfocusedTextColor = LocalAppColors.current.textPrimary,
    disabledTextColor = LocalAppColors.current.textSubtle,
    cursorColor = LocalAppColors.current.coral,
)

@Composable
private fun ContactSection(draft: UserProfile, onChange: (UserProfile) -> Unit) {
    SectionBody(
        title = "Información de contacto",
        description = "Actualiza tu correo, teléfono y ubicación.",
    ) {
        OutlinedTextField(
            value = draft.email,
            onValueChange = { onChange(draft.copy(email = it)) },
            label = { Text("Correo electrónico") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = fieldColors(),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = draft.phone,
            onValueChange = { onChange(draft.copy(phone = it)) },
            label = { Text("Teléfono") },
            placeholder = { Text("Teléfono") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors = fieldColors(),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = draft.age,
            onValueChange = { onChange(draft.copy(age = it)) },
            label = { Text("Edad") },
            placeholder = { Text("Edad") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = fieldColors(),
            modifier = Modifier.fillMaxWidth(),
        )
        LocationDropdown(
            label = "Región",
            value = draft.region,
            placeholder = "Seleccione una región",
            options = ChileLocations.regions,
            enabled = true,
            onSelect = { onChange(draft.copy(region = it, city = "", commune = "")) },
        )
        LocationDropdown(
            label = "Ciudad",
            value = draft.city,
            placeholder = if (draft.region.isBlank()) "Primero seleccione una región" else "Seleccione una ciudad",
            options = ChileLocations.citiesOf(draft.region),
            enabled = draft.region.isNotBlank(),
            onSelect = { onChange(draft.copy(city = it, commune = "")) },
        )
        LocationDropdown(
            label = "Comuna",
            value = draft.commune,
            placeholder = if (draft.city.isBlank()) "Primero seleccione una ciudad" else "Seleccione una comuna",
            options = ChileLocations.communesOf(draft.region, draft.city),
            enabled = draft.city.isNotBlank(),
            onSelect = { onChange(draft.copy(commune = it)) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationDropdown(
    label: String,
    value: String,
    placeholder: String,
    options: List<String>,
    enabled: Boolean,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it },
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = fieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontFamily = Outfit, fontSize = 14.sp) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Acerca de mí
// ---------------------------------------------------------------------------

@Composable
private fun AboutSection(about: String, onChange: (String) -> Unit) {
    SectionBody(
        title = "Acerca de mí",
        description = "Cuéntales a otros usuarios quién eres y qué te motiva.",
    ) {
        OutlinedTextField(
            value = about,
            onValueChange = onChange,
            label = { Text("Descripción") },
            placeholder = { Text("Escribe una descripción sobre ti...") },
            minLines = 6,
            colors = fieldColors(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ---------------------------------------------------------------------------
// Habilidades
// ---------------------------------------------------------------------------

@Composable
private fun SkillsSection(skills: List<Skill>, onChange: (List<Skill>) -> Unit) {
    val T = LocalAppColors.current
    SectionBody(
        title = "Habilidades",
        description = "Gestiona tus habilidades y su nivel de dominio.",
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(
                onClick = { onChange(skills + Skill("", 50)) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = T.blue, contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Agregar habilidad", fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }
        skills.forEachIndexed { index, skill ->
            SkillCard(
                skill = skill,
                onNameChange = { name -> onChange(skills.toMutableList().apply { set(index, skill.copy(name = name)) }) },
                onLevelChange = { level -> onChange(skills.toMutableList().apply { set(index, skill.copy(level = level)) }) },
                onDelete = { onChange(skills.toMutableList().apply { removeAt(index) }) },
            )
        }
        if (skills.isEmpty()) {
            Text(
                "Aún no has agregado habilidades.",
                fontFamily = Outfit,
                fontSize = 13.sp,
                color = T.textMuted,
            )
        }
    }
}

@Composable
private fun SkillCard(
    skill: Skill,
    onNameChange: (String) -> Unit,
    onLevelChange: (Int) -> Unit,
    onDelete: () -> Unit,
) {
    val T = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .border(1.dp, T.border1, RoundedCornerShape(16.dp))
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = skill.name,
                onValueChange = onNameChange,
                placeholder = { Text("Nombre de la habilidad") },
                singleLine = true,
                colors = fieldColors(),
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onDelete) {
                Text("Eliminar", fontFamily = SpaceGrotesk, fontSize = 12.sp, color = T.coral)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Nivel", fontFamily = Outfit, fontSize = 13.sp, color = T.textMuted)
            Text("${skill.level}%", fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = T.blue)
        }
        Slider(
            value = skill.level.toFloat(),
            onValueChange = { onLevelChange(it.roundToInt().coerceIn(0, 100)) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = T.blue,
                activeTrackColor = T.blue,
                inactiveTrackColor = T.progressTrack,
            ),
        )
    }
}

// ---------------------------------------------------------------------------
// Privacidad
// ---------------------------------------------------------------------------

@Composable
private fun PrivacySection(privacy: PrivacySettings, onChange: (PrivacySettings) -> Unit) {
    SectionBody(
        title = "Privacidad",
        description = "Controla qué información de tu perfil es visible para otros usuarios.",
    ) {
        PrivacyGroup("Información Personal") {
            PrivacyToggle(
                title = "Horas de voluntariado",
                subtitle = "Mostrar u ocultar las horas de voluntariado (incluyendo el ícono)",
                checked = privacy.volunteerHours,
                onCheckedChange = { onChange(privacy.copy(volunteerHours = it)) },
            )
            PrivacyToggle(
                title = "Edad",
                subtitle = "Mostrar u ocultar tu edad (incluyendo el ícono)",
                checked = privacy.age,
                onCheckedChange = { onChange(privacy.copy(age = it)) },
            )
        }
        PrivacyGroup("Información de Contacto") {
            PrivacyToggle(
                title = "Correo electrónico",
                subtitle = "Controla la visibilidad de tu email",
                checked = privacy.email,
                onCheckedChange = { onChange(privacy.copy(email = it)) },
            )
            PrivacyToggle(
                title = "Teléfono",
                subtitle = "Mostrar u ocultar tu número de teléfono",
                checked = privacy.phone,
                onCheckedChange = { onChange(privacy.copy(phone = it)) },
            )
            PrivacyToggle(
                title = "Ubicación",
                subtitle = "Mostrar u ocultar tu ubicación (incluyendo el ícono)",
                checked = privacy.location,
                onCheckedChange = { onChange(privacy.copy(location = it)) },
            )
        }
        PrivacyGroup("Contenido del Perfil") {
            PrivacyToggle(
                title = "Acerca de mí",
                subtitle = "Mostrar u ocultar tu descripción personal",
                checked = privacy.aboutMe,
                onCheckedChange = { onChange(privacy.copy(aboutMe = it)) },
            )
            PrivacyToggle(
                title = "Experiencia de Voluntariados",
                subtitle = "Mostrar u ocultar tu historial de voluntariados",
                checked = privacy.volunteerExperience,
                onCheckedChange = { onChange(privacy.copy(volunteerExperience = it)) },
            )
            PrivacyToggle(
                title = "Habilidades",
                subtitle = "Mostrar u ocultar tus habilidades",
                checked = privacy.skills,
                onCheckedChange = { onChange(privacy.copy(skills = it)) },
            )
            PrivacyToggle(
                title = "Reseñas y Referencias",
                subtitle = "Mostrar u ocultar reseñas sobre tu trabajo",
                checked = privacy.reviews,
                onCheckedChange = { onChange(privacy.copy(reviews = it)) },
            )
        }
    }
}

@Composable
private fun PrivacyGroup(title: String, content: @Composable () -> Unit) {
    val T = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .border(1.dp, T.border1, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Text(
            title,
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = T.textPrimary,
        )
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun PrivacyToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val T = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = T.textPrimary)
            Text(subtitle, fontFamily = Outfit, fontSize = 11.sp, color = T.textMuted)
        }
        Spacer(Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = T.blue,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = T.textSubtle,
            ),
        )
    }
}
