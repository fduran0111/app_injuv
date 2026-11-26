package com.injuv.voluntariado.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
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

// Tipografías de la plataforma web: Bricolage Grotesque (títulos y cifras) e Instrument Sans (texto y UI).
val Bricolage = FontFamily(
    Font(R.font.bricolage_medium, FontWeight.Medium),
    Font(R.font.bricolage_semibold, FontWeight.SemiBold),
    Font(R.font.bricolage_bold, FontWeight.Bold),
    Font(R.font.bricolage_extrabold, FontWeight.ExtraBold),
)

val InstrumentSans = FontFamily(
    Font(R.font.instrument_sans_regular, FontWeight.Normal),
    Font(R.font.instrument_sans_medium, FontWeight.Medium),
    Font(R.font.instrument_sans_semibold, FontWeight.SemiBold),
    Font(R.font.instrument_sans_bold, FontWeight.Bold),
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
    /** Rojo institucional para plazos, urgencia y acento (legible como texto). */
    val coral: Color,
    val coralDark: Color,
    /** Acción con texto blanco encima: contraste AA (≥4.5:1) en ambos temas. */
    val action: Color,
    val actionDark: Color,
    /** Ámbar legible como texto sobre el fondo del tema. */
    val yellow: Color,
    val blue: Color,
    val green: Color,
    val progressTrack: Color,
    val inputBg: Color,
    val inputBorder: Color,
    /** Ámbar de marcador (franja bajo palabras clave, cifras sobre tinta). */
    val ambar: Color,
    /** Relleno de fichas activas: tinta en claro, papel en oscuro. */
    val ink: Color,
    val onInk: Color,
    /** Superficie plana de tarjetas. */
    val card: Color,
)

private val Tinta = Color(0xFF0F1B2D)
private val Papel = Color(0xFFF4EFE6)

fun darkAppColors() = AppColors(
    isDark = true,
    bgBase = Color(0xFF0A1220),
    bgCardGradient = listOf(Color(0xFF111B2D), Color(0xFF111B2D)),
    bgHeaderGradient = listOf(Color(0xFF0F1B2D), Color(0xFF0A3A8C)),
    surface1 = Color(0x0DFFFFFF),
    surface2 = Color(0x17FFFFFF),
    border1 = Color(0x17FFFFFF),
    border2 = Color(0x29FFFFFF),
    textPrimary = Papel,
    textSecondary = Color(0xFFD5DAE3),
    textMuted = Color(0xFF9AA6BA),
    textSubtle = Color(0xFF7D8AA1),
    navBg = Color(0xFF131E32),
    navBorder = Color(0x17FFFFFF),
    navInactive = Color(0xFF8C98AE),
    coral = Color(0xFFFF6166),
    coralDark = Color(0xFFE3242B),
    action = Color(0xFF1E66E0),
    actionDark = Color(0xFF0052CC),
    yellow = Color(0xFFF2A93B),
    blue = Color(0xFF7FB0FF),
    green = Color(0xFF4ADE9B),
    progressTrack = Color(0x17FFFFFF),
    inputBg = Color(0x0DFFFFFF),
    inputBorder = Color(0x33FFFFFF),
    ambar = Color(0xFFF2A93B),
    ink = Papel,
    onInk = Tinta,
    card = Color(0xFF111B2D),
)

fun lightAppColors() = AppColors(
    isDark = false,
    bgBase = Papel,
    bgCardGradient = listOf(Color(0xFFFFFDF8), Color(0xFFFFFDF8)),
    bgHeaderGradient = listOf(Tinta, Color(0xFF0052CC)),
    surface1 = Color(0x0A0F1B2D),
    surface2 = Color(0x120F1B2D),
    border1 = Color(0x1A0F1B2D),
    border2 = Color(0x330F1B2D),
    textPrimary = Tinta,
    textSecondary = Color(0xFF33405A),
    textMuted = Color(0xFF5B6680),
    textSubtle = Color(0xFF747E94),
    navBg = Color(0xFFFFFDF8),
    navBorder = Color(0x1A0F1B2D),
    navInactive = Color(0xFF5B6680),
    coral = Color(0xFFC81E25),
    coralDark = Color(0xFFA3181E),
    action = Color(0xFF0052CC),
    actionDark = Color(0xFF003D99),
    yellow = Color(0xFF8A5A0B),
    blue = Color(0xFF0052CC),
    green = Color(0xFF047857),
    progressTrack = Color(0x1A0F1B2D),
    inputBg = Color(0xFFFFFDF8),
    inputBorder = Color(0x590F1B2D),
    ambar = Color(0xFFF2A93B),
    ink = Tinta,
    onInk = Papel,
    card = Color(0xFFFFFDF8),
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
        primaryContainer = action.copy(alpha = 0.12f),
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

/** Escala Material con las fuentes de la web: los componentes M3 (diálogos, campos, menús) las heredan. */
private val InjuvTypography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontFamily = Bricolage, fontWeight = FontWeight.ExtraBold),
        displayMedium = displayMedium.copy(fontFamily = Bricolage, fontWeight = FontWeight.ExtraBold),
        displaySmall = displaySmall.copy(fontFamily = Bricolage, fontWeight = FontWeight.Bold),
        headlineLarge = headlineLarge.copy(fontFamily = Bricolage, fontWeight = FontWeight.Bold),
        headlineMedium = headlineMedium.copy(fontFamily = Bricolage, fontWeight = FontWeight.Bold),
        headlineSmall = headlineSmall.copy(fontFamily = Bricolage, fontWeight = FontWeight.Bold),
        titleLarge = titleLarge.copy(fontFamily = Bricolage, fontWeight = FontWeight.Bold),
        titleMedium = titleMedium.copy(fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold),
        titleSmall = titleSmall.copy(fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold),
        bodyLarge = bodyLarge.copy(fontFamily = InstrumentSans),
        bodyMedium = bodyMedium.copy(fontFamily = InstrumentSans),
        bodySmall = bodySmall.copy(fontFamily = InstrumentSans),
        labelLarge = labelLarge.copy(fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold),
        labelMedium = labelMedium.copy(fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold),
        labelSmall = labelSmall.copy(fontFamily = InstrumentSans, fontWeight = FontWeight.Medium),
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
        MaterialTheme(colorScheme = colors.toColorScheme(), typography = InjuvTypography, content = content)
    }
}
