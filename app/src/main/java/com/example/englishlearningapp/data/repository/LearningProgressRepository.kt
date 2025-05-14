package com.example.englishlearningapp.data.repository

import com.example.englishlearningapp.data.dao.LearningProgressDao
import com.example.englishlearningapp.data.entity.LearningProgress
import kotlinx.coroutines.flow.Flow

class LearningProgressRepository(private val learningProgressDao: LearningProgressDao) {
    val learningProgress: Flow<LearningProgress?> = learningProgressDao.getLearningProgress()

    suspend fun initializeProgress() {
        // Initialize with default values if not exists
        learningProgressDao.insertOrUpdate(LearningProgress())
    }

    suspend fun incrementWordsLearned() {
        learningProgressDao.incrementWordsLearned()
    }

    suspend fun incrementCorrectAnswers() {
        learningProgressDao.incrementCorrectAnswers()
    }

    suspend fun incrementAttempts() {
        learningProgressDao.incrementAttempts()
    }

    suspend fun updateStudyStreak() {
        learningProgressDao.updateStudyStreak()
    }
} 