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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.injuv.voluntariado.components.AppScreenHeader
import com.injuv.voluntariado.ui.theme.Bricolage
import com.injuv.voluntariado.components.Vacio
import com.injuv.voluntariado.components.TituloSeccion
import com.injuv.voluntariado.components.FlechaCircular
import com.injuv.voluntariado.components.Avatar
import com.injuv.voluntariado.components.Tarjeta
import com.injuv.voluntariado.data.ForumChannel
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.InstrumentSans

@Composable
fun ForumsScreen(
    forums: List<ForumChannel>,
    onOpenForum: (Int) -> Unit,
    onShowAlert: () -> Unit,
    onToggleMenu: () -> Unit,
) {
    val T = LocalAppColors.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            AppScreenHeader(
                title = "Foros de voluntariado",
                resaltado = "Foros",
                subtitle = "Anuncios y chat con tus organizaciones",
                onNotificationClick = onShowAlert,
                onMenuClick = onToggleMenu,
            )
        }

        if (forums.isEmpty()) {
            item {
                Vacio(
                    "Aún no tienes foros activos",
                    "Cuando quedes seleccionado en un voluntariado, aquí verás los anuncios y el chat con tu organización.",
                    icono = Icons.Outlined.Forum,
                )
            }
        } else {
            item {
                TituloSeccion("Tus canales", modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 8.dp))
            }
        }

        items(forums) { channel ->
            Tarjeta(
                onClick = { onOpenForum(channel.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Avatar(channel.org.ifBlank { channel.title }, size = 46.dp)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                    ) {
                        Text(
                            channel.title,
                            fontFamily = Bricolage,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = T.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            channel.org,
                            fontFamily = InstrumentSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = if (T.isDark) T.blue else T.action,
                            maxLines = 1,
                        )
                        channel.lastMessage?.let {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                it,
                                fontFamily = InstrumentSans,
                                fontSize = 13.sp,
                                color = T.textMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    FlechaCircular()
                }
            }
        }
    }
}
