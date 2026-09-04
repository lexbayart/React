package com.react.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Action::class, Log::class],
    version = 3,
    exportSchema = false
)
abstract class ReactDatabase : RoomDatabase() {
    abstract fun actionDao(): ActionDao
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
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
