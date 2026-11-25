package com.injuv.voluntariado.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.core.view.WindowInsetsControllerCompat
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

/** Par fondo/texto de una etiqueta de estado, equivalente a los badges con tono de la web. */
data class BadgeTone(val bg: Color, val fg: Color)

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
    /** Acción con texto blanco encima: contraste AA (≥4.5:1) en ambos temas. */
    val action: Color,
    val actionDark: Color,
    val yellow: Color,
    val blue: Color,
    val green: Color,
    val progressTrack: Color,
    val inputBg: Color,
    val inputBorder: Color,
    val brandRed: Color,
    val ambar: Color,
    val brandSoft: Color,
    val papelStrong: Color,
    val badgeNeutral: BadgeTone,
    val badgeBlue: BadgeTone,
    val badgeGreen: BadgeTone,
    val badgeRed: BadgeTone,
    val badgeAmber: BadgeTone,
    val badgeViolet: BadgeTone,
    val badgeSlate: BadgeTone,
)

fun darkAppColors() = AppColors(
    isDark = true,
    bgBase = Color(0xFF0B1220),
    bgCardGradient = listOf(Color(0xFF141F38), Color(0xFF101A2E)),
    bgHeaderGradient = listOf(Color(0xFF0F1B2D), Color(0xFF1B2A4A)),
    surface1 = Color(0x0FFFFFFF),
    surface2 = Color(0x14FFFFFF),
    border1 = Color(0x12FFFFFF),
    border2 = Color(0x1FFFFFFF),
    textPrimary = Color(0xFFF4EFE6),
    textSecondary = Color(0xFFC9D2E8),
    textMuted = Color(0xFF9AA5C0),
    textSubtle = Color(0xFF7E8AA5),
    navBg = Color(0xFF101A2E),
    navBorder = Color(0x14FFFFFF),
    navInactive = Color(0xFF7E8AA5),
    coral = Color(0xFF7DB3FF),
    coralDark = Color(0xFF4D8DFF),
    action = Color(0xFF1D6FE0),
    actionDark = Color(0xFF1668D9),
    yellow = Color(0xFFF2A93B),
    blue = Color(0xFF8FB8FF),
    green = Color(0xFF34D399),
    progressTrack = Color(0x14FFFFFF),
    inputBg = Color(0x0FFFFFFF),
    inputBorder = Color(0x1AFFFFFF),
    brandRed = Color(0xFFFF6B6E),
    ambar = Color(0xFFF2A93B),
    brandSoft = Color(0xFF1B2A4A),
    papelStrong = Color(0xFF1A2438),
    badgeNeutral = BadgeTone(Color(0xFF1E293B), Color(0xFFCBD5E1)),
    badgeBlue = BadgeTone(Color(0xFF17293F), Color(0xFF93C5FD)),
    badgeGreen = BadgeTone(Color(0xFF022C22), Color(0xFF6EE7B7)),
    badgeRed = BadgeTone(Color(0xFF450A0A), Color(0xFFFCA5A5)),
    badgeAmber = BadgeTone(Color(0xFF451A03), Color(0xFFFCD34D)),
    badgeViolet = BadgeTone(Color(0xFF2E1065), Color(0xFFC4B5FD)),
    badgeSlate = BadgeTone(Color(0xFF1E293B), Color(0xFFCBD5E1)),
)

