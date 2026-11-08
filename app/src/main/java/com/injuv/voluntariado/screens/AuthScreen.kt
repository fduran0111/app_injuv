package com.injuv.voluntariado.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(onAuthenticated: (name: String, email: String, birthDateMillis: Long?) -> Unit) {
    val T = LocalAppColors.current
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

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = T.coral,
        unfocusedBorderColor = T.inputBorder,
        focusedLabelColor = T.coral,
        unfocusedLabelColor = T.textMuted,
        cursorColor = T.coral,
        focusedTextColor = T.textPrimary,
        unfocusedTextColor = T.textPrimary,
        focusedTrailingIconColor = T.textMuted,
        unfocusedTrailingIconColor = T.textMuted,
        focusedLeadingIconColor = T.coral,
        unfocusedLeadingIconColor = T.textMuted,
        focusedSupportingTextColor = T.textMuted,
        unfocusedSupportingTextColor = T.textMuted,
        focusedPlaceholderColor = T.textSubtle,
        unfocusedPlaceholderColor = T.textSubtle,
    )

    val birthText = birthMillis?.let { millis ->
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            .apply { timeZone = TimeZone.getTimeZone("UTC") }
            .format(Date(millis))
    } ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase)
            .imePadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(T.bgHeaderGradient))
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_injuv_logo),
                contentDescription = "INJUV",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp),
                contentScale = ContentScale.Fit,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                if (isLogin) "INJUV Voluntariado" else "Crear cuenta",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.White,
            )
            Text(
                if (isLogin) "Ingresa para continuar" else "Regístrate para comenzar a participar",
                fontFamily = Outfit,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.85f),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(T.surface1),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isLogin) Brush.linearGradient(listOf(T.coral, T.coralDark))
                            else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                        )
                        .clickable { isLogin = true; error = null }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "Iniciar sesión",
                        fontFamily = SpaceGrotesk,
                        fontWeight = if (isLogin) FontWeight.SemiBold else FontWeight.Medium,
                        fontSize = 13.sp,
                        color = if (isLogin) Color.White else T.textMuted,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (!isLogin) Brush.linearGradient(listOf(T.coral, T.coralDark))
                            else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                        )
                        .clickable { isLogin = false; error = null }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "Crear cuenta",
                        fontFamily = SpaceGrotesk,
                        fontWeight = if (!isLogin) FontWeight.SemiBold else FontWeight.Medium,
                        fontSize = 13.sp,
                        color = if (!isLogin) Color.White else T.textMuted,
                    )
                }
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
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(14.dp),
                    colors = fieldColors,
                )
            }

            if (error != null) {
                Spacer(Modifier.height(10.dp))
                Text(error!!, fontFamily = Outfit, fontSize = 13.sp, color = T.coral)
            }

            Spacer(Modifier.height(22.dp))

            Button(
                onClick = {
                    val result = validateAuth(isLogin, name, lastName, email, rut, birthMillis, password, confirm)
                    if (result != null) {
                        error = result
                    } else {
                        val resolvedName = if (isLogin) {
                            email.substringBefore("@").replaceFirstChar { it.uppercase() }
                        } else {
                            "${name.trim()} ${lastName.trim()}".trim()
                        }
                        onAuthenticated(resolvedName, email.trim(), if (isLogin) null else birthMillis)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = T.coral, contentColor = Color.White),
            ) {
                Text(
                    if (isLogin) "Entrar" else "Registrarse",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
            }

            TextButton(
                onClick = { isLogin = !isLogin; error = null },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(
                    if (isLogin) "¿No tienes una cuenta? Regístrate aquí" else "¿Ya tienes una cuenta? Inicia sesión aquí",
                    fontFamily = Outfit,
                    fontSize = 13.sp,
                    color = T.blue,
                )
            }
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
