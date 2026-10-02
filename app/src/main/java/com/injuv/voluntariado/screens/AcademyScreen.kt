package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.components.AppScreenHeader
import com.injuv.voluntariado.components.FilterChipsRow
import com.injuv.voluntariado.data.Course
import com.injuv.voluntariado.data.SampleData
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun AcademyScreen(onShowAlert: () -> Unit, onToggleMenu: () -> Unit) {
    val T = LocalAppColors.current
    var selectedLevel by remember { mutableStateOf("Todos") }

    val filtered = if (selectedLevel == "Todos") SampleData.courses
    else SampleData.courses.filter { it.level == selectedLevel }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            AppScreenHeader(
                title = "Academia",
                subtitle = "Cursos para potenciar tu voluntariado",
                onNotificationClick = onShowAlert,
                onMenuClick = onToggleMenu,
                bottomContent = {
                    FilterChipsRow(
                        options = SampleData.courseLevels,
                        selected = selectedLevel,
                        onSelect = { selectedLevel = it },
                    )
                },
            )
        }

        items(filtered) { course ->
            CourseCard(course, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
        }
    }
}

@Composable
private fun CourseCard(course: Course, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient)),
    ) {
        Box {
            AsyncImage(
                model = course.imageUrl,
                contentDescription = course.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
            )
            if (course.enrolledDefault) {
                Icon(
                    Icons.Filled.PlayCircle,
                    contentDescription = "En curso",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp),
                )
            }
        }
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(course.level, fontFamily = SpaceGrotesk, fontWeight = FontWeight.Medium, fontSize = 10.sp, color = T.coral)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = T.yellow, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(3.dp))
                    Text("${course.rating}", fontFamily = Outfit, fontSize = 11.sp, color = T.textMuted)
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(course.title, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = T.textPrimary)
            Text(course.instructor, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted)
            Spacer(Modifier.height(8.dp))
            Text("${course.duration} · ${course.modules} módulos · ${course.students} inscritos", fontFamily = Outfit, fontSize = 11.sp, color = T.textSubtle)

            if (course.enrolledDefault) {
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { course.progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                    color = T.green,
                    trackColor = T.progressTrack,
                )
                Spacer(Modifier.height(4.dp))
                Text("${course.progress}% completado", fontFamily = Outfit, fontSize = 11.sp, color = T.green)
            }
        }
    }
}
