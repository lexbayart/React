package com.react.app.data.database

import androidx.room.*

@Dao
interface StateActionUsageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsage(usage: StateActionUsage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsages(usages: List<StateActionUsage>)

    @Query("SELECT * FROM state_action_usage WHERE state_id = :stateId")
    suspend fun getUsagesByState(stateId: Int): List<StateActionUsage>

    @Query("UPDATE state_action_usage SET uses = uses + 1 WHERE state_id = :stateId AND action_id = :actionId")
    suspend fun incrementUsage(stateId: Int, actionId: Int)

    @Query("SELECT * FROM state_action_usage")
    suspend fun getAllUsages(): List<StateActionUsage>

    @Query("DELETE FROM state_action_usage")
    suspend fun deleteAllUsages()
}
