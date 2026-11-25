package com.injuv.voluntariado.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.R
import com.injuv.voluntariado.data.api.diasHasta
import com.injuv.voluntariado.ui.theme.AppColors
import com.injuv.voluntariado.ui.theme.Bricolage
import com.injuv.voluntariado.ui.theme.InstrumentSans
import com.injuv.voluntariado.ui.theme.LocalAppColors

/*
 * Kit visual de la plataforma web INJUV traducido a Compose (frontend-react/src/components/ui.tsx):
 * etiquetas por tono (Badge), fichas (ficha), título con marcador ámbar, trazo a mano,
 * tarjetas planas con anillo, botones sólidos y franjas de área.
 */

private val EaseOutWeb = CubicBezierEasing(0.23f, 1f, 0.32f, 1f)

/* ---------- Tonos (lib/constants.ts → Tone) ---------- */

enum class Tono { Neutral, Azul, Verde, Rojo, Ambar, Violeta, Gris }

data class ColoresTono(val fondo: Color, val texto: Color, val anillo: Color)

fun coloresTono(tono: Tono, T: AppColors): ColoresTono {
    val base = when (tono) {
        Tono.Neutral -> if (T.isDark) Color(0xFFD5DAE3) else Color(0xFF33405A)
        Tono.Azul -> if (T.isDark) Color(0xFF8DB9FF) else Color(0xFF003D99)
        Tono.Verde -> if (T.isDark) Color(0xFF5EE2A6) else Color(0xFF065F46)
        Tono.Rojo -> if (T.isDark) Color(0xFFFF8A8E) else Color(0xFF991B1B)
        Tono.Ambar -> if (T.isDark) Color(0xFFF7BF66) else Color(0xFF78350F)
        Tono.Violeta -> if (T.isDark) Color(0xFFC4B5FD) else Color(0xFF5B21B6)
        Tono.Gris -> if (T.isDark) Color(0xFFB4BDCC) else Color(0xFF334155)
    }
    val fondoClaro = when (tono) {
        Tono.Neutral -> Color(0xFFEBE3D4)
        Tono.Azul -> Color(0xFFE8F0FD)
        Tono.Verde -> Color(0xFFECFDF5)
        Tono.Rojo -> Color(0xFFFEF2F2)
        Tono.Ambar -> Color(0xFFFFFBEB)
        Tono.Violeta -> Color(0xFFF5F3FF)
        Tono.Gris -> Color(0xFFF1F5F9)
    }
    return if (T.isDark) {
        ColoresTono(fondo = base.copy(alpha = 0.14f), texto = base, anillo = base.copy(alpha = 0.35f))
    } else {
        ColoresTono(fondo = fondoClaro, texto = base, anillo = base.copy(alpha = 0.28f))
    }
}

/** Tono de cada estado de postulación, igual que ESTADO_POSTULACION_TONE de la web. */
fun tonoEstado(estado: String): Tono = when (estado) {
    "Pendiente de revisión" -> Tono.Ambar
    "Pre-seleccionado" -> Tono.Azul
    "Etapa de entrevista" -> Tono.Violeta
    "En lista de espera" -> Tono.Gris
    "Seleccionado", "Finalizado" -> Tono.Verde
    "No seleccionado" -> Tono.Rojo
    else -> Tono.Neutral
}

/* ---------- Etiqueta (Badge) ---------- */

