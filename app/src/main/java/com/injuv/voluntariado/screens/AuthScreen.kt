package com.injuv.voluntariado.screens

import androidx.compose.foundation.Image
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.em
import androidx.core.view.WindowCompat
import com.injuv.voluntariado.components.BarraBicolor
import com.injuv.voluntariado.components.BotonPrimario
import com.injuv.voluntariado.components.LogoInjuv
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.injuv.voluntariado.R
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.data.api.ApiException
import com.injuv.voluntariado.data.api.LoginBody
import com.injuv.voluntariado.data.api.RegisterBody
import com.injuv.voluntariado.data.api.SessionManager
import com.injuv.voluntariado.data.api.Usuario
import com.injuv.voluntariado.data.api.limpiarRut
import kotlinx.coroutines.launch
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.InstrumentSans
import com.injuv.voluntariado.ui.theme.Bricolage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Inicio de sesión y registro contra `/auth/login` y `/auth/register`. Al entrar, guarda el JWT. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(onAuthenticated: (usuario: Usuario, birthDateMillis: Long?) -> Unit = { _, _ -> }) {
    val T = LocalAppColors.current
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    var isLogin by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var birthMillis by remember { mutableStateOf<Long?>(null) }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Campos sobre vidrio oscuro: siempre claros, sin importar el tema de la app.
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF8DB9FF),
        unfocusedBorderColor = Color.White.copy(alpha = 0.26f),
        focusedLabelColor = Color(0xFF8DB9FF),
        unfocusedLabelColor = Color.White.copy(alpha = 0.72f),
        cursorColor = Color(0xFFF2A93B),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedContainerColor = Color.White.copy(alpha = 0.07f),
        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
        focusedTrailingIconColor = Color.White.copy(alpha = 0.8f),
        unfocusedTrailingIconColor = Color.White.copy(alpha = 0.65f),
        focusedLeadingIconColor = Color(0xFF8DB9FF),
        unfocusedLeadingIconColor = Color.White.copy(alpha = 0.65f),
        focusedSupportingTextColor = Color.White.copy(alpha = 0.65f),
        unfocusedSupportingTextColor = Color.White.copy(alpha = 0.65f),
        focusedPlaceholderColor = Color.White.copy(alpha = 0.45f),
        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.45f),
    )

    // Íconos claros en la barra de estado mientras se ve la foto.
    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = (view.context as? android.app.Activity)?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        val previo = controller?.isAppearanceLightStatusBars
        controller?.isAppearanceLightStatusBars = false
        onDispose { if (previo != null) controller.isAppearanceLightStatusBars = previo }
    }

    val birthText = birthMillis?.let { millis ->
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            .apply { timeZone = TimeZone.getTimeZone("UTC") }
            .format(Date(millis))
    } ?: ""

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1220)),
    ) {
        val asienta = remember { Animatable(1.08f) }
        LaunchedEffect(Unit) { asienta.animateTo(1f, tween(1800, easing = CubicBezierEasing(0.23f, 1f, 0.32f, 1f))) }
        Image(
            painter = painterResource(R.drawable.foto_acceso),
            contentDescription = "Cumbre de voluntariado juvenil INJUV",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { scaleX = asienta.value; scaleY = asienta.value },
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color(0x8C0A1220),
                        0.32f to Color(0x660A1220),
                        0.58f to Color(0xE00A1220),
                        1f to Color(0xFA0A1220),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            LogoInjuv(Modifier.padding(top = 14.dp).height(30.dp), blanco = true)
            Spacer(Modifier.height(if (isLogin) 150.dp else 36.dp))

            LineaSube(if (isLogin) "Bienvenido/a" else "Súmate a la red", Color.White, retraso = 0, clave = isLogin)
            LineaSube(if (isLogin) "de vuelta" else "de voluntariado", Color(0xFFF2A93B), retraso = 110, clave = isLogin)
            Spacer(Modifier.height(10.dp))
            Text(
                if (isLogin) "Ingresa para postular y seguir tus voluntariados." else "Crea tu cuenta y postula a voluntariados en todo Chile.",
                fontFamily = InstrumentSans,
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.82f),
            )
            Spacer(Modifier.height(22.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF0F1B2D).copy(alpha = 0.64f))
                    .border(
                        1.dp,
                        Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.30f), Color.White.copy(alpha = 0.06f))),
                        RoundedCornerShape(24.dp),
                    )
                    .padding(18.dp),
            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .padding(4.dp),
            ) {
                PestanaVidrio("Iniciar sesión", activa = isLogin, modifier = Modifier.weight(1f)) { isLogin = true; error = null }
                PestanaVidrio("Crear cuenta", activa = !isLogin, modifier = Modifier.weight(1f)) { isLogin = false; error = null }
            }

            Spacer(Modifier.height(22.dp))

            if (!isLogin) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; error = null },
                        modifier = Modifier.weight(1f),
                        label = { Text("Nombre") },
                        placeholder = { Text("Tu nombre") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = fieldColors,
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it; error = null },
                        modifier = Modifier.weight(1f),
                        label = { Text("Apellido") },
                        placeholder = { Text("Tu apellido") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = fieldColors,
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; error = null },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Correo electrónico") },
                placeholder = { Text("tunombre@dominio.cl") },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors,
            )
            Spacer(Modifier.height(12.dp))

            if (!isLogin) {
                OutlinedTextField(
                    value = rut,
                    onValueChange = { rut = it; error = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("RUT") },
                    placeholder = { Text("12345678-9") },
                    leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null) },
                    supportingText = { Text("Formato: 12345678-9 (con guion y dígito verificador)") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = fieldColors,
                )
                Spacer(Modifier.height(12.dp))

                Box {
                    OutlinedTextField(
                        value = birthText,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Fecha de nacimiento") },
                        placeholder = { Text("dd-mm-aaaa") },
                        trailingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
                        supportingText = { Text("Debes tener al menos 15 años para registrarte.") },
                        readOnly = true,
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = fieldColors,
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showDatePicker = true },
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; error = null },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contraseña") },
                placeholder = { Text(if (isLogin) "Tu contraseña" else "Mínimo 8 caracteres") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPassword) "Ocultar" else "Mostrar",
                        )
                    }
                },
                supportingText = if (!isLogin) ({ Text("Mínimo 8 caracteres.") }) else null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors,
            )

            if (!isLogin) {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = confirm,
                    onValueChange = { confirm = it; error = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Confirmar contraseña") },
                    placeholder = { Text("Repite tu contraseña") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(14.dp),
                    colors = fieldColors,
                )
            }

            if (error != null) {
                Spacer(Modifier.height(10.dp))
                Text(error!!, fontFamily = InstrumentSans, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = Color(0xFFFF9EA1))
            }

            Spacer(Modifier.height(22.dp))

            BotonPrimario(
                texto = if (isLogin) "Entrar" else "Crear mi cuenta",
                icono = Icons.AutoMirrored.Filled.ArrowForward,
                cargando = loading,
                color = Color(0xFF1E66E0),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val result = validateAuth(isLogin, name, lastName, email, rut, birthMillis, password, confirm)
                    if (result != null) {
                        error = result
                        return@BotonPrimario
                    }
                    loading = true
                    scope.launch {
                        try {
                            val respuesta = ApiClient.call {
                                if (isLogin) {
                                    login(LoginBody(email.trim(), password))
                                } else {
                                    register(
                                        RegisterBody(
                                            nombre = name.trim(),
                                            apellido = lastName.trim(),
                                            email = email.trim(),
                                            rut = limpiarRut(rut),
                                            fecha_nacimiento = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                                                .apply { timeZone = TimeZone.getTimeZone("UTC") }
                                                .format(Date(birthMillis!!)),
                                            password = password,
                                        ),
                                    )
                                }
                            }
                            SessionManager.guardar(respuesta)
                            onAuthenticated(respuesta.user, if (isLogin) null else birthMillis)
                        } catch (e: ApiException) {
                            error = e.message
                        } finally {
                            loading = false
                        }
                    }
                },
            )
            if (loading) {
                Text(
                    "Si el servidor estaba inactivo, la primera conexión puede tardar hasta un minuto.",
                    fontFamily = InstrumentSans,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp),
                )
            }

            TextButton(
                onClick = { isLogin = !isLogin; error = null },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(
                    if (isLogin) "¿No tienes cuenta? Regístrate gratis" else "¿Ya tienes cuenta? Inicia sesión",
                    fontFamily = InstrumentSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF8DB9FF),
                )
            }
            }

            Spacer(Modifier.height(22.dp))
            BarraBicolor(Modifier.width(72.dp).clip(RoundedCornerShape(50)), alto = 4.dp)
            Spacer(Modifier.height(10.dp))
            Text(
                "Instituto Nacional de la Juventud · Gobierno de Chile",
                fontFamily = InstrumentSans,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f),
            )
            Spacer(Modifier.height(20.dp))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = birthMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    birthMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                    error = null
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun validateAuth(
    isLogin: Boolean,
    name: String,
    lastName: String,
    email: String,
    rut: String,
    birthMillis: Long?,
    password: String,
    confirm: String,
): String? {
    if (!isLogin) {
        if (name.isBlank()) return "Escribe tu nombre"
        if (lastName.isBlank()) return "Escribe tu apellido"
    }
    if (email.isBlank() || !email.contains("@")) return "Ingresa un correo válido"
    if (isLogin) {
        if (password.isBlank()) return "Ingresa tu contraseña"
    } else {
        if (!isValidRut(rut)) return "Ingresa un RUT válido (ej: 12345678-9)"
        if (birthMillis == null) return "Selecciona tu fecha de nacimiento"
        if (computeAgeYears(birthMillis) < 15) return "Debes tener al menos 15 años para registrarte"
        if (password.length < 8) return "La contraseña debe tener al menos 8 caracteres"
        if (password != confirm) return "Las contraseñas no coinciden"
    }
    return null
}

