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
        val actionsAlreadyExist = actionDao.getCount() > 0
        if (actionsAlreadyExist) return@withContext

        val defaultActions = listOf(
            "Walk in nature (forest/park)",
            "Start a new pet project (coding)",
            "Cook something tasty",
            "Call a friend or family",
            "Sleep for 20-30 minutes",
            "Read a book/article",
            "Do short exercise/stretching",
            "Write a warm message to someone",
            "Watch a short movie/episode (20-30 min)",
            "Clean/organize one workspace"
        )
        for (title in defaultActions) {
            actionDao.insertAction(Action(title = title))
        }
    }

    suspend fun getAllActions() = withContext(Dispatchers.IO) {
        actionDao.getAllActions()
    }

    suspend fun getWeightedRandomActions(excludeActionIds: Set<Int> = emptySet()): List<Action> {
        return withContext(Dispatchers.IO) {
            val allActions = actionDao.getAllActions()
            val eligible = allActions.filter { !excludeActionIds.contains(it.id) }

            if (eligible.size <= 4) eligible else {
                val selected = mutableListOf<Action>()
                val remaining = eligible.toMutableList()
                val usedIds = mutableSetOf<Int>()

                val count = minOf(4, remaining.size)
                for (i in 0 until count) {
                    if (remaining.isEmpty()) break

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
    }

    suspend fun addAction(title: String): Int {
        return withContext(Dispatchers.IO) {
            actionDao.insertAction(Action(title = title)).toInt()
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