/** Etiqueta rectangular con anillo, como `Badge` de la web. */
@Composable
fun Etiqueta(
    texto: String,
    tono: Tono = Tono.Neutral,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    solida: Boolean = false,
) {
    val T = LocalAppColors.current
    val c = coloresTono(tono, T)
    val fondo = if (solida) (if (T.isDark) T.card.copy(alpha = 0.92f) else Color(0xFFFFFDF8).copy(alpha = 0.95f)) else c.fondo
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .background(fondo)
            .border(1.dp, c.anillo, RoundedCornerShape(5.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icono != null) {
            Icon(icono, contentDescription = null, tint = c.texto, modifier = Modifier.size(12.dp))
            Spacer(Modifier.width(4.dp))
        }
        Text(
            texto,
            fontFamily = InstrumentSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            color = c.texto,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Etiqueta de plazo de postulación (DeadlineBadge de la web). */
@Composable
fun EtiquetaPlazo(fecha: String?, estado: String, modifier: Modifier = Modifier, postulado: Boolean = false, solida: Boolean = false) {
    if (postulado) return Etiqueta("Ya postulaste", Tono.Violeta, modifier, solida = solida)
    if (estado == "cerrada") return Etiqueta("Cerrada", Tono.Gris, modifier, solida = solida)
    val dias = diasHasta(fecha)
    when {
        dias == null -> Etiqueta("Abierta", Tono.Verde, modifier, solida = solida)
        dias < 0 -> Etiqueta("Plazo vencido", Tono.Gris, modifier, solida = solida)
        dias == 0 -> Etiqueta("Cierra hoy", Tono.Rojo, modifier, solida = solida)
        dias <= 7 -> Etiqueta("Cierra en $dias ${if (dias == 1) "día" else "días"}", Tono.Ambar, modifier, solida = solida)
        else -> Etiqueta("Abierta", Tono.Verde, modifier, solida = solida)
    }
}

/** Etiqueta del estado de una postulación con el tono de la web. */
@Composable
fun EtiquetaEstado(estado: String, modifier: Modifier = Modifier) = Etiqueta(estado, tonoEstado(estado), modifier)

/* ---------- Ficha (filtros) ---------- */

/** Ficha de filtro: píldora con borde que se entinta al activarse (`.ficha` de la web). */
@Composable
fun Ficha(texto: String, activa: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier, contador: Int? = null) {
    val T = LocalAppColors.current
    val fondo by animateColorAsState(if (activa) T.ink else T.card, tween(160), label = "fichaFondo")
    val texto2 by animateColorAsState(if (activa) T.onInk else T.textSecondary, tween(160), label = "fichaTexto")
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val escala by animateFloatAsState(if (pressed) 0.96f else 1f, tween(160), label = "fichaEscala")
    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .selectable(selected = activa, interactionSource = interaction, indication = null, enabled = true, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .scale(escala)
                .height(38.dp)
                .clip(RoundedCornerShape(50))
                .background(fondo)
                .border(1.5.dp, if (activa) T.ink else T.border2, RoundedCornerShape(50))
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(texto, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = texto2, maxLines = 1)
            if (contador != null) {
                Spacer(Modifier.width(6.dp))
                Text(
                    "$contador",
                    fontFamily = InstrumentSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = texto2,
                    style = TextStyle(fontFeatureSettings = "tnum"),
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(texto2.copy(alpha = 0.14f))
                        .padding(horizontal = 5.dp, vertical = 1.dp),
                )
            }
        }
    }
}

@Composable
fun FilaFichas(
    opciones: List<String>,
    seleccion: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    contadores: Map<String, Int> = emptyMap(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        opciones.forEach { op ->
            Ficha(op, activa = op == seleccion, onClick = { onSelect(op) }, contador = contadores[op])
        }
    }
}

/* ---------- Títulos ---------- */

/** Palabra que se marca por defecto: la última palabra con letras del título. */
fun palabraClave(titulo: String): String? =
    titulo.split(" ").lastOrNull { w -> w.any { it.isLetter() } }?.trim { !it.isLetterOrDigit() }?.takeIf { it.length > 1 }

/**
 * Título con la palabra clave marcada en ámbar (`.marcador-ambar` de la web).
 * En claro: franja ámbar bajo el texto tinta. En oscuro: la palabra va en ámbar sobre un velo ámbar suave.
 */
@Composable
fun TituloMarcador(
    texto: String,
    modifier: Modifier = Modifier,
    resaltado: String? = palabraClave(texto),
    fontSize: TextUnit = 30.sp,
    color: Color = LocalAppColors.current.textPrimary,
    maxLines: Int = 3,
    fontWeight: FontWeight = FontWeight.ExtraBold,
) {
    val T = LocalAppColors.current
    val inicio = resaltado?.let { texto.indexOf(it) }?.takeIf { it >= 0 }
    val fin = inicio?.let { it + resaltado.length }
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    val anotado = buildAnnotatedString {
        if (inicio == null || fin == null) {
            append(texto)
        } else {
            append(texto.substring(0, inicio))
            withStyle(SpanStyle(color = if (T.isDark) T.ambar else color)) { append(texto.substring(inicio, fin)) }
            append(texto.substring(fin))
        }
    }
    Text(
        anotado,
        modifier = modifier.drawBehind {
            val l = layout ?: return@drawBehind
            val ini = inicio ?: return@drawBehind
            val fin = fin ?: return@drawBehind
            val marcador = if (T.isDark) T.ambar.copy(alpha = 0.22f) else T.ambar
            var i: Int = ini
            while (i < fin) {
                val linea = l.getLineForOffset(i)
                val finLinea = minOf(fin, l.getLineEnd(linea, visibleEnd = true))
                if (finLinea <= i) break
                val izq = l.getBoundingBox(i).left
                val der = l.getBoundingBox(finLinea - 1).right
                val top = l.getLineTop(linea)
                val alto = l.getLineBottom(linea) - top
                val pad = 2.dp.toPx()
                drawRect(
                    marcador,
                    topLeft = Offset(minOf(izq, der) - pad, top + alto * 0.56f),
                    size = Size(kotlin.math.abs(der - izq) + pad * 2, alto * 0.32f),
                )
                i = l.getLineEnd(linea)
            }
        },
        onTextLayout = { layout = it },
        fontFamily = Bricolage,
        fontWeight = fontWeight,
        fontSize = fontSize,
        lineHeight = fontSize * 1.06f,
        letterSpacing = (-0.025).em,
        color = color,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None)),
    )
}

