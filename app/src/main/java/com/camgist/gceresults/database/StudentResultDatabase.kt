package com.camgist.gceresults.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StudentResult::class], version = 1, exportSchema = false)
abstract class StudentResultDatabase : RoomDatabase() {



    abstract val studentResultDao: StudentResultDao
    companion object {
        @Volatile
        private var INSTANCE: StudentResultDatabase? = null

        fun getInstance(context: Context): StudentResultDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        StudentResultDatabase::class.java,
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