package com.example.local_database_room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Student::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // 1. DAO getters
    abstract fun studentDao(): StudentDAO

    companion object {
        // 2. Singleton instance
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 3. Builder function
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_database" // 4. Database file name
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}