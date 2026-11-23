package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.injuv.voluntariado.data.api.Encargado
import com.injuv.voluntariado.data.api.Usuario
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk
import kotlinx.coroutines.launch

/**
 * Designa tutores por correo (solo integrantes de la organización). Lee y guarda `encargados_ids`
 * en la API, así que es la misma lista que se edita en la web (formulario del voluntariado).
 */
@Composable
fun AssignTutorScreen(
    programTitle: String,
    loadAssigned: suspend () -> List<Encargado>,
    findAccount: suspend (String) -> Usuario,
    isMember: (Usuario) -> Boolean,
    onSave: suspend (List<Int>) -> Unit,
    onBack: () -> Unit,
) {
    val T = LocalAppColors.current
    val scope = rememberCoroutineScope()
    var emailQuery by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var searching by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    var assigned by remember { mutableStateOf<List<Encargado>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            assigned = loadAssigned()
        } catch (e: Exception) {
            error = e.message ?: "No se pudieron cargar los encargados"
        }
        loading = false
    }

    fun search() {
        val email = emailQuery.trim()
        if (email.isEmpty() || searching || loading) return
        scope.launch {
            searching = true
            error = null
            try {
                val cuenta = findAccount(email)
                if (assigned.any { it.id == cuenta.id }) {
                    error = "Esa cuenta ya es encargada de este voluntariado"
                } else if (!isMember(cuenta)) {
                    error = "${cuenta.nombreCompleto} no es integrante de tu organización. " +
                        "Agrégalo primero como miembro desde la plataforma web."
                } else {
                    assigned = assigned + Encargado(cuenta.id, cuenta.nombre, cuenta.apellido, cuenta.email, isMember(cuenta))
                    emailQuery = ""
                }
            } catch (e: Exception) {
                error = e.message ?: "No se pudo buscar el correo"
            }
            searching = false
        }
    }

    fun save() {
        if (saving || loading) return
        scope.launch {
            saving = true
            error = null
            try {
                onSave(assigned.map { it.id })
            } catch (e: Exception) {
                error = e.message ?: "No se pudo guardar la designación"
            }
            saving = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase)
            .imePadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(T.bgHeaderGradient))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Column {
                Text(
                    "Designar tutores",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White,
                )
                Text(
                    programTitle,
                    fontFamily = Outfit,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                )
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Correo del tutor",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = T.textPrimary,
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = emailQuery,
                    onValueChange = { emailQuery = it; error = null },
                    placeholder = {
                        Text("tutor@correo.cl", fontFamily = Outfit, fontSize = 13.sp, color = T.textSubtle)
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.Email, contentDescription = null, tint = T.textMuted, modifier = Modifier.size(18.dp))
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { search() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = T.coral,
                        unfocusedBorderColor = T.textSubtle.copy(alpha = 0.4f),
                        cursorColor = T.coral,
                        focusedTextColor = T.textPrimary,
                        unfocusedTextColor = T.textPrimary,
                    ),
                )
                Spacer(Modifier.width(10.dp))
                Button(
                    onClick = { search() },
                    enabled = !searching,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = T.action),
                ) {
                    if (searching) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Filled.Search, contentDescription = "Buscar", modifier = Modifier.size(18.dp))
                    }
                }
            }
            if (error != null) {
                Text(
                    error!!,
                    fontFamily = Outfit,
                    fontSize = 11.sp,
                    color = T.coral,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
            Text(
                "Si la cuenta no pertenece a la organización queda como tutor externo: conserva sus permisos de voluntario.",
                fontFamily = Outfit,
                fontSize = 11.sp,
                color = T.textMuted,
                modifier = Modifier.padding(top = 6.dp),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                if (loading) "Cargando tutores..." else "${assigned.size} tutores designados",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = T.coral,
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
        ) {
            items(assigned, key = { it.id }) { encargado ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(T.bgCardGradient))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            encargado.nombreCompleto,
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = T.textPrimary,
                        )
                        Text(
                            encargado.email,
                            fontFamily = Outfit,
                            fontSize = 11.sp,
                            color = T.textMuted,
                        )
                    }
                    val badgeColor = if (encargado.miembro) T.green else T.coral
                    Text(
                        if (encargado.miembro) "Miembro" else "Externo",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        color = badgeColor,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(badgeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                    IconButton(onClick = { assigned = assigned.filter { it.id != encargado.id } }) {
                        Icon(Icons.Filled.Close, contentDescription = "Quitar", tint = T.textSubtle, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        Button(
            onClick = { save() },
            enabled = !saving && !loading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = T.action),
        ) {
            if (saving) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text(
                    "Guardar designación",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                )
            }
        }
    }
}
