package com.react.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.react.app.data.database.State
import com.react.app.utils.isDarkTheme
import com.react.app.utils.triggerHapticAndSound
import kotlinx.coroutines.launch

@Composable
fun StateScreen(
    states: List<State>,
    onStateSelected: (Int) -> Unit,
    onOpenStats: () -> Unit
) {
    var longPressTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(longPressTriggered) {
        if (longPressTriggered) {
            onOpenStats()
            longPressTriggered = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { longPressTriggered = true })
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            for (row in 0..3) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    for (col in 0..1) {
                        val index = row * 2 + col
                        val state = states.getOrNull(index)
                        if (state != null) {
                            StateButton(
                                state = state,
                                onClick = { onStateSelected(state.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StateButton(
    state: State,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val isDark = isDarkTheme(context)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .background(
                color = if (isDark) Color(0xFF1E1E1E) else Color(0xFFFFFFFF),
                shape = RoundedCornerShape(12.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        scope.launch {
                            context.triggerHapticAndSound()
                            scale.animateTo(0.95f, animationSpec = tween(100))
                            scale.animateTo(1f, animationSpec = tween(100))
                            onClick()
                        }
                    }
                )
            }
            .scale(scale.value),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = state.emoji,
            fontSize = 72.sp,
            textAlign = TextAlign.Center
        )
    }
}
