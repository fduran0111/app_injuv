package com.injuv.voluntariado.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.injuv.voluntariado.components.FilterChipsRow
import com.injuv.voluntariado.data.Book
import com.injuv.voluntariado.data.SampleData
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

@Composable
fun LibraryScreen() {
    val T = LocalAppColors.current
    var selectedCategory by remember { mutableStateOf("Todos") }

    val filtered = if (selectedCategory == "Todos") SampleData.books
    else SampleData.books.filter { it.category == selectedCategory }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase),
        contentPadding = PaddingValues(bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(T.bgHeaderGradient))
                    .padding(20.dp),
            ) {
                Column {
                    Text("Biblioteca", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
                    Spacer(Modifier.height(2.dp))
                    Text("Lecturas recomendadas sobre voluntariado", fontFamily = Outfit, fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
                    Spacer(Modifier.height(16.dp))
                    FilterChipsRow(
                        options = SampleData.bookCategories,
                        selected = selectedCategory,
                        onSelect = { selectedCategory = it },
                    )
                }
            }
        }

        items(filtered) { book ->
            BookCard(book, modifier = Modifier.padding(horizontal = 6.dp))
        }
    }
}

@Composable
private fun BookCard(book: Book, modifier: Modifier = Modifier) {
    val T = LocalAppColors.current
    var saved by remember { mutableStateOf(book.savedDefault) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(T.bgCardGradient))
            .padding(10.dp),
    ) {
        Box {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(10.dp)),
            )
            IconButton(
                onClick = { saved = !saved },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Color.Black.copy(alpha = 0.35f))
                    .size(30.dp),
            ) {
                Icon(
                    if (saved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = "Guardar",
                    tint = if (saved) T.yellow else Color.White,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(book.title, fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = T.textPrimary, maxLines = 2)
        Text(book.author, fontFamily = Outfit, fontSize = 11.sp, color = T.textMuted, maxLines = 1)
        Spacer(Modifier.height(4.dp))
        Text("${book.year} · ${book.pages}p.", fontFamily = Outfit, fontSize = 10.sp, color = T.textSubtle)
    }
}
