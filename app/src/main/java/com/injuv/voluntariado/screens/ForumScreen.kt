package com.injuv.voluntariado.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.injuv.voluntariado.components.coloresTono
import com.injuv.voluntariado.components.Tono
import com.injuv.voluntariado.components.Etiqueta
import com.injuv.voluntariado.components.Avatar
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.injuv.voluntariado.components.Cargando
import com.injuv.voluntariado.components.ErrorCarga
import com.injuv.voluntariado.components.Vacio
import com.injuv.voluntariado.data.ForumMessage
import com.injuv.voluntariado.components.SubHeader
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.InstrumentSans
import com.injuv.voluntariado.ui.theme.Bricolage
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ForumScreen(
    programTitle: String,
    messages: List<ForumMessage>,
    canWrite: Boolean,
    headerTitle: String = "Foro de anuncios",
    badge: String? = "TUTOR",
    placeholder: String = "Escribe un anuncio...",
    readOnlyText: String = "Solo lectura — únicamente el tutor puede publicar anuncios",
    onSend: (String) -> Unit = {},
    canPin: Boolean = false,
    onToggleFijar: (ForumMessage) -> Unit = {},
    onBack: () -> Unit,
    isLoading: Boolean = false,
    loadError: String? = null,
    onRetry: () -> Unit = {},
    emptyTitle: String = "Aún no hay mensajes",
    emptyDescription: String? = "Cuando haya actividad en este foro, aparecerá aquí.",
) {
    val T = LocalAppColors.current
    var draft by remember { mutableStateOf("") }
    var pinDialog by remember { mutableStateOf<ForumMessage?>(null) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val fijado = messages.lastOrNull { it.fijado }

    // Insets del teclado: el Scaffold ya aplica navigationBars abajo, así que solo se
    // necesita el excedente del teclado sobre la nav bar (ime - navBars). Cuando el
    // teclado está cerrado el excedente es 0 y queda solo el inset de navegación.
    val density = LocalDensity.current
    val tecladoExtraPx = (WindowInsets.ime.getBottom(density) - WindowInsets.navigationBars.getBottom(density))
        .coerceAtLeast(0)
    val tecladoExtra = with(density) { tecladoExtraPx.toDp() }

    // Auto-scroll inteligente: el estado se reinicia si cambia el foro (primer mensaje distinto).
    val primeraClave = messages.firstOrNull()?.id ?: -1
    val cercaDelFinal by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val total = info.totalItemsCount
            total == 0 || (info.visibleItemsInfo.lastOrNull()?.index ?: 0) >= total - 2
        }
    }
    var scrollInicialHecho by remember(primeraClave) { mutableStateOf(false) }
    var ultimoTamano by remember(primeraClave) { mutableIntStateOf(-1) }
    var nuevosAbajo by remember(primeraClave) { mutableIntStateOf(0) }

    LaunchedEffect(messages.size, primeraClave) {
        val size = messages.size
        if (size == 0) return@LaunchedEffect
        if (!scrollInicialHecho) {
            // Al abrir: posicionar en el mensaje más reciente.
            listState.scrollToItem(size - 1)
            scrollInicialHecho = true
            ultimoTamano = size
            return@LaunchedEffect
        }
        if (size > ultimoTamano) {
            val delta = size - ultimoTamano
            if (cercaDelFinal) {
                listState.animateScrollToItem(size - 1)
                nuevosAbajo = 0
            } else {
                nuevosAbajo += delta
            }
        }
        ultimoTamano = size
    }
    // Si el usuario llega al fondo por su cuenta, limpiar el contador de nuevos.
    LaunchedEffect(cercaDelFinal) {
        if (cercaDelFinal) nuevosAbajo = 0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase)
            .padding(bottom = tecladoExtra),
    ) {
        SubHeader(
            titulo = headerTitle,
            onBack = onBack,
            subtitulo = programTitle,
        )

        // Mensaje fijado por el tutor: visible arriba del chat; al tocarlo baja hasta el mensaje.
        if (fijado != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(coloresTono(Tono.Ambar, T).fondo)
                    .clickable {
                        val i = messages.indexOfFirst { it.id == fijado.id }
                        if (i >= 0) scope.launch { listState.animateScrollToItem(i) }
                    }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.PushPin, contentDescription = null, tint = coloresTono(Tono.Ambar, T).texto, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Mensaje fijado",
                        fontFamily = InstrumentSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = coloresTono(Tono.Ambar, T).texto,
                    )
                    Text(
                        "${fijado.author} · ${fijado.text}",
                        fontFamily = InstrumentSans,
                        fontSize = 12.sp,
                        color = T.textSecondary,
                        maxLines = 1,
                    )
                }
            }
        }

        // Refresco fallido con datos visibles: aviso discreto + reintento, sin borrar la lista.
        if (loadError != null && messages.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(T.surface1)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.CloudOff, contentDescription = null, tint = T.textMuted, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Sin conexión. Mostrando la última información disponible.",
                    fontFamily = InstrumentSans,
                    fontSize = 11.sp,
                    color = T.textMuted,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = onRetry) {
                    Text("Reintentar", fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = T.action)
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when {
                isLoading && messages.isEmpty() -> Cargando(modifier = Modifier.fillMaxSize())
                loadError != null && messages.isEmpty() ->
                    ErrorCarga(loadError, onReintentar = onRetry, modifier = Modifier.fillMaxSize())
                messages.isEmpty() -> Vacio(emptyTitle, emptyDescription, modifier = Modifier.fillMaxSize())
                else -> LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(messages) { message ->
                        Row(verticalAlignment = Alignment.Top) {
                            Avatar(message.author, size = 36.dp)
                            Spacer(Modifier.width(10.dp))
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
                                    .background(T.card)
                                    .border(
                                        1.dp,
                                        if (message.fijado) T.ambar.copy(alpha = 0.6f) else T.border1,
                                        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
                                    )
                                    .then(
                                        if (canPin) Modifier.combinedClickable(
                                            onClick = {},
                                            onLongClick = { pinDialog = message },
                                        ) else Modifier,
                                    )
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        message.author,
                                        fontFamily = InstrumentSans,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = T.textPrimary,
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f, fill = false),
                                    )
                                    if (badge != null) {
                                        Spacer(Modifier.width(8.dp))
                                        Etiqueta(badge.lowercase().replaceFirstChar { it.uppercase() }, Tono.Ambar)
                                    }
                                    Spacer(Modifier.weight(1f))
                                    if (message.fijado) {
                                        Icon(
                                            Icons.Filled.PushPin,
                                            contentDescription = "Fijado",
                                            tint = T.ambar,
                                            modifier = Modifier.size(13.dp),
                                        )
                                        Spacer(Modifier.width(4.dp))
                                    }
                                    Text(
                                        message.date,
                                        fontFamily = InstrumentSans,
                                        fontSize = 11.sp,
                                        color = T.textMuted,
                                    )
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    message.text,
                                    fontFamily = InstrumentSans,
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp,
                                    color = T.textSecondary,
                                )
                            }
                        }
                    }
                }
            }

            // Atajo visible para bajar a los mensajes nuevos sin perder la posición de lectura.
            if (nuevosAbajo > 0) {
                Surface(
                    onClick = {
                        scope.launch { listState.animateScrollToItem(messages.lastIndex) }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(50),
                    color = T.action,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            if (nuevosAbajo == 1) "1 mensaje nuevo" else "$nuevosAbajo mensajes nuevos",
                            fontFamily = InstrumentSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color.White,
                        )
                    }
                }
            }
        }

        if (canWrite) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(T.bgBase)
                    .border(1.dp, T.border1)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    placeholder = {
                        Text(
                            placeholder,
                            fontFamily = InstrumentSans,
                            fontSize = 13.sp,
                            color = T.textMuted,
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = T.action,
                        unfocusedBorderColor = T.border2,
                        focusedContainerColor = T.card,
                        unfocusedContainerColor = T.card,
                        cursorColor = T.action,
                        focusedTextColor = T.textPrimary,
                        unfocusedTextColor = T.textPrimary,
                    ),
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (draft.isNotBlank()) {
                            onSend(draft.trim())
                            draft = ""
                            // Tras enviar, bajar al final para ver la propia respuesta.
                            if (messages.isNotEmpty()) {
                                scope.launch { listState.animateScrollToItem(messages.lastIndex) }
                            }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(T.action),
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Enviar",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = T.textSubtle, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    readOnlyText,
                    fontFamily = InstrumentSans,
                    fontSize = 11.sp,
                    color = T.textMuted,
                )
            }
        }
    }

    pinDialog?.let { m ->
        AlertDialog(
            onDismissRequest = { pinDialog = null },
            title = { Text(if (m.fijado) "Desfijar mensaje" else "Fijar mensaje", fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold) },
            text = {
                Text(
                    if (m.fijado) "Se quitará de la parte superior del foro."
                    else "Se mostrará fijado arriba del foro para todos los voluntarios.",
                    fontFamily = InstrumentSans,
                )
            },
            confirmButton = {
                TextButton(onClick = { onToggleFijar(m); pinDialog = null }) {
                    Text(if (m.fijado) "Desfijar" else "Fijar", color = T.coral)
                }
            },
            dismissButton = {
                TextButton(onClick = { pinDialog = null }) { Text("Cancelar") }
            },
        )
    }
}
