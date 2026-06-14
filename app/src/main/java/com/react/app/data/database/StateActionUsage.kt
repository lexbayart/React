package com.react.app.data.database

import androidx.room.Entity

@Entity(
    tableName = "state_action_usage",
    primaryKeys = ["state_id", "action_id"]
)
data class StateActionUsage(
    val state_id: Int,
    val action_id: Int,
    val uses: Int = 0
)
