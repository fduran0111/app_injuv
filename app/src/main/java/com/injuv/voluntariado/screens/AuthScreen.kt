package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolunteerActivism
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.data.api.ApiException
import com.injuv.voluntariado.data.api.LoginBody
import com.injuv.voluntariado.data.api.RegisterBody
import com.injuv.voluntariado.data.api.SessionManager
import com.injuv.voluntariado.data.api.formatearFecha
import com.injuv.voluntariado.data.api.limpiarRut
import com.injuv.voluntariado.data.api.rutValido
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

/** Inicio de sesión y registro contra `/auth/login` y `/auth/register`. Al entrar, guarda el JWT. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen() {
    val T = LocalAppColors.current
    val scope = rememberCoroutineScope()
    var modoRegistro by rememberSaveable { mutableStateOf(false) }

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var nombre by rememberSaveable { mutableStateOf("") }
    var apellido by rememberSaveable { mutableStateOf("") }
    var rut by rememberSaveable { mutableStateOf("") }
    var fechaNacimiento by rememberSaveable { mutableStateOf("") } // yyyy-MM-dd
    var confirmar by rememberSaveable { mutableStateOf("") }
    var verPassword by remember { mutableStateOf(false) }
    var mostrarCalendario by remember { mutableStateOf(false) }

    var error by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }

    fun validar(): String? {
        if (!email.contains("@")) return "Ingresa un correo válido"
        if (password.isBlank()) return "Ingresa tu contraseña"
        if (!modoRegistro) return null
        if (nombre.isBlank() || apellido.isBlank()) return "Ingresa tu nombre y apellido"
        if (!rutValido(rut)) return "Revisa el RUT ingresado"
        if (fechaNacimiento.isBlank()) return "Indica tu fecha de nacimiento"
        if (password.length < 8) return "La contraseña debe tener al menos 8 caracteres"
        if (password != confirmar) return "Las contraseñas no coinciden"
        return null
    }

    fun enviar() {
        error = validar()
        if (error != null) return
        cargando = true
        scope.launch {
            try {
                val respuesta = ApiClient.call {
                    if (modoRegistro) {
                        register(
                            RegisterBody(
                                nombre = nombre.trim(),
                                apellido = apellido.trim(),
                                email = email.trim(),
                                rut = limpiarRut(rut),
                                fecha_nacimiento = fechaNacimiento,
                                password = password,
                            ),
                        )
                    } else {
                        login(LoginBody(email.trim(), password))
                    }
                }
                SessionManager.guardar(respuesta) // la navegación cambia sola al detectar el usuario
            } catch (e: ApiException) {
                error = e.message
            } finally {
                cargando = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase)
            .verticalScroll(rememberScrollState())
            .imePadding(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(T.bgHeaderGradient))
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 36.dp),
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.VolunteerActivism, contentDescription = null, tint = Color(0xFFFFD60A))
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    if (modoRegistro) "Crea tu cuenta" else "Bienvenido/a",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = Color.White,
                )
                Text(
                    if (modoRegistro) "Únete a la red de voluntariado juvenil de Chile" else "Ingresa para postular y seguir tus voluntariados",
                    fontFamily = Outfit,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f),
                )
            }
        }

        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (error != null) {
                Text(
                    error!!,
                    fontFamily = Outfit,
                    fontSize = 13.sp,
                    color = T.coral,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(T.coral.copy(alpha = 0.12f))
                        .padding(12.dp),
                )
            }
            if (modoRegistro) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Campo("Nombre", nombre, { nombre = it }, Modifier.weight(1f))
                    Campo("Apellido", apellido, { apellido = it }, Modifier.weight(1f))
                }
                Campo("RUT", rut, { rut = it }, placeholder = "12.345.678-9")
                // Fecha de nacimiento con calendario (el campo no se escribe a mano)
                Box {
                    Campo(
                        "Fecha de nacimiento",
                        if (fechaNacimiento.isBlank()) "" else formatearFecha(fechaNacimiento),
                        {},
                        readOnly = true,
                        placeholder = "Toca para elegir",
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { mostrarCalendario = true },
                    )
                }
            }
            Campo("Correo electrónico", email, { email = it }, keyboardType = KeyboardType.Email)
            Campo(
                "Contraseña",
                password,
                { password = it },
                keyboardType = KeyboardType.Password,
                oculto = !verPassword,
                trailing = {
                    IconButton(onClick = { verPassword = !verPassword }) {
                        Icon(
                            if (verPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (verPassword) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = T.textMuted,
                        )
                    }
                },
            )
            if (modoRegistro) {
                Campo("Confirmar contraseña", confirmar, { confirmar = it }, keyboardType = KeyboardType.Password, oculto = !verPassword)
                Text("Mínimo 8 caracteres. Debes tener al menos 15 años.", fontFamily = Outfit, fontSize = 11.sp, color = T.textMuted)
            }

            Spacer(Modifier.height(4.dp))
            Button(
                onClick = ::enviar,
                enabled = !cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = T.coral),
            ) {
                if (cargando) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        if (modoRegistro) "Crear cuenta" else "Iniciar sesión",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                    )
                }
            }
            TextButton(
                onClick = {
                    modoRegistro = !modoRegistro
                    error = null
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(
                    if (modoRegistro) "¿Ya tienes cuenta? Inicia sesión" else "¿No tienes cuenta? Regístrate gratis",
                    fontFamily = Outfit,
                    fontSize = 13.sp,
                    color = T.coral,
                )
            }
            if (cargando) {
                Text(
                    "Si el servidor estaba inactivo, la primera conexión puede tardar hasta un minuto.",
                    fontFamily = Outfit,
                    fontSize = 11.sp,
                    color = T.textSubtle,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
        }
    }

    if (mostrarCalendario) {
        val pickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        val c = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }
                        fechaNacimiento = "%04d-%02d-%02d".format(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
                    }
                    mostrarCalendario = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") } },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun Campo(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Text,
    oculto: Boolean = false,
    readOnly: Boolean = false,
    placeholder: String? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val T = LocalAppColors.current
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, fontFamily = Outfit) },
        placeholder = placeholder?.let { { Text(it, fontFamily = Outfit, color = T.textSubtle) } },
        singleLine = true,
        readOnly = readOnly,
        visualTransformation = if (oculto) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon = trailing,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = T.textPrimary,
            unfocusedTextColor = T.textPrimary,
            focusedContainerColor = T.inputBg,
            unfocusedContainerColor = T.inputBg,
            focusedBorderColor = T.coral,
            unfocusedBorderColor = T.inputBorder,
            focusedLabelColor = T.coral,
            unfocusedLabelColor = T.textMuted,
            cursorColor = T.coral,
        ),
        modifier = modifier,
    )
}
