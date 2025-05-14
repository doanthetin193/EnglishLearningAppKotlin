package com.example.englishlearningapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.englishlearningapp.data.dao.VocabularyDao
import com.example.englishlearningapp.data.dao.LearningProgressDao
import com.example.englishlearningapp.data.entity.Vocabulary
import com.example.englishlearningapp.data.entity.LearningProgress

@Database(
    entities = [
        Vocabulary::class,
        LearningProgress::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun learningProgressDao(): LearningProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vocabulary_database"
                )
                .fallbackToDestructiveMigration() // Handle version changes
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 