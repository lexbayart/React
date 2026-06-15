package com.react.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actions")
data class Action(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val uses: Int = 0
)
