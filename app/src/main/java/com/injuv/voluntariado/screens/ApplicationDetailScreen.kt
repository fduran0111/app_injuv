package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.data.Opportunity
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun ApplicationDetailScreen(opportunity: Opportunity, onBack: () -> Unit) {
    val T = LocalAppColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase)
            .verticalScroll(rememberScrollState()),
    ) {
        Box {
            AsyncImage(
                model = opportunity.imageUrl,
                contentDescription = opportunity.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, T.bgBase))),
            )
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.35f)),
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Row {
                opportunity.tags.forEach { tag ->
                    Text(
                        tag,
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        color = T.coral,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(T.coral.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                opportunity.title,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = T.textPrimary,
            )
            Text(opportunity.org, fontFamily = Outfit, fontSize = 14.sp, color = T.textMuted)

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(T.bgCardGradient))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                DetailStat(Icons.Filled.LocationOn, opportunity.location, "Lugar")
                DetailStat(Icons.Filled.Schedule, "${opportunity.hours}h", "Duración")
                DetailStat(Icons.Filled.People, "${opportunity.spots}", "Cupos")
            }

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = T.textMuted, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(6.dp))
                Text(opportunity.dateRange, fontFamily = Outfit, fontSize = 13.sp, color = T.textSecondary)
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Descripción",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = T.textPrimary,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                opportunity.description,
                fontFamily = Outfit,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = T.textSecondary,
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = { /* TODO: conectar con backend FastAPI de postulaciones */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = T.coral),
            ) {
                Text(
                    "Postular ahora",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Cierre de postulaciones: ${opportunity.deadline}",
                fontFamily = Outfit,
                fontSize = 11.sp,
                color = T.textMuted,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun DetailStat(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    val T = LocalAppColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = T.coral, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = T.textPrimary)
        Text(label, fontFamily = Outfit, fontSize = 10.sp, color = T.textMuted)
    }
}
