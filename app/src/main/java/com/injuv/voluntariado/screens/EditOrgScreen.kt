package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.injuv.voluntariado.data.OrgProfile
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun EditOrgScreen(
    org: OrgProfile,
    onSave: (OrgProfile) -> Unit,
    onBack: () -> Unit,
) {
    val T = LocalAppColors.current
    var name by remember { mutableStateOf(org.name) }
    var email by remember { mutableStateOf(org.email) }
    var phone by remember { mutableStateOf(org.phone) }
    var website by remember { mutableStateOf(org.website) }
    var about by remember { mutableStateOf(org.about) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase),
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
                    "Editar organización",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White,
                )
                Text(
                    "Datos visibles para voluntarios",
                    fontFamily = Outfit,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            OrgField("Nombre de la organización", name, { name = it }, "Ej: Fundación Ayuda Chile")
            Spacer(Modifier.height(14.dp))
            OrgField("Correo de contacto", email, { email = it }, "contacto@miorg.cl")
            Spacer(Modifier.height(14.dp))
            OrgField("Teléfono", phone, { phone = it }, "+56 9 1234 5678")
            Spacer(Modifier.height(14.dp))
            OrgField("Sitio web", website, { website = it }, "www.miorg.cl")
            Spacer(Modifier.height(14.dp))
            OrgField("Descripción", about, { about = it }, "A qué se dedica tu organización", minLines = 4)
        }

        Button(
            onClick = {
                onSave(
                    OrgProfile(
                        name = name.trim(),
                        about = about.trim(),
                        email = email.trim(),
                        phone = phone.trim(),
                        website = website.trim(),
                    ),
                )
            },
            enabled = name.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = T.coral,
                disabledContainerColor = T.textSubtle.copy(alpha = 0.3f),
            ),
        ) {
            Text(
                "Guardar cambios",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
        }
    }
}

@Composable
private fun OrgField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1,
) {
    val T = LocalAppColors.current
    Column {
        Text(
            label,
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = T.textPrimary,
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            placeholder = {
                Text(placeholder, fontFamily = Outfit, fontSize = 13.sp, color = T.textSubtle)
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = T.coral,
                unfocusedBorderColor = T.textSubtle.copy(alpha = 0.4f),
                cursorColor = T.coral,
                focusedTextColor = T.textPrimary,
                unfocusedTextColor = T.textPrimary,
                focusedPlaceholderColor = T.textSubtle,
                unfocusedPlaceholderColor = T.textSubtle,
            ),
        )
    }
}
