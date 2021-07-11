package com.example.android.bellmanford.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GraphEntity::class], version = 1, exportSchema = false)
abstract class GraphDatabase : RoomDatabase() {
    abstract val graphDatabase: GraphDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: GraphDatabase? = null

        fun getInstance(context: Context): GraphDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        GraphDatabase::class.java,
                        "sleep_history_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}