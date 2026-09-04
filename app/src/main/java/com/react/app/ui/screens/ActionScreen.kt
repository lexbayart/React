package com.react.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.react.app.data.database.Action
import com.react.app.data.repository.ReactRepository
import com.react.app.utils.triggerHapticAndSound
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ActionScreen(
    onBack: () -> Unit,
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Top bar with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("React", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color(0xFFE53935))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4 cards in vertical column
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            for (i in 0..3) {
                ActionCard(
                    action = actions.getOrNull(i),
                    onClick = {
                        val action = actions.getOrNull(i) ?: return@ActionCard
                        context.triggerHapticAndSound()
                        scope.launch {
                            repository.selectAction(action.id)
                            delay(50)
                            onCloseApp()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Red dice button centered at bottom
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            FloatingActionButton(
                onClick = {
                    context.triggerHapticAndSound()
                    scope.launch {
                        excludeSet = actions.map { it.id }.toSet()
                        actions = repository.getWeightedRandomActions(excludeSet)
                    }
                },
                containerColor = Color(0xFFE53935),
                modifier = Modifier.size(72.dp)
            ) {
                Text("🎲", fontSize = 36.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
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
    onClick: () -> Unit
) {
    val cardBg = Color(0xFFFFFFFF)
    val textColor = Color(0xFF212121)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(enabled = action != null) { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(
                text = action?.emoji ?: "❓",
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = action?.title ?: "???",
                fontSize = 16.sp,
                color = textColor,
                fontWeight = FontWeight.Medium
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
