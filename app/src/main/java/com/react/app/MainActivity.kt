package com.react.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.react.app.data.database.ReactDatabase
import com.react.app.data.repository.ReactRepository
import com.react.app.ui.screens.ActionScreen
import com.react.app.ui.screens.StateScreen
import com.react.app.ui.screens.StatsScreen
import com.react.app.ui.theme.ReactTheme
import com.react.app.utils.ExportUtils

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
                val viewModel: MainViewModel = viewModel()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "state",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("state") {
                            viewModel.states.value?.let { states ->
                                StateScreen(
                                    states = states,
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
                                states = viewModel.states.value ?: emptyList(),
                                usages = viewModel.usages.value ?: emptyList(),
                                totalSessions = viewModel.totalSessions.value ?: 0,
                                onBack = { navController.popBackStack() },
                                onExport = {
                                    val logs = viewModel.logs.value ?: emptyList()
                                    val usages = viewModel.usages.value ?: emptyList()
                                    ExportUtils.exportStats(this@MainActivity, logs, usages)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Initialize defaults in background
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            Thread {
                repository.initializeDefaults()
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    viewModel.loadStates(repository)
                    viewModel.loadUsages(repository)
                    viewModel.loadLogs(repository)
                    viewModel.loadTotalSessions(repository)
                }
            }.start()
        }
    }

    private fun closeApp() {
        moveTaskToBack(true)
        finish()
    }
}
