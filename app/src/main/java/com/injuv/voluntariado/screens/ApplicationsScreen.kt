package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.components.AppScreenHeader
import com.injuv.voluntariado.components.FilterChipsRow
import com.injuv.voluntariado.data.Opportunity
import com.injuv.voluntariado.data.SampleData
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun ApplicationsScreen(
    onOpenDetail: (Int) -> Unit,
    onShowAlert: () -> Unit,
    onToggleMenu: () -> Unit,
) {
    val T = LocalAppColors.current
    var selectedCategory by remember { mutableStateOf("Todos") }

    val filtered = if (selectedCategory == "Todos") {
        SampleData.opportunities
    } else {
        SampleData.opportunities.filter { it.tags.contains(selectedCategory) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            AppScreenHeader(
                title = "Postulaciones",
                subtitle = "${filtered.size} oportunidades disponibles",
                onNotificationClick = onShowAlert,
                onMenuClick = onToggleMenu,
                bottomContent = {
                    FilterChipsRow(
                        options = SampleData.categories,
                        selected = selectedCategory,
                        onSelect = { selectedCategory = it },
                    )
                },
            )
        }

        items(filtered) { opportunity ->
            OpportunityListCard(
                opportunity = opportunity,
                onClick = { onOpenDetail(opportunity.id) },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun OpportunityListCard(opportunity: Opportunity, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .clickable(onClick = onClick)
            .padding(12.dp),
    ) {
        AsyncImage(
            model = opportunity.imageUrl,
            contentDescription = opportunity.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(12.dp)),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                opportunity.title,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = T.textPrimary,
                maxLines = 1,
            )
            Text(opportunity.org, fontFamily = Outfit, fontSize = 12.sp, color = T.textMuted, maxLines = 1)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = T.textMuted, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(3.dp))
                Text(opportunity.location, fontFamily = Outfit, fontSize = 11.sp, color = T.textMuted)
                Spacer(Modifier.width(10.dp))
                Icon(Icons.Filled.People, contentDescription = null, tint = T.textMuted, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(3.dp))
                Text("${opportunity.spots} cupos", fontFamily = Outfit, fontSize = 11.sp, color = T.textMuted)
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = T.coral, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(3.dp))
                Text("Cierra: ${opportunity.deadline}", fontFamily = Outfit, fontSize = 11.sp, color = T.coral, fontWeight = FontWeight.Medium)
            }
        }
    }
}
