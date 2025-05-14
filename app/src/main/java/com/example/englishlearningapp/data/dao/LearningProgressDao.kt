package com.example.englishlearningapp.data.dao

import androidx.room.*
import com.example.englishlearningapp.data.entity.LearningProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningProgressDao {
    @Query("SELECT * FROM learning_progress WHERE id = 1")
    fun getLearningProgress(): Flow<LearningProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: LearningProgress)

    @Query("UPDATE learning_progress SET totalWordsLearned = totalWordsLearned + 1 WHERE id = 1")
    suspend fun incrementWordsLearned()

    @Query("UPDATE learning_progress SET totalCorrectAnswers = totalCorrectAnswers + 1 WHERE id = 1")
    suspend fun incrementCorrectAnswers()

    @Query("UPDATE learning_progress SET totalAttempts = totalAttempts + 1 WHERE id = 1")
    suspend fun incrementAttempts()

    @Query("""
        UPDATE learning_progress 
        SET lastStudyDate = :currentDate,
            streak = CASE 
                WHEN ((:currentDate - lastStudyDate) / 86400000) = 1 THEN streak + 1
                WHEN ((:currentDate - lastStudyDate) / 86400000) > 1 THEN 1
                ELSE streak
            END
        WHERE id = 1
    """)
    suspend fun updateStudyStreak(currentDate: Long = System.currentTimeMillis())
} 