/** Valida un RUT chileno con dígito verificador (módulo 11). Acepta formato "12345678-9". */
private fun isValidRut(rut: String): Boolean {
    val match = Regex("^(\\d{1,8})-([0-9kK])$").find(rut.trim().replace(".", "")) ?: return false
    val body = match.groupValues[1]
    val dv = match.groupValues[2].first().uppercaseChar()
    var sum = 0
    var mul = 2
    for (c in body.reversed()) {
        sum += (c - '0') * mul
        mul = if (mul == 7) 2 else mul + 1
    }
    return when (val expected = 11 - sum % 11) {
        11 -> dv == '0'
        10 -> dv == 'K'
        else -> dv == '0' + expected
    }
}

/** Edad en años cumplidos para un timestamp UTC a medianoche (como entrega DatePicker). */
fun computeAgeYears(birthMillis: Long): Int {
    val dob = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = birthMillis }
    val now = Calendar.getInstance()
    var age = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
    if (now.get(Calendar.MONTH) < dob.get(Calendar.MONTH) ||
        (now.get(Calendar.MONTH) == dob.get(Calendar.MONTH) &&
            now.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH))
    ) {
        age--
    }
    return age
}


/** Línea de titular que sube desde detrás de su caja al aparecer (`.linea` de la web). */
@Composable
private fun LineaSube(texto: String, color: Color, retraso: Int, clave: Any) {
    val avance = remember(clave) { Animatable(1f) }
    LaunchedEffect(clave) {
        kotlinx.coroutines.delay(retraso.toLong())
        avance.animateTo(0f, tween(900, easing = CubicBezierEasing(0.23f, 1f, 0.32f, 1f)))
    }
    Box(Modifier.clipToBounds()) {
        Text(
            texto,
            fontFamily = Bricolage,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 44.sp,
            lineHeight = 47.sp,
            letterSpacing = (-0.03).em,
            color = color,
            modifier = Modifier.graphicsLayer { translationY = size.height * 1.05f * avance.value },
        )
    }
}

/** Pestaña del selector Iniciar sesión / Crear cuenta sobre el vidrio. */
@Composable
private fun PestanaVidrio(texto: String, activa: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .heightIn(min = 44.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(if (activa) Color(0xFFF4EFE6) else Color.Transparent)
            .selectable(selected = activa, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            texto,
            fontFamily = InstrumentSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = if (activa) Color(0xFF0F1B2D) else Color.White.copy(alpha = 0.75f),
        )
    }
}
