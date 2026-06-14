package com.react.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.react.app.data.database.Action
import com.react.app.data.database.Log
import com.react.app.data.database.ReactDatabase
import com.react.app.data.database.State
import com.react.app.data.database.StateActionUsage
import com.react.app.data.repository.ReactRepository
import com.react.app.ui.screens.ActionScreen
import com.react.app.ui.screens.StateScreen
import com.react.app.ui.screens.StatsScreen
import com.react.app.ui.theme.ReactTheme
import com.react.app.utils.ExportUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private lateinit var repository: ReactRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = ReactDatabase.getDatabase(this)
        repository = ReactRepository(database)

        enableEdgeToEdge()

        setContent {
            ReactTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                var states by mutableStateOf<List<State>?>(null)
                var usages by mutableStateOf<List<StateActionUsage>?>(null)
                var logs by mutableStateOf<List<Log>?>(null)
                var totalSessions by mutableStateOf<Int>(0)
                var allActions by mutableStateOf<List<Action>?>(null)

                // Load data in background
                CoroutineScope(Dispatchers.IO).launch {
                    repository.initializeDefaults()
                    val loadedStates = repository.getAllStates()
                    val loadedUsages = repository.getAllUsages()
                    val loadedLogs = repository.getAllLogs()
                    val loadedSessions = repository.getTotalSessions()
                    val loadedActions = repository.getAllActions()
                    withContext(Dispatchers.Main) {
                        states = loadedStates
                        usages = loadedUsages
                        logs = loadedLogs
                        totalSessions = loadedSessions
                        allActions = loadedActions
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "state",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("state") {
                            states?.let { st ->
                                StateScreen(
                                    states = st,
                                    onStateSelected = { stateId ->
                                        navController.navigate("action/$stateId")
                                    },
                                    onOpenStats = {
                                        navController.navigate("stats")
                                    },
                                    repository = repository
                                )
                            }
                        }

                        composable("action/{stateId}") { backStackEntry ->
                            val stateId = backStackEntry.arguments?.getString("stateId")?.toIntOrNull()
                            if (stateId != null) {
                                ActionScreen(
                                    stateId = stateId,
                                    onBack = { navController.popBackStack() },
                                    onCloseApp = { closeApp() },
                                    repository = repository
                                )
                            }
                        }

                        composable("stats") {
                            StatsScreen(
                                states = states ?: emptyList(),
                                usages = usages ?: emptyList(),
                                totalSessions = totalSessions,
                                onBack = { navController.popBackStack() },
                                onExport = {
                                    val currentLogs = logs ?: emptyList()
                                    val currentUsages = usages ?: emptyList()
                                    ExportUtils.exportStats(context, currentLogs, currentUsages)
                                },
                                actions = allActions ?: emptyList()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun closeApp() {
        moveTaskToBack(true)
        finish()
    }
}
