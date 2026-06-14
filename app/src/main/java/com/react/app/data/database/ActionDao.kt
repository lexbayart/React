package com.react.app.data.database

import androidx.room.*

@Dao
interface ActionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: Action): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActions(actions: List<Action>)

    @Query("SELECT * FROM actions ORDER BY id")
    suspend fun getAllActions(): List<Action>

    @Query("SELECT * FROM actions WHERE id = :id")
    suspend fun getActionById(id: Int): Action?

    @Query("DELETE FROM actions")
    suspend fun deleteAllActions()
}
