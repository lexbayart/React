package com.react.app.data.repository

import com.react.app.data.database.Action
import com.react.app.data.database.Log
import com.react.app.data.database.ReactDatabase
import com.react.app.data.database.State
import com.react.app.data.database.StateActionUsage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReactRepository(private val database: ReactDatabase) {

    private val stateDao = database.stateDao()
    private val actionDao = database.actionDao()
    private val stateActionUsageDao = database.stateActionUsageDao()
    private val logDao = database.logDao()

    suspend fun initializeDefaults() = withContext(Dispatchers.IO) {
        val statesAlreadyExist = stateDao.getAllStates().isNotEmpty()
        if (statesAlreadyExist) return@withContext

        val states = listOf(
            State(1, "🎮", "Gamer"),
            State(2, "📺", "Viewer"),
            State(3, "💬", "Talker"),
            State(4, "😴", "Sleeper"),
            State(5, "🔄", "Zombie"),
            State(6, "🔧", "Stuck"),
            State(7, "😰", "Anxious"),
            State(8, "😔", "Sad")
        )
        stateDao.insertStates(states)

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
        val actionIds = mutableListOf<Int>()
        for (title in defaultActions) {
            val id = actionDao.insertAction(Action(title = title)).toInt()
            actionIds.add(id)
        }

        val allStates = stateDao.getAllStates()
        val usages = mutableListOf<StateActionUsage>()
        for (state in allStates) {
            for (actionId in actionIds) {
                usages.add(StateActionUsage(state.id, actionId, 0))
            }
        }
        stateActionUsageDao.insertUsages(usages)
    }

    suspend fun getAllStates() = withContext(Dispatchers.IO) {
        stateDao.getAllStates()
    }

    suspend fun getStateById(id: Int) = withContext(Dispatchers.IO) {
        stateDao.getStateById(id)
    }

    suspend fun getAllActions() = withContext(Dispatchers.IO) {
        actionDao.getAllActions()
    }

    suspend fun getUsagesByState(stateId: Int) = withContext(Dispatchers.IO) {
        stateActionUsageDao.getUsagesByState(stateId)
    }

    suspend fun getWeightedRandomActions(stateId: Int, excludeActionIds: Set<Int> = emptySet()): List<Action> {
        return withContext(Dispatchers.IO) {
            val usages = stateActionUsageDao.getUsagesByState(stateId)
            val allActions = actionDao.getAllActions()
            val actionMap = allActions.associateBy { it.id }

            val eligible = usages.filter { !excludeActionIds.contains(it.action_id) }

            if (eligible.isEmpty()) {
                val remaining = allActions.filter { !excludeActionIds.contains(it.id) }
                if (remaining.size <= 4) remaining else remaining.shuffled().take(4)
            } else {
                val totalWeight = eligible.sumOf { it.uses }
                val selected = mutableListOf<Action>()
                val remainingUsages = eligible.toMutableList()
                val usedActionIds = mutableSetOf<Int>()

                val count = minOf(4, remainingUsages.size)
                for (i in 0 until count) {
                    if (remainingUsages.isEmpty()) break

                    val currentTotal = remainingUsages.sumOf { it.uses }
                    val random = (1..currentTotal).random()
                    var cumulative = 0
                    var chosen = remainingUsages[0]

                    for (usage in remainingUsages) {
                        cumulative += usage.uses
                        if (random <= cumulative) {
                            chosen = usage
                            break
                        }
                    }

                    actionMap[chosen.action_id]?.let { action ->
                        if (!usedActionIds.contains(action.id)) {
                            selected.add(action)
                            usedActionIds.add(action.id)
                        }
                    }

                    remainingUsages.removeAll { it.action_id == chosen.action_id }
                }

                selected
            }
        }
    }

    suspend fun addAction(title: String, stateId: Int): Int {
        return withContext(Dispatchers.IO) {
            val actionId = actionDao.insertAction(Action(title = title)).toInt()
            stateActionUsageDao.insertUsage(StateActionUsage(stateId, actionId, 0))
            actionId
        }
    }

    suspend fun selectAction(stateId: Int, actionId: Int) = withContext(Dispatchers.IO) {
        stateActionUsageDao.incrementUsage(stateId, actionId)
        logDao.insertLog(Log(timestamp = System.currentTimeMillis(), state_id = stateId, action_id = actionId))
    }

    suspend fun getAllLogs() = withContext(Dispatchers.IO) {
        logDao.getAllLogs()
    }

    suspend fun getAllUsages() = withContext(Dispatchers.IO) {
        stateActionUsageDao.getAllUsages()
    }

    suspend fun getTotalSessions() = withContext(Dispatchers.IO) {
        logDao.getTotalLogCount()
    }
}
