package com.react.app.data.database

import androidx.room.*

@Dao
interface LogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: Log): Long

    @Query("SELECT * FROM logs ORDER BY timestamp DESC")
    suspend fun getAllLogs(): List<Log>

    @Query("SELECT COUNT(*) FROM logs")
    suspend fun getTotalLogCount(): Int
}