/** Subrayado "a mano" (TrazoTitulo de la web) que se dibuja de izquierda a derecha al aparecer. */
@Composable
fun Trazo(modifier: Modifier = Modifier, color: Color = LocalAppColors.current.action, grosor: Dp = 3.5.dp, animar: Boolean = true) {
    val progreso = remember { Animatable(if (animar) 0f else 1f) }
    LaunchedEffect(Unit) { progreso.animateTo(1f, tween(700, easing = CubicBezierEasing(0.3f, 0.9f, 0.3f, 1f))) }
    Canvas(modifier) {
        val sx = size.width / 240f
        val sy = size.height / 14f
        val path = Path().apply {
            moveTo(3 * sx, 9 * sy)
            cubicTo(48 * sx, 3 * sy, 92 * sx, 12 * sy, 136 * sx, 7 * sy)
            cubicTo(180 * sx, 2 * sy, 204 * sx, 2 * sy, 237 * sx, 8 * sy)
        }
        val medida = PathMeasure().apply { setPath(path, false) }
        val parcial = Path()
        medida.getSegment(0f, medida.length * progreso.value, parcial, true)
        drawPath(parcial, color, style = Stroke(width = grosor.toPx(), cap = StrokeCap.Round))
    }
}

/** Título de sección con trazo bajo el texto y una acción opcional a la derecha. */
@Composable
fun TituloSeccion(
    titulo: String,
    modifier: Modifier = Modifier,
    accion: String? = null,
    onAccion: (() -> Unit)? = null,
    colorTrazo: Color = LocalAppColors.current.action,
) {
    val T = LocalAppColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.width(IntrinsicSize.Max).weight(1f, fill = false)) {
            Text(
                titulo,
                fontFamily = Bricolage,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                letterSpacing = (-0.02).em,
                color = T.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Trazo(
                Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = colorTrazo,
            )
        }
        if (accion != null && onAccion != null) {
            Row(
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onAccion)
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(accion, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = T.action.takeIf { !T.isDark } ?: T.blue)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Filled.NorthEast, contentDescription = null, tint = T.action.takeIf { !T.isDark } ?: T.blue, modifier = Modifier.size(14.dp))
            }
        }
    }
}

/* ---------- Marca ---------- */

/** Franja bicolor azul|rojo del logotipo INJUV. */
@Composable
fun BarraBicolor(modifier: Modifier = Modifier, alto: Dp = 4.dp) {
    Row(modifier.height(alto)) {
        Box(Modifier.weight(0.41f).fillMaxHeight().background(Color(0xFF0070BA)))
        Box(Modifier.weight(0.59f).fillMaxHeight().background(Color(0xFFEF4044)))
    }
}

/** Logotipo oficial INJUV (versión blanca sobre fondos oscuros o fotos). */
@Composable
fun LogoInjuv(modifier: Modifier = Modifier, blanco: Boolean = LocalAppColors.current.isDark) {
    Image(
        painter = painterResource(if (blanco) R.drawable.logo_injuv_blanco else R.drawable.logo_injuv),
        contentDescription = "INJUV, Ministerio de Desarrollo Social y Familia",
        contentScale = ContentScale.Fit,
        modifier = modifier,
    )
}

/* ---------- Superficies ---------- */