fun lightAppColors() = AppColors(
    isDark = false,
    bgBase = Color(0xFFF4EFE6),
    bgCardGradient = listOf(Color(0xFFFFFDF8), Color(0xFFF7F1E3)),
    bgHeaderGradient = listOf(Color(0xFF0F1B2D), Color(0xFF1B2A4A)),
    surface1 = Color(0x0F0F1B2D),
    surface2 = Color(0x140F1B2D),
    border1 = Color(0xFFD8D0C2),
    border2 = Color(0xFFC6B99F),
    textPrimary = Color(0xFF0F1B2D),
    textSecondary = Color(0xFF33405A),
    textMuted = Color(0xFF5A6A86),
    textSubtle = Color(0xFF5F6B85),
    navBg = Color(0xFFFFFDF8),
    navBorder = Color(0xFFD8D0C2),
    navInactive = Color(0xFF5A6A86),
    coral = Color(0xFF0052CC),
    coralDark = Color(0xFF003D99),
    action = Color(0xFF0052CC),
    actionDark = Color(0xFF003D99),
    yellow = Color(0xFFA04408),
    blue = Color(0xFF1D5FD1),
    green = Color(0xFF047857),
    progressTrack = Color(0x140F1B2D),
    inputBg = Color(0xFFFFFDF8),
    inputBorder = Color(0x330F1B2D),
    brandRed = Color(0xFFE3242B),
    ambar = Color(0xFFF2A93B),
    brandSoft = Color(0xFFE8F0FD),
    papelStrong = Color(0xFFEBE3D4),
    badgeNeutral = BadgeTone(Color(0xFFEBE3D4), Color(0xFF33405A)),
    badgeBlue = BadgeTone(Color(0xFFE8F0FD), Color(0xFF003D99)),
    badgeGreen = BadgeTone(Color(0xFFECFDF5), Color(0xFF065F46)),
    badgeRed = BadgeTone(Color(0xFFFEF2F2), Color(0xFF991B1B)),
    badgeAmber = BadgeTone(Color(0xFFFFFBEB), Color(0xFF92400E)),
    badgeViolet = BadgeTone(Color(0xFFF5F3FF), Color(0xFF5B21B6)),
    badgeSlate = BadgeTone(Color(0xFFF1F5F9), Color(0xFF334155)),
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

/**
 * Esquema Material 3 derivado de los tokens AppColors: los componentes M3
 * (AlertDialog, Switch, Checkbox, TextButton, progress indicators, sheets)
 * heredan colores coherentes con el tema en vez de la paleta clara por defecto.
 */
private fun AppColors.toColorScheme() = if (isDark) {
    darkColorScheme(
        primary = action,
        onPrimary = Color.White,
        primaryContainer = actionDark,
        onPrimaryContainer = Color.White,
        secondary = blue,
        onSecondary = Color(0xFF060D1F),
        background = bgBase,
        onBackground = textPrimary,
        surface = bgCardGradient.first(),
        onSurface = textPrimary,
        surfaceVariant = surface1,
        onSurfaceVariant = textSecondary,
        outline = border2,
        error = Color(0xFFFF6B6E),
        onError = Color(0xFF0B1220),
    )
} else {
    lightColorScheme(
        primary = action,
        onPrimary = Color.White,
        primaryContainer = brandSoft,
        onPrimaryContainer = textPrimary,
        secondary = blue,
        onSecondary = Color.White,
        background = bgBase,
        onBackground = textPrimary,
        surface = bgCardGradient.first(),
        onSurface = textPrimary,
        surfaceVariant = surface1,
        onSurfaceVariant = textSecondary,
        outline = border2,
        error = Color(0xFFB91C1C),
        onError = Color.White,
    )
}

@Composable
fun ProvideAppTheme(
    isDark: Boolean,
    onThemeChange: (Boolean) -> Unit = {},
    content: @Composable () -> Unit,
) {
    val colors = if (isDark) darkAppColors() else lightAppColors()
    // Íconos de las barras del sistema según el tema de la APP (no el del sistema):
    // enableEdgeToEdge() solo detecta el modo oscuro del sistema, así que se corrige aquí
    // y reacciona al toggle de Ajustes sin recrear la Activity.
    val view = LocalView.current
    SideEffect {
        val window = (view.context as? android.app.Activity)?.window ?: return@SideEffect
        WindowInsetsControllerCompat(window, view).apply {
            isAppearanceLightStatusBars = !colors.isDark
            isAppearanceLightNavigationBars = !colors.isDark
        }
    }
    val density = LocalDensity.current
    // +8% de escala tipográfica respecto a la del sistema: mismo aumento en ambos modos.
    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalOnThemeChange provides onThemeChange,
        LocalDensity provides Density(density.density, density.fontScale * 1.08f),
    ) {
        MaterialTheme(colorScheme = colors.toColorScheme(), content = content)
    }
}
