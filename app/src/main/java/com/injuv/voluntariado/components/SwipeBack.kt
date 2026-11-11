package com.injuv.voluntariado.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown

/** Deslizar desde el borde izquierdo para volver atrás. */
fun Modifier.swipeToGoBack(enabled: Boolean, onBack: () -> Unit): Modifier = composed {
    val edgePx = with(LocalDensity.current) { 28.dp.toPx() }
    val threshold = with(LocalDensity.current) { 72.dp.toPx() }
    pointerInput(enabled) {
        if (!enabled) return@pointerInput
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            if (down.position.x > edgePx) return@awaitEachGesture
            var total = 0f
            while (true) {
                val event = awaitPointerEvent()
                val change = event.changes.firstOrNull() ?: break
                if (!change.pressed) {
                    if (total > threshold) onBack()
                    break
                }
                total += change.positionChange().x
            }
        }
    }
}