/** Tarjeta plana con anillo (`rounded-2xl bg-surface ring-1 ring-tinta/10` de la web). */
@Composable
fun Tarjeta(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    radio: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val T = LocalAppColors.current
    val forma = RoundedCornerShape(radio)
    Column(
        modifier = modifier
            .then(
                if (T.isDark) Modifier else Modifier.shadow(10.dp, forma, ambientColor = Color(0x140F1B2D), spotColor = Color(0x330F1B2D)),
            )
            .clip(forma)
            .background(T.card)
            .border(1.dp, T.border1, forma)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        content = content,
    )
}

/** Botón circular de encabezado (menú, campana, volver). */
@Composable
fun BotonCirculo(icono: ImageVector, descripcion: String, onClick: () -> Unit, modifier: Modifier = Modifier, insignia: Boolean = false) {
    val T = LocalAppColors.current
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick, role = Role.Button),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(T.card)
                .border(1.dp, T.border2, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icono, contentDescription = descripcion, tint = T.textPrimary, modifier = Modifier.size(21.dp))
        }
        if (insignia) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-6).dp, y = 6.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(T.coral)
                    .border(2.dp, T.bgBase, CircleShape),
            )
        }
    }
}

/** Botón primario sólido de la web: azul, radio 12dp, se aprieta al pulsar. */
@Composable
fun BotonPrimario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    cargando: Boolean = false,
    habilitado: Boolean = true,
    color: Color = LocalAppColors.current.action,
    colorTexto: Color = Color.White,
    alto: Dp = 52.dp,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val escala by animateFloatAsState(if (pressed) 0.97f else 1f, tween(140), label = "botonEscala")
    val activo = habilitado && !cargando
    Row(
        modifier = modifier
            .scale(escala)
            .height(alto)
            .shadow(if (activo) 6.dp else 0.dp, RoundedCornerShape(12.dp), ambientColor = color.copy(alpha = 0.3f), spotColor = color.copy(alpha = 0.45f))
            .clip(RoundedCornerShape(12.dp))
            .background(if (activo) color else color.copy(alpha = 0.45f))
            .clickable(enabled = activo, interactionSource = interaction, indication = androidx.compose.material.ripple.rememberRipple(color = colorTexto), role = Role.Button, onClick = onClick)
            .padding(horizontal = 22.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (cargando) {
            CircularProgressIndicator(color = colorTexto, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
        }
        Text(texto, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = colorTexto, maxLines = 1)
        if (icono != null && !cargando) {
            Spacer(Modifier.width(8.dp))
            Icon(icono, contentDescription = null, tint = colorTexto, modifier = Modifier.size(18.dp))
        }
    }
}

/** Botón de contorno (variante `outline` de la web). */
@Composable
fun BotonContorno(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    color: Color = LocalAppColors.current.textPrimary,
    alto: Dp = 48.dp,
    habilitado: Boolean = true,
) {
    val T = LocalAppColors.current
    Row(
        modifier = modifier
            .height(alto)
            .clip(RoundedCornerShape(12.dp))
            .background(T.card)
            .border(1.5.dp, if (habilitado) color.copy(alpha = 0.55f) else T.border1, RoundedCornerShape(12.dp))
            .clickable(enabled = habilitado, role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icono != null) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(17.dp))
            Spacer(Modifier.width(7.dp))
        }
        Text(texto, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = if (habilitado) color else T.textSubtle, maxLines = 1)
    }
}

/** Avatar con foto o iniciales en tinta (Avatar de la web). */
@Composable
fun Avatar(nombre: String, url: String? = null, modifier: Modifier = Modifier, size: Dp = 40.dp) {
    val T = LocalAppColors.current
    val iniciales = nombre.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }.ifBlank { "?" }
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(if (T.isDark) Color(0xFF223352) else Color(0xFF0F1B2D))
            .border(2.dp, T.card, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            iniciales,
            fontFamily = Bricolage,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.36f).sp,
            color = Color(0xFFF4EFE6),
        )
        if (url != null) {
            AsyncImage(model = url, contentDescription = nombre, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())
        }
    }
}

