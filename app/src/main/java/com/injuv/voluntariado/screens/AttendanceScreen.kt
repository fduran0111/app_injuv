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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AttendanceScreen(
    programTitle: String,
    roster: List<String>,
    initialDateKey: String,
    loadPresent: (String) -> Set<String>,
    savePresent: (String, Set<String>) -> Unit,
    onBack: () -> Unit,
    readOnly: Boolean = false,
) {
    val T = LocalAppColors.current
    val context = LocalContext.current
    var dateKey by remember { mutableStateOf(initialDateKey) }
    var showDatePicker by remember { mutableStateOf(false) }
    var present by remember { mutableStateOf(loadPresent(dateKey).ifEmpty { roster.toSet() }) }

    fun labelFor(key: String): String {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(key) ?: return key
        return SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "CL")).format(date)
            .replaceFirstChar { it.uppercase() }
    }

    fun selectDate(key: String) {
        dateKey = key
        present = loadPresent(key).ifEmpty { roster.toSet() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(T.bgBase),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(T.bgHeaderGradient))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Column {
                Text(
                    "Pasar lista",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White,
                )
                Text(programTitle, fontFamily = Outfit, fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showDatePicker = true },
                ) {
                    Icon(
                        Icons.Filled.CalendarToday,
                        contentDescription = "Elegir fecha",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(12.dp),
                    )
                    Text(
                        "  ${labelFor(dateKey)}",
                        fontFamily = Outfit,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f),
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "${present.size} presentes",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = T.green,
            )
            Text(
                "${roster.size - present.size} ausentes",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = T.textMuted,
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
        ) {
            items(roster) { name ->
                val isPresent = name in present
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(T.bgCardGradient))
                        .then(
                            if (readOnly) {
                                Modifier
                            } else {
                                Modifier.clickable {
                                    present = if (isPresent) present - name else present + name
                                }
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
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isPresent) T.green else T.textMuted,
                        )
                    }
                    Spacer(Modifier.size(12.dp))
                    Text(
                        name,
                        fontFamily = Outfit,
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
                            onCheckedChange = { checked ->
                                present = if (checked) present + name else present - name
                            },
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
                    fontFamily = Outfit,
                    fontSize = 11.sp,
                    color = T.textMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                Button(
                    onClick = { savePresent(dateKey, present) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = T.coral),
                ) {
                    Text(
                        "Guardar lista",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "Toca un nombre para marcarlo presente o ausente",
                    fontFamily = Outfit,
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
                selectDate("%04d-%02d-%02d".format(year, month + 1, day))
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        ).show()
    }
}
