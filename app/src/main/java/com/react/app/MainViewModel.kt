package com.react.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.react.app.data.database.Log
import com.react.app.data.repository.ReactRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    var logs by mutableStateOf<List<Log>?>(null)
        private set

    var totalSessions by mutableStateOf<Int?>(null)
        private set

    fun loadLogs(repository: ReactRepository) {
        viewModelScope.launch {
            logs = repository.getAllLogs()
        }
    }

    fun loadTotalSessions(repository: ReactRepository) {
        viewModelScope.launch {
            totalSessions = repository.getTotalSessions()
        }
    }
}
