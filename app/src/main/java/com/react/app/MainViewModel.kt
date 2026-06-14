package com.react.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.react.app.data.database.Log
import com.react.app.data.database.State
import com.react.app.data.database.StateActionUsage
import com.react.app.data.repository.ReactRepository

class MainViewModel : ViewModel() {

    var states by mutableStateOf<List<State>?>(null)
        private set

    var usages by mutableStateOf<List<StateActionUsage>?>(null)
        private set

    var logs by mutableStateOf<List<Log>?>(null)
        private set

    var totalSessions by mutableStateOf<Int?>(null)
        private set

    fun loadStates(repository: ReactRepository) {
        Thread {
            val result = repository.getAllStates()
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                states = result
            }
        }.start()
    }

    fun loadUsages(repository: ReactRepository) {
        Thread {
            val result = repository.getAllUsages()
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                usages = result
            }
        }.start()
    }

    fun loadLogs(repository: ReactRepository) {
        Thread {
            val result = repository.getAllLogs()
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                logs = result
            }
        }.start()
    }

    fun loadTotalSessions(repository: ReactRepository) {
        Thread {
            val result = repository.getTotalSessions()
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                totalSessions = result
            }
        }.start()
    }
}
