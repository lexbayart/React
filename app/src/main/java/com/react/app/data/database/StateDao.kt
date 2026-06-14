package com.react.app.data.database

import androidx.room.*

@Dao
interface StateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertState(state: State)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStates(states: List<State>)

    @Query("SELECT * FROM states ORDER BY id")
    suspend fun getAllStates(): List<State>

    @Query("SELECT * FROM states WHERE id = :id")
    suspend fun getStateById(id: Int): State?
}
