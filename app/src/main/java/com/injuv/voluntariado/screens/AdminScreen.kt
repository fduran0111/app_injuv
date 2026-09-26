package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.data.Applicant
import com.injuv.voluntariado.data.ProgramCapacity
import com.injuv.voluntariado.data.SampleData
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun AdminScreen(onBack: () -> Unit) {
    val T = LocalAppColors.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(T.bgHeaderGradient))
                    .padding(horizontal = 12.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                Column {
                    Text("Panel de administrador", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                    Text("Gestión de postulantes y cupos", fontFamily = Outfit, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        item {
            Text(
                "Postulantes pendientes",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = T.textPrimary,
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 10.dp),
            )
        }

        items(SampleData.pendingApplicants) { applicant ->
            ApplicantCard(applicant, modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
        }

        item {
            Text(
                "Cupos por programa",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = T.textPrimary,
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 10.dp),
            )
        }

        items(SampleData.programsCapacity) { program ->
            CapacityCard(program, modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
        }
    }
}

@Composable
private fun ApplicantCard(applicant: Applicant, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = applicant.avatarUrl,
            contentDescription = applicant.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(applicant.name, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = T.textPrimary)
            Text(applicant.program, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
            Text(applicant.date, fontFamily = Outfit, fontSize = 11.sp, color = T.textSubtle)
        }
        IconButton(
            onClick = { /* TODO: aceptar postulante vía API */ },
            modifier = Modifier
                .clip(CircleShape)
                .background(T.green.copy(alpha = 0.18f)),
        ) {
            Icon(Icons.Filled.Check, contentDescription = "Aceptar", tint = T.green)
        }
        Spacer(Modifier.width(6.dp))
        IconButton(
            onClick = { /* TODO: rechazar postulante vía API */ },
            modifier = Modifier
                .clip(CircleShape)
                .background(T.coral.copy(alpha = 0.18f)),
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Rechazar", tint = T.coral)
        }
    }
}

@Composable
private fun CapacityCard(program: ProgramCapacity, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .padding(14.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(program.name, fontFamily = SpaceGrotesk, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = T.textPrimary)
            Text("${program.enrolled}/${program.cap}", fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { program.enrolled.toFloat() / program.cap.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50)),
            color = T.blue,
            trackColor = T.progressTrack,
        )
    }
}
