package com.react.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.react.app.data.database.Action
import com.react.app.data.repository.ReactRepository
import com.react.app.utils.isDarkTheme
import com.react.app.utils.triggerHapticAndSound
import kotlinx.coroutines.launch

@Composable
fun ActionScreen(
    @Suppress("UNUSED_PARAMETER") onBack: () -> Unit,
    onCloseApp: () -> Unit,
    repository: ReactRepository
) {
    var actions by remember { mutableStateOf<List<Action>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var excludeSet by remember { mutableStateOf<Set<Int>>(emptySet()) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            actions = repository.getWeightedRandomActions(excludeSet)
        }
    }

    val bgColor = if (isDarkTheme(context)) Color(0xFF121212) else Color(0xFFF5F5F5)
    val cardBg = if (isDarkTheme(context)) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)
    val textColor = if (isDarkTheme(context)) Color(0xFFFFFFFF) else Color(0xFF333333)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                context.triggerHapticAndSound()
                onCloseApp()
            }) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "Stats",
                    tint = textColor
                )
            }

            Spacer(modifier = Modifier.width(48.dp))

            IconButton(onClick = {
                context.triggerHapticAndSound()
                showAddDialog = true
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add action",
                    tint = Color(0xFFFF8C00)
                )
            }
        }

        // 2x2 grid of action cards
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (row in 0 until 2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (col in 0 until 2) {
                            val index = row * 2 + col
                            val action = actions.getOrNull(index)

                            ActionCard(
                                action = action,
                                index = index,
                                onClick = {
                                    if (action != null) {
                                        context.triggerHapticAndSound()
                                        scope.launch {
                                            repository.selectAction(action.id)
                                        }
                                        onCloseApp()
                                    }
                                },
                                cardBg = cardBg,
                                textColor = textColor,
                                scope = scope
                            )
                        }
                    }
                }
            }

            // Centered dice button
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                val diceScale = remember { Animatable(1f) }

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .scale(diceScale.value)
                        .background(Color(0xFFFF8C00), CircleShape)
                        .clickable {
                            context.triggerHapticAndSound()
                            scope.launch {
                                diceScale.animateTo(0.9f, animationSpec = tween(100))
                                diceScale.animateTo(1f, animationSpec = tween(100))
                                excludeSet = actions.map { it.id }.toSet()
                                actions = repository.getWeightedRandomActions(excludeSet)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎲",
                        fontSize = 32.sp
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddActionDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title ->
                scope.launch {
                    repository.addAction(title)
                    excludeSet = emptySet()
                    actions = repository.getWeightedRandomActions(emptySet())
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
fun ActionCard(
    action: Action?,
    @Suppress("UNUSED_PARAMETER") index: Int,
    onClick: () -> Unit,
    cardBg: Color,
    textColor: Color,
    scope: kotlinx.coroutines.CoroutineScope
) {
    val scale = remember { Animatable(1f) }
    val isVisible = action != null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .background(cardBg, RoundedCornerShape(16.dp))
            .then(
                if (action != null) {
                    Modifier.scale(scale.value)
                } else {
                    Modifier
                }
            )
            .then(
                if (action != null) {
                    Modifier.clickable {
                        scope.launch {
                            scale.animateTo(0.95f, animationSpec = tween(100))
                            scale.animateTo(1f, animationSpec = tween(100))
                            onClick()
                        }
                    }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isVisible) {
            androidx.compose.animation.AnimatedVisibility(
                visible = true,
                enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically { it / 4 },
                exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically { it / 4 }
            ) {
                Text(
                    text = action!!.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = textColor,
                    modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            Text(
                text = "...",
                fontSize = 24.sp,
                color = Color(0xFF999999)
            )
        }
    }
}

@Composable
fun AddActionDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "What would lift you up right now?")
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g., Make coffee and go to the balcony") },
                singleLine = false
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onAdd(text.trim())
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
