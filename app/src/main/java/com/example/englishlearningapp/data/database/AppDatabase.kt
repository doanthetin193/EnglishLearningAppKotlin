package com.example.englishlearningapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.englishlearningapp.data.dao.VocabularyDao
import com.example.englishlearningapp.data.entity.Vocabulary

@Database(entities = [Vocabulary::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vocabularyDao(): VocabularyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vocabulary_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 