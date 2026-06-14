package com.react.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logs")
data class Log(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val state_id: Int,
    val action_id: Int
)
