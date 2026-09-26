package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.components.FilterChipsRow
import com.injuv.voluntariado.data.NewsItem
import com.injuv.voluntariado.data.SampleData
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun NewsScreen() {
    val T = LocalAppColors.current
    var selectedCategory by remember { mutableStateOf("Todas") }

    val filtered = if (selectedCategory == "Todas") SampleData.news
    else SampleData.news.filter { it.category == selectedCategory }

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
                Column {
                    Text("Noticias", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
                    Spacer(Modifier.height(2.dp))
                    Text("Novedades del programa de voluntariado", fontFamily = Outfit, fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
                    Spacer(Modifier.height(16.dp))
                    FilterChipsRow(
                        options = SampleData.newsCategories,
                        selected = selectedCategory,
                        onSelect = { selectedCategory = it },
                    )
                }
            }
        }

        items(filtered) { news ->
            NewsCard(news, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
        }
    }
}

@Composable
private fun NewsCard(news: NewsItem, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient)),
    ) {
        AsyncImage(
            model = news.imageUrl,
            contentDescription = news.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (news.featured) 170.dp else 120.dp),
        )
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                news.category.uppercase(),
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                color = T.coral,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                news.title,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = if (news.featured) 16.sp else 14.sp,
                color = T.textPrimary,
            )
            Spacer(Modifier.height(4.dp))
            Text(news.excerpt, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted, maxLines = 2)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(news.date, fontFamily = Outfit, fontSize = 11.sp, color = T.textSubtle)
                Text("${news.readTime} de lectura", fontFamily = Outfit, fontSize = 11.sp, color = T.textSubtle)
            }
        }
    }
}
