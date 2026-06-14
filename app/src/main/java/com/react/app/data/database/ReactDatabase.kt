package com.react.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [State::class, Action::class, StateActionUsage::class, Log::class],
    version = 1,
    exportSchema = false
)
abstract class ReactDatabase : RoomDatabase() {
    abstract fun stateDao(): StateDao
    abstract fun actionDao(): ActionDao
    abstract fun stateActionUsageDao(): StateActionUsageDao
    abstract fun logDao(): LogDao

    companion object {
        @Volatile
        private var INSTANCE: ReactDatabase? = null

        fun getDatabase(context: Context): ReactDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReactDatabase::class.java,
                    "react_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