/** Flecha en círculo que gira al pulsar la tarjeta ("Leer noticia ↗"). */
@Composable
fun FlechaCircular(modifier: Modifier = Modifier, activa: Boolean = false) {
    val T = LocalAppColors.current
    val giro by animateFloatAsState(if (activa) 45f else 0f, tween(300, easing = EaseOutWeb), label = "flecha")
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (activa) T.action else Color.Transparent)
            .border(1.5.dp, if (activa) T.action else T.border2, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Filled.NorthEast, contentDescription = null, tint = if (activa) Color.White else T.textPrimary, modifier = Modifier.size(16.dp).rotate(giro))
    }
}

/* ---------- Áreas de voluntariado ---------- */

fun iconoArea(area: String): ImageVector = when (area) {
    "Educación" -> Icons.Filled.School
    "Comunidad" -> Icons.Filled.Groups
    "Medio Ambiente" -> Icons.Filled.Park
    "Salud" -> Icons.Filled.HealthAndSafety
    "Deporte" -> Icons.Filled.SportsSoccer
    "Cultura" -> Icons.Filled.TheaterComedy
    "Emergencia" -> Icons.Filled.LocalFireDepartment
    "Tecnología" -> Icons.Filled.Computer
    "Arte" -> Icons.Filled.Palette
    "Animales" -> Icons.Filled.Pets
    "Adultos Mayores" -> Icons.Filled.Favorite
    "Niños y Jóvenes" -> Icons.Filled.ChildCare
    "Discapacidad" -> Icons.Filled.Accessible
    else -> Icons.Filled.VolunteerActivism
}

val AREAS_VOLUNTARIADO = listOf(
    "Educación", "Comunidad", "Medio Ambiente", "Salud", "Deporte", "Cultura", "Emergencia",
    "Desarrollo Social", "Tecnología", "Arte", "Animales", "Adultos Mayores", "Niños y Jóvenes", "Discapacidad",
)

/** Colores del bloque de un área (TONOS_AREA de la web): fondo, texto y marca de agua. */
fun tonoArea(area: String, T: AppColors): Triple<Color, Color, Color> {
    val i = AREAS_VOLUNTARIADO.indexOf(area).let { if (it < 0) 1 else it }
    return when (i % 4) {
        0 -> Triple(Color(0xFF0052CC), Color.White, Color.White.copy(alpha = 0.16f))
        1 -> Triple(if (T.isDark) Color(0xFF1B2A44) else Color(0xFF0F1B2D), Color(0xFFF4EFE6), Color(0xFFF2A93B).copy(alpha = 0.28f))
        2 -> Triple(Color(0xFFF2A93B), Color(0xFF0F1B2D), Color(0xFF0F1B2D).copy(alpha = 0.14f))
        else -> Triple(if (T.isDark) Color(0xFF16305C) else Color(0xFFE8F0FD), if (T.isDark) Color(0xFFC9DCFF) else Color(0xFF003D99), Color(0xFF0052CC).copy(alpha = if (T.isDark) 0.35f else 0.15f))
    }
}

/** Bloque de color del área con ícono gigante de marca de agua (franja de OportunidadCard sin foto). */
@Composable
fun BloqueArea(area: String, modifier: Modifier = Modifier, mostrarNombre: Boolean = true, tamanoNombre: TextUnit = 20.sp, content: @Composable BoxScope.() -> Unit = {}) {
    val T = LocalAppColors.current
    val (fondo, texto, marca) = tonoArea(area, T)
    Box(modifier = modifier.background(fondo)) {
        Icon(
            iconoArea(area),
            contentDescription = null,
            tint = marca,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 22.dp, y = 28.dp)
                .size(150.dp)
                .rotate(-12f),
        )
        if (mostrarNombre && area.isNotBlank()) {
            Text(
                area,
                fontFamily = Bricolage,
                fontWeight = FontWeight.ExtraBold,
                fontSize = tamanoNombre,
                lineHeight = tamanoNombre * 1.05f,
                letterSpacing = (-0.02).em,
                color = texto,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp),
            )
        }
        content()
    }
}

/** Fila de dato con ícono (lugar, fecha, cupos) en la ficha técnica de las tarjetas. */
@Composable
fun DatoIcono(icono: ImageVector, texto: String, modifier: Modifier = Modifier, destacado: Boolean = false) {
    val T = LocalAppColors.current
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = T.textMuted, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            texto,
            fontFamily = InstrumentSans,
            fontWeight = if (destacado) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 13.sp,
            color = if (destacado) T.textPrimary else T.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(fontFeatureSettings = "tnum"),
        )
    }
}

