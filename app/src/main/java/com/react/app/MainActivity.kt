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
import com.react.app.data.repository.ReactRepository
import com.react.app.ui.screens.ActionScreen
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
            ReactTheme(darkTheme = false) {
                val navController = rememberNavController()
                val context = LocalContext.current
                var logs by mutableStateOf<List<Log>>(emptyList())
                var totalSessions by mutableStateOf<Int>(0)
                var allActions by mutableStateOf<List<Action>>(emptyList())
                var dataLoaded by mutableStateOf(false)

                // Load data in background
                CoroutineScope(Dispatchers.IO).launch {
                    repository.initializeDefaults()
                    val loadedLogs = repository.getAllLogs()
                    val loadedSessions = repository.getTotalSessions()
                    val loadedActions = repository.getAllActions()
                    withContext(Dispatchers.Main) {
                        logs = loadedLogs
                        totalSessions = loadedSessions
                        allActions = loadedActions
                        dataLoaded = true
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "action",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("action") {
                            if (dataLoaded) {
                                ActionScreen(
                                    onBack = { /* already start destination */ },
                                    onCloseApp = { closeApp() },
                                    repository = repository
                                )
                            }
                        }

                        composable("stats") {
                            StatsScreen(
                                totalSessions = totalSessions,
                                onBack = { navController.popBackStack() },
                                onExport = {
                                    ExportUtils.exportStats(context, logs, emptyList())
                                },
                                actions = allActions
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
