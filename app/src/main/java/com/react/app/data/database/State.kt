package com.react.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "states")
data class State(
    @PrimaryKey val id: Int,
    val emoji: String,
    val name: String
)
