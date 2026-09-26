package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.LinearProgressIndicator
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
import com.injuv.voluntariado.data.ActiveProgram
import com.injuv.voluntariado.data.HistoryProgram
import com.injuv.voluntariado.data.SampleData
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun ProfileScreen() {
    val T = LocalAppColors.current
    var activeTab by remember { mutableStateOf("activas") } // "activas" | "historial"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(T.bgHeaderGradient))
                    .padding(20.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("F", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 28.sp, color = Color.White)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("Francisco", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                    Text("Voluntario desde 2024", fontFamily = Outfit, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))

                    Spacer(Modifier.height(18.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ProfileStat("178h", "Acumuladas")
                        ProfileStat("6", "Programas")
                        ProfileStat("4", "Certificados")
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .clip(RoundedCornerShape(50))
                    .background(T.surface1),
            ) {
                ProfileTab("Activas", isSelected = activeTab == "activas", modifier = Modifier
                    .weight(1f)
                    .clickable { activeTab = "activas" })
                ProfileTab("Historial", isSelected = activeTab == "historial", modifier = Modifier
                    .weight(1f)
                    .clickable { activeTab = "historial" })
            }
        }

        if (activeTab == "activas") {
            items(SampleData.activePrograms) { program ->
                ActiveProgramCard(program, modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
            }
        } else {
            items(SampleData.historyPrograms) { program ->
                HistoryProgramCard(program, modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
            }
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFFFFD60A))
        Text(label, fontFamily = Outfit, fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
private fun ProfileTab(label: String, isSelected: Boolean, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) Brush.linearGradient(listOf(T.coral, T.coralDark)) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)))
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            fontFamily = SpaceGrotesk,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            fontSize = 13.sp,
            color = if (isSelected) Color.White else T.textMuted,
        )
    }
}

@Composable
private fun ActiveProgramCard(program: ActiveProgram, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .padding(16.dp),
    ) {
        Text(program.title, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
        Text(program.org, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { program.progress / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50)),
            color = T.coral,
            trackColor = T.progressTrack,
        )
        Spacer(Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${program.hours}/${program.totalHours} horas", fontFamily = Outfit, fontSize = 11.sp, color = T.textMuted)
            Text("Cierra ${program.deadline}", fontFamily = Outfit, fontSize = 11.sp, color = T.coral)
        }
    }
}

@Composable
private fun HistoryProgramCard(program: HistoryProgram, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(program.title, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
            Text("${program.org} · ${program.year}", fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(program.rating) {
                    androidx.compose.material3.Icon(Icons.Filled.Star, contentDescription = null, tint = T.yellow, modifier = Modifier.size(13.dp))
                }
                Spacer(Modifier.width(8.dp))
                Text("${program.hours}h", fontFamily = Outfit, fontSize = 11.sp, color = T.textMuted)
            }
        }
        if (program.certificate) {
            androidx.compose.material3.Icon(Icons.Filled.Verified, contentDescription = "Certificado", tint = T.green)
        }
    }
}
