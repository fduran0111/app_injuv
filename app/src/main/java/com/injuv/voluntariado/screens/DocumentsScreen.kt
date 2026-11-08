package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.injuv.voluntariado.components.FilterChipsRow
import com.injuv.voluntariado.data.DocumentItem
import com.injuv.voluntariado.data.SampleData
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun DocumentsScreen(onBack: (() -> Unit)? = null) {
    val T = LocalAppColors.current
    var selectedCategory by remember { mutableStateOf("Todos") }

    val filtered = if (selectedCategory == "Todos") SampleData.documents
    else SampleData.documents.filter { it.category == selectedCategory }

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (onBack != null) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                            }
                        }
                        Text("Documentos", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
                    }
                    Spacer(Modifier.height(2.dp))
                    Text("Reglamentos, formularios y guías descargables", fontFamily = Outfit, fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
                    Spacer(Modifier.height(16.dp))
                    FilterChipsRow(
                        options = SampleData.documentCategories,
                        selected = selectedCategory,
                        onSelect = { selectedCategory = it },
                    )
                }
            }
        }

        items(filtered) { doc ->
            DocumentCard(doc, modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
        }
    }
}

@Composable
private fun DocumentCard(doc: DocumentItem, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(T.blue.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Description, contentDescription = doc.type, tint = T.blue)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(doc.title, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = T.textPrimary, maxLines = 2)
            Spacer(Modifier.height(4.dp))
            Text("${doc.type} · ${doc.size} · ${doc.date}", fontFamily = Outfit, fontSize = 11.sp, color = T.textMuted)
            Text("${doc.downloads} descargas", fontFamily = Outfit, fontSize = 10.sp, color = T.textSubtle)
        }
        IconButton(onClick = { /* TODO: descargar documento desde el backend */ }) {
            Icon(Icons.Filled.Download, contentDescription = "Descargar", tint = T.coral)
        }
    }
}
