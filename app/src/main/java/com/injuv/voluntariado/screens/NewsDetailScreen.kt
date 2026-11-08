package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
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
import com.injuv.voluntariado.data.NewsItem
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun NewsDetailScreen(news: NewsItem, onBack: () -> Unit) {
    val T = LocalAppColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase)
            .verticalScroll(rememberScrollState()),
    ) {
        Box {
            AsyncImage(
                model = news.imageUrl.replace("w=300&h=180", "w=600&h=360"),
                contentDescription = news.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, T.bgBase))),
            )
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.35f)),
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                news.category.uppercase(),
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = T.coral,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(T.coral.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                news.title,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = T.textPrimary,
            )
            Spacer(Modifier.height(14.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = T.textMuted, modifier = Modifier.height(14.dp))
                    Spacer(Modifier.height(1.dp))
                    Text(news.date, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted, modifier = Modifier.padding(start = 6.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = T.textMuted, modifier = Modifier.height(14.dp))
                    Text("${news.readTime} de lectura", fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted, modifier = Modifier.padding(start = 6.dp))
                }
            }

            Spacer(Modifier.height(20.dp))

            val paragraphs = news.body.ifBlank { news.excerpt }.split("\n\n")
            paragraphs.forEach { paragraph ->
                Text(
                    paragraph,
                    fontFamily = Outfit,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = T.textSecondary,
                )
                Spacer(Modifier.height(14.dp))
            }
        }
    }
}
