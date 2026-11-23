package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.data.api.Oportunidad
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

/** Voluntariados donde la cuenta es tutora; al elegir uno se abre su panel de tutor. */
@Composable
fun TutorListScreen(
    tutorias: List<Oportunidad>,
    onOpen: (Int) -> Unit,
    onOpenSettings: () -> Unit,
) {
    val T = LocalAppColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(T.bgHeaderGradient))
                .padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Mis tutorías",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White,
                )
                Text(
                    "Elige un voluntariado para abrir su panel",
                    fontFamily = Outfit,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                )
            }
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Filled.Settings, contentDescription = "Configuración", tint = Color.White)
            }
        }

        if (tutorias.isEmpty()) {
            Text(
                "Aún no te designan como tutor de un voluntariado.",
                fontFamily = Outfit,
                fontSize = 13.sp,
                color = T.textMuted,
                modifier = Modifier.padding(24.dp),
            )
            return@Column
        }

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val grupos = tutorias
                .groupBy { it.organizacion_nombre.ifBlank { "Sin organización" } }
                .toSortedMap(String.CASE_INSENSITIVE_ORDER)
            grupos.forEach { (org, ops) ->
                item(key = "org_$org") {
                    Text(
                        org,
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = T.textMuted,
                    )
                }
                items(ops, key = { it.id }) { op ->
                    TutoriaCard(op, onClick = { onOpen(op.id) })
                }
            }
        }
    }
}

@Composable
private fun TutoriaCard(op: Oportunidad, onClick: () -> Unit) {
    val T = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(T.coral.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            if (op.tiene_imagen) {
                AsyncImage(
                    model = ApiClient.imagenOportunidad(op.id),
                    contentDescription = op.titulo,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Icon(Icons.Filled.Groups, contentDescription = null, tint = T.coral, modifier = Modifier.size(26.dp))
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            Text(
                op.titulo,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = T.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                op.organizacion_nombre,
                fontFamily = Outfit,
                fontSize = 12.sp,
                color = T.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "${op.horas_voluntariado ?: 0}h",
                fontFamily = Outfit,
                fontSize = 12.sp,
                color = T.textMuted,
            )
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = T.textSubtle)
    }
}
