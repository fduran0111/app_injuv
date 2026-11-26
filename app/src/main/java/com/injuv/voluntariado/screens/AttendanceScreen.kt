package com.injuv.voluntariado.screens

import android.app.DatePickerDialog
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.injuv.voluntariado.components.SubHeader
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.injuv.voluntariado.ui.theme.LocalAppColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.injuv.voluntariado.ui.theme.InstrumentSans
import com.injuv.voluntariado.ui.theme.Bricolage
import kotlinx.coroutines.launch

/** Un voluntario de la lista: [id] es el de su postulación. */
data class RosterEntry(val id: Int, val name: String)

@Composable
fun AttendanceScreen(
    programTitle: String,
    roster: List<RosterEntry>,
    initialDateKey: String,
    loadPresent: suspend (String) -> Set<Int>,
    initialPresent: Set<Int>,
    onSave: (String, Set<Int>) -> Unit,
    onBack: () -> Unit,
    readOnly: Boolean = false,
    saving: Boolean = false,
) {
    val T = LocalAppColors.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var dateKey by remember { mutableStateOf(initialDateKey) }
    var showDatePicker by remember { mutableStateOf(false) }
    var pendingDateKey by remember { mutableStateOf<String?>(null) }
    var present by remember(initialPresent) { mutableStateOf(initialPresent) }

    fun marcar(id: Int, presente: Boolean) {
        present = if (presente) present + id else present - id
    }

    fun labelFor(key: String): String {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(key) ?: return key
        return SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "CL")).format(date)
            .replaceFirstChar { it.uppercase() }
    }

    fun selectDate(key: String) {
        dateKey = key
        scope.launch { present = loadPresent(key) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase),
    ) {
        SubHeader(
            titulo = "Pasar lista",
            onBack = onBack,
            subtitulo = programTitle,
            extra = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .heightIn(min = 40.dp)
                        .clip(RoundedCornerShape(50))
                        .border(1.5.dp, T.border2, RoundedCornerShape(50))
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 12.dp),
                ) {
                    Icon(
                        Icons.Filled.CalendarToday,
                        contentDescription = "Elegir fecha",
                        tint = T.action,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        "  ${labelFor(dateKey)}",
                        fontFamily = InstrumentSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = T.textPrimary,
                    )
                }
            },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "${present.size} presentes",
                fontFamily = InstrumentSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = T.green,
            )
            Text(
                "${roster.size - present.size} ausentes",
                fontFamily = InstrumentSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = T.textMuted,
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
        ) {
            items(roster, key = { it.id }) { entry ->
                val name = entry.name
                val isPresent = entry.id in present
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(T.card)
                        .border(1.dp, T.border1, RoundedCornerShape(14.dp))
                        .then(
                            if (readOnly) {
                                Modifier
                            } else {
                                Modifier.clickable { marcar(entry.id, !isPresent) }
                            },
                        )
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isPresent) T.green.copy(alpha = 0.2f) else T.surface1),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            name.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString("").uppercase(),
                            fontFamily = InstrumentSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isPresent) T.green else T.textMuted,
                        )
                    }
                    Spacer(Modifier.size(12.dp))
                    Text(
                        name,
                        fontFamily = InstrumentSans,
                        fontSize = 14.sp,
                        color = T.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    if (isPresent) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = "Presente", tint = T.green, modifier = Modifier.size(22.dp))
                    } else {
                        Icon(Icons.Filled.Cancel, contentDescription = "Ausente", tint = T.textSubtle, modifier = Modifier.size(22.dp))
                    }
                    if (!readOnly) {
                        Spacer(Modifier.size(8.dp))
                        Checkbox(
                            checked = isPresent,
                            onCheckedChange = { checked -> marcar(entry.id, checked) },
                            colors = CheckboxDefaults.colors(checkedColor = T.green),
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            if (readOnly) {
                Text(
                    "Vista de solo lectura — el tutor registra la asistencia",
                    fontFamily = InstrumentSans,
                    fontSize = 11.sp,
                    color = T.textMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                Button(
                    onClick = { onSave(dateKey, present) },
                    enabled = !saving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = T.action),
                ) {
                    if (saving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else Text(
                        "Guardar lista",
                        fontFamily = InstrumentSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "Toca un nombre para marcarlo presente o ausente",
                    fontFamily = InstrumentSans,
                    fontSize = 11.sp,
                    color = T.textMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    if (showDatePicker) {
        val calendar = Calendar.getInstance().apply {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateKey)?.let { time = it }
        }
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val key = "%04d-%02d-%02d".format(year, month + 1, day)
                if (key == initialDateKey) {
                    selectDate(key)
                } else {
                    pendingDateKey = key
                }
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        ).show()
    }

    pendingDateKey?.let { key ->
        AlertDialog(
            onDismissRequest = { pendingDateKey = null },
            title = { Text("Modificar otra fecha", fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold) },
            text = { Text("Vas a modificar la lista del ${labelFor(key)}.", fontFamily = InstrumentSans) },
            confirmButton = {
                TextButton(onClick = { selectDate(key); pendingDateKey = null }) {
                    Text("Continuar", color = T.coral)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDateKey = null }) { Text("Cancelar") }
            },
        )
    }
}
