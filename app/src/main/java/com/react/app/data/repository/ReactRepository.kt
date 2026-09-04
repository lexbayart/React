package com.react.app.data.repository

import com.react.app.data.database.Action
import com.react.app.data.database.Log
import com.react.app.data.database.ReactDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReactRepository(private val database: ReactDatabase) {

    private val actionDao = database.actionDao()
    private val logDao = database.logDao()

    suspend fun initializeDefaults() = withContext(Dispatchers.IO) {
        val count = actionDao.getCount()
        android.util.Log.d("ReactRepo", "Current action count: $count")
        if (count > 0) return@withContext

        android.util.Log.d("ReactRepo", "Inserting 10 default actions")

        val defaultActions = listOf(
            "🌳 Walk in nature (forest/park)" to "🌳",
            "💻 Start a new pet project (coding)" to "💻",
            "🍳 Cook something tasty" to "🍳",
            "📞 Call a friend or family" to "📞",
            "😴 Sleep for 20-30 minutes" to "😴",
            "📖 Read a book/article" to "📖",
            "🏋️ Do short exercise/stretching" to "🏋️",
            "💬 Write a warm message to someone" to "💬",
            "🎬 Watch a short movie/episode (20-30 min)" to "🎬",
            "🧹 Clean/organize one workspace" to "🧹"
        )
        for ((title, emoji) in defaultActions) {
            actionDao.insertAction(Action(title = title, emoji = emoji))
        }
    }

    suspend fun getAllActions() = withContext(Dispatchers.IO) {
        actionDao.getAllActions()
    }

    suspend fun getWeightedRandomActions(excludeActionIds: Set<Int> = emptySet()): List<Action> {
        return withContext(Dispatchers.IO) {
            val allActions = actionDao.getAllActions()
            val eligible = allActions.filter { !excludeActionIds.contains(it.id) }

            if (eligible.isEmpty()) return@withContext emptyList()

            val selected = mutableListOf<Action>()
            val remaining = eligible.toMutableList()
            val usedIds = mutableSetOf<Int>()

            while (selected.size < 4) {
                if (remaining.isEmpty()) {
                    val allAvailable = allActions.filter { !usedIds.contains(it.id) }
                    if (allAvailable.isEmpty()) break
                    remaining.addAll(allAvailable)
                }

                val currentTotal = remaining.sumOf { it.uses }
                val random = if (currentTotal == 0) (1..1).random() else (1..currentTotal).random()
                var cumulative = 0
                var chosen = remaining[0]

                for (action in remaining) {
                    cumulative += action.uses
                    if (random <= cumulative) {
                        chosen = action
                        break
                    }
                }

                if (!usedIds.contains(chosen.id)) {
                    selected.add(chosen)
                    usedIds.add(chosen.id)
                }

                remaining.removeAll { it.id == chosen.id }
            }

            selected
        }
    }

    suspend fun addAction(title: String): Int {
        return withContext(Dispatchers.IO) {
            val emoji = when {
                title.contains("🌳") -> "🌳"
                title.contains("💻") -> "💻"
                title.contains("🍳") -> "🍳"
                title.contains("📞") -> "📞"
                title.contains("😴") -> "😴"
                title.contains("📖") -> "📖"
                title.contains("🏋️") -> "🏋️"
                title.contains("💬") -> "💬"
                title.contains("🎬") -> "🎬"
                title.contains("🧹") -> "🧹"
                else -> "⚡"
            }
            actionDao.insertAction(Action(title = title, emoji = emoji)).toInt()
        }
    }

    suspend fun selectAction(actionId: Int) = withContext(Dispatchers.IO) {
        actionDao.incrementUses(actionId)
        logDao.insertLog(Log(timestamp = System.currentTimeMillis(), action_id = actionId))
    }

    suspend fun getAllLogs() = withContext(Dispatchers.IO) {
        logDao.getAllLogs()
    }

    suspend fun getTotalSessions() = withContext(Dispatchers.IO) {
        logDao.getTotalLogCount()
    }

    suspend fun getAllActionsSortedByUses() = withContext(Dispatchers.IO) {
        actionDao.getAllActionsSortedByUses()
    }
}
