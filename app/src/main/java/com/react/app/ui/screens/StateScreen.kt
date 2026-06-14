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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.react.app.data.database.State
import com.react.app.utils.triggerHapticAndSound
import kotlinx.coroutines.launch

@Composable
fun StateScreen(
    states: List<State>,
    onStateSelected: (Int) -> Unit,
    onOpenStats: () -> Unit,
    repository: ReactRepository
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var longPressTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(longPressTriggered) {
        if (longPressTriggered) {
            onOpenStats()
            longPressTriggered = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        longPressTriggered = true
                    },
                    onTap = { }
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "React",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        // 8 state buttons in 2 columns x 4 rows
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            for (row in 0 until 4) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    val state1 = states[row * 2]
                    val state2 = states[row * 2 + 1]

                    StateButton(state1, onStateSelected, scope, context)
                    StateButton(state2, onStateSelected, scope, context)
                }
            }
        }
    }
}

@Composable
fun StateButton(
    state: State,
    onStateSelected: (Int) -> Unit,
    scope: androidx.compose.runtime.CoroutineScope,
    context: android.content.Context
) {
    val scale = remember { Animatable(1f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .background(
                color = if (isDarkTheme(context)) Color(0xFF1E1E1E) else Color(0xFFFFFFFF),
                shape = RoundedCornerShape(12.dp)
            )
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        scope.launch {
                            context.triggerHapticAndSound()
                            scale.animateTo(0.95f, animationSpec = tween(100))
                            scale.animateTo(1f, animationSpec = tween(100))
                            onStateSelected(state.id)
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = state.emoji,
            fontSize = 72.sp,
            textAlign = TextAlign.Center
        )
    }
}

private fun isDarkTheme(context: android.content.Context): Boolean {
    val resources = context.resources
    val config = resources.configuration
    return config.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES
}
