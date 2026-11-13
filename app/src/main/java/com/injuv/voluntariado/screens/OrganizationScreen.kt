package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.injuv.voluntariado.components.MenuIconButton
import com.injuv.voluntariado.components.NotificationIconButton
import com.injuv.voluntariado.data.OrgProfile
import com.injuv.voluntariado.data.SampleData
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun OrganizationScreen(
    org: OrgProfile,
    onEdit: () -> Unit,
    onShowAlert: () -> Unit,
    onToggleMenu: () -> Unit,
    designations: Map<Int, Set<String>> = emptyMap(),
    onAssignTutor: (Int) -> Unit = {},
    onViewAttendance: (Int) -> Unit = {},
    resolveName: (String) -> String = { it },
) {
    val T = LocalAppColors.current
    val displayName = org.name.ifBlank { "Mi organización" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(T.bgHeaderGradient))
                    .padding(horizontal = 12.dp, vertical = 16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    MenuIconButton(onClick = onToggleMenu)
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                displayName.first().uppercase(),
                                fontFamily = SpaceGrotesk,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                color = Color.White,
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(displayName, fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        Text("Perfil de organización", fontFamily = Outfit, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        Spacer(Modifier.height(18.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OrgStat("${SampleData.opportunities.size}", "Programas")
                            OrgStat("${SampleData.tutorRoster.size}", "Voluntarios")
                            OrgStat("${SampleData.news.size}", "Noticias")
                        }
                        Spacer(Modifier.height(14.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.18f))
                                .clickable(onClick = onEdit)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Editar organización",
                                fontFamily = SpaceGrotesk,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = Color.White,
                            )
                        }
                    }
                    NotificationIconButton(onClick = onShowAlert)
                }
            }
        }

        if (org.email.isNotBlank() || org.phone.isNotBlank() || org.website.isNotBlank()) {
            item {
                OrgInfoCard("Información de contacto") {
                    if (org.email.isNotBlank()) OrgContactRow(Icons.Filled.Email, org.email)
                    if (org.phone.isNotBlank()) OrgContactRow(Icons.Filled.Phone, org.phone)
                    if (org.website.isNotBlank()) OrgContactRow(Icons.Filled.Language, org.website)
                }
            }
        }

        if (org.about.isNotBlank()) {
            item {
                OrgInfoCard("Acerca de la organización") {
                    Text(org.about, fontFamily = Outfit, fontSize = 13.sp, color = T.textSecondary)
                }
            }
        }

        item {
            Text(
                "Oportunidades publicadas",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = T.textMuted,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp),
            )
        }

        items(SampleData.opportunities) { opportunity ->
            val assigned = designations[opportunity.id].orEmpty()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(T.bgCardGradient))
                    .clickable { onAssignTutor(opportunity.id) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        opportunity.title,
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = T.textPrimary,
                    )
                    Text(
                        "${opportunity.hours}h · ${opportunity.spots} cupos",
                        fontFamily = Outfit,
                        fontSize = 11.sp,
                        color = T.textMuted,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (assigned.isEmpty()) "Sin tutor designado — toca para asignar"
                        else "Tutores: ${assigned.map(resolveName).joinToString(", ")}",
                        fontFamily = Outfit,
                        fontSize = 11.sp,
                        color = if (assigned.isEmpty()) T.textSubtle else T.green,
                    )
                }
                IconButton(onClick = { onViewAttendance(opportunity.id) }) {
                    Icon(Icons.Filled.FactCheck, contentDescription = "Ver asistencia", tint = T.coral, modifier = Modifier.size(20.dp))
                }
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = T.textSubtle)
            }
        }
    }
}

@Composable
private fun OrgStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFFFFD60A))
        Text(label, fontFamily = Outfit, fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
private fun OrgInfoCard(title: String, content: @Composable () -> Unit) {
    val T = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(title, fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = T.textPrimary)
        content()
    }
}

@Composable
private fun OrgContactRow(icon: ImageVector, value: String) {
    val T = LocalAppColors.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = T.coral, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(10.dp))
        Text(value, fontFamily = Outfit, fontSize = 13.sp, color = T.textSecondary)
    }
}
