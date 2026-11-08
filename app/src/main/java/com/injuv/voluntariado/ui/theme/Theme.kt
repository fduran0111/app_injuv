package com.injuv.voluntariado.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.injuv.voluntariado.R

// Tipografías del diseño original: Space Grotesk (títulos/UI) y Outfit (cuerpo de texto).
// Descarga los .ttf desde Google Fonts y colócalos en res/font (ver README_FUENTES.txt).
val SpaceGrotesk = FontFamily(
    Font(R.font.space_grotesk_regular, FontWeight.Normal),
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.space_grotesk_semibold, FontWeight.SemiBold),
    Font(R.font.space_grotesk_bold, FontWeight.Bold),
)

val Outfit = FontFamily(
    Font(R.font.outfit_light, FontWeight.Light),
    Font(R.font.outfit_regular, FontWeight.Normal),
    Font(R.font.outfit_medium, FontWeight.Medium),
    Font(R.font.outfit_semibold, FontWeight.SemiBold),
)

data class AppColors(
    val isDark: Boolean,
    val bgBase: Color,
    val bgCardGradient: List<Color>,
    val bgHeaderGradient: List<Color>,
    val surface1: Color,
    val surface2: Color,
    val border1: Color,
    val border2: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val textSubtle: Color,
    val navBg: Color,
    val navBorder: Color,
    val navInactive: Color,
    val coral: Color,
    val coralDark: Color,
    val yellow: Color,
    val blue: Color,
    val green: Color,
    val progressTrack: Color,
    val inputBg: Color,
    val inputBorder: Color,
)

fun darkAppColors() = AppColors(
    isDark = true,
    bgBase = Color(0xFF060D1F),
    bgCardGradient = listOf(Color(0xFF112240), Color(0xFF0E1C38)),
    bgHeaderGradient = listOf(Color(0xFF0A1628), Color(0xFF1A3460)),
    surface1 = Color(0x0AFFFFFF),
    surface2 = Color(0x0FFFFFFF),
    border1 = Color(0x12FFFFFF),
    border2 = Color(0x1FFFFFFF),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFDDE4F5),
    textMuted = Color(0xFF8896B3),
    textSubtle = Color(0xFF4A5878),
    navBg = Color(0xFF0A1628),
    navBorder = Color(0x0DFFFFFF),
    navInactive = Color(0xFF4A5878),
    coral = Color(0xFFFF4D6D),
    coralDark = Color(0xFFE03555),
    yellow = Color(0xFFFFD60A),
    blue = Color(0xFF60A5FA),
    green = Color(0xFF34D399),
    progressTrack = Color(0x14FFFFFF),
    inputBg = Color(0x0FFFFFFF),
    inputBorder = Color(0x1AFFFFFF),
)

fun lightAppColors() = AppColors(
    isDark = false,
    bgBase = Color(0xFFF8FAFF),
    bgCardGradient = listOf(Color(0xFFFFFFFF), Color(0xFFF5F7FF)),
    bgHeaderGradient = listOf(Color(0xFF1E3A8A), Color(0xFF2A5298)),
    surface1 = Color(0x0A0A1628),
    surface2 = Color(0x0F0A1628),
    border1 = Color(0x170A1628),
    border2 = Color(0x260A1628),
    textPrimary = Color(0xFF060D1F),
    textSecondary = Color(0xFF1A2E5A),
    textMuted = Color(0xFF3D5080),
    textSubtle = Color(0xFF7A8FBE),
    navBg = Color(0xFFFFFFFF),
    navBorder = Color(0x140A1628),
    navInactive = Color(0xFF7A8FBE),
    coral = Color(0xFFFF4D6D),
    coralDark = Color(0xFFE03555),
    yellow = Color(0xFFD4A800),
    blue = Color(0xFF2563EB),
    green = Color(0xFF059669),
    progressTrack = Color(0x1A0A1628),
    inputBg = Color(0x0D0A1628),
    inputBorder = Color(0x1F0A1628),
)

// Gradientes predefinidos para el banner del perfil (índice 0 = gradiente del tema).
val bannerPresets: List<List<Color>> = listOf(
    listOf(Color(0xFFFF4D6D), Color(0xFFE03555)),
    listOf(Color(0xFF059669), Color(0xFF34D399)),
    listOf(Color(0xFF7C3AED), Color(0xFFA78BFA)),
    listOf(Color(0xFFD97706), Color(0xFFFBBF24)),
    listOf(Color(0xFF0F172A), Color(0xFF334155)),
    listOf(Color(0xFFBE185D), Color(0xFFF472B6)),
)

val LocalAppColors = compositionLocalOf { darkAppColors() }

val LocalOnThemeChange = staticCompositionLocalOf<(Boolean) -> Unit> { {} }

@Composable
fun ProvideAppTheme(
    isDark: Boolean,
    onThemeChange: (Boolean) -> Unit = {},
    content: @Composable () -> Unit,
) {
    val colors = if (isDark) darkAppColors() else lightAppColors()
    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalOnThemeChange provides onThemeChange,
    ) {
        MaterialTheme(content = content)
    }
}