/** Línea divisoria fina (punteada opcional, como el pie de DocumentoCard). */
@Composable
fun Divisor(modifier: Modifier = Modifier, punteado: Boolean = false) {
    val T = LocalAppColors.current
    Canvas(modifier.fillMaxWidth().height(1.5.dp)) {
        drawLine(
            color = if (punteado) T.border2 else T.border1,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = size.height,
            pathEffect = if (punteado) androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())) else null,
        )
    }
}

/** Encabezado de sub-pantalla: volver, título con marcador, subtítulo y acciones. */
@Composable
fun SubHeader(
    titulo: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,
    resaltado: String? = palabraClave(titulo),
    extra: @Composable (() -> Unit)? = null,
    acciones: @Composable () -> Unit = {},
) {
    val T = LocalAppColors.current
    Column(modifier = modifier.fillMaxWidth().background(T.bgBase)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BotonCirculo(Icons.AutoMirrored.Filled.ArrowBack, "Volver", onBack)
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                TituloMarcador(titulo, resaltado = resaltado, fontSize = 23.sp, maxLines = 1)
                if (!subtitulo.isNullOrBlank()) {
                    Text(subtitulo, fontFamily = InstrumentSans, fontSize = 13.sp, color = T.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                extra?.invoke()
            }
            acciones()
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(T.border1))
    }
}

/** Dato numérico de una cabecera de perfil (cifra en Bricolage tabular sobre su etiqueta). */
data class DatoPerfil(val valor: String, val etiqueta: String)

/**
 * Cabecera de identidad para Perfil y Organización: barra de acciones arriba y una tarjeta con
 * franja bicolor, avatar, nombre con marcador, etiqueta de rol, cifras y acción principal.
 */
@Composable
fun CabeceraPerfil(
    nombre: String,
    onMenu: () -> Unit,
    onAlertas: () -> Unit,
    modifier: Modifier = Modifier,
    avatar: @Composable () -> Unit,
    rol: String? = null,
    tonoRol: Tono = Tono.Azul,
    detalle: String? = null,
    datos: List<DatoPerfil> = emptyList(),
    accion: String? = null,
    iconoAccion: ImageVector? = null,
    onAccion: (() -> Unit)? = null,
    accionesExtra: @Composable () -> Unit = {},
    debajoRol: @Composable () -> Unit = {},
) {
    val T = LocalAppColors.current
    Column(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BotonCirculo(Icons.Filled.Menu, "Recursos", onMenu)
            Spacer(Modifier.weight(1f))
            LogoInjuv(Modifier.height(26.dp))
            Spacer(Modifier.weight(1f))
            accionesExtra()
            BotonCirculo(Icons.Outlined.Notifications, "Alertas", onAlertas)
        }
        Tarjeta(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            radio = 20.dp,
        ) {
            BarraBicolor(Modifier.fillMaxWidth(), alto = 5.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 18.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(78.dp)
                            .border(2.dp, T.ambar, CircleShape)
                            .padding(4.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center,
                    ) { avatar() }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        TituloMarcador(nombre, resaltado = nombre.split(" ").firstOrNull(), fontSize = 23.sp, maxLines = 2)
                        if (rol != null) {
                            Spacer(Modifier.height(6.dp))
                            Etiqueta(rol, tonoRol)
                        }
                        debajoRol()
                    }
                }
                if (detalle != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(detalle, fontFamily = InstrumentSans, fontSize = 13.sp, color = T.textMuted)
                }
                if (datos.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(T.surface1)
                            .border(1.dp, T.border1, RoundedCornerShape(12.dp))
                            .padding(vertical = 12.dp),
                    ) {
                        datos.forEachIndexed { i, d ->
                            if (i > 0) Box(Modifier.width(1.dp).height(34.dp).background(T.border2).align(Alignment.CenterVertically))
                            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    d.valor,
                                    fontFamily = Bricolage,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 22.sp,
                                    color = if (T.isDark) T.ambar else T.textPrimary,
                                    style = TextStyle(fontFeatureSettings = "tnum"),
                                )
                                Text(d.etiqueta, fontFamily = InstrumentSans, fontSize = 12.sp, color = T.textMuted, maxLines = 1)
                            }
                        }
                    }
                }
                if (accion != null && onAccion != null) {
                    Spacer(Modifier.height(14.dp))
                    BotonContorno(accion, onAccion, Modifier.fillMaxWidth(), icono = iconoAccion)
                }
            }
        }
    }
}
