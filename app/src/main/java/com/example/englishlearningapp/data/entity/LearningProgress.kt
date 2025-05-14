package com.example.englishlearningapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learning_progress")
data class LearningProgress(
    @PrimaryKey
    val id: Int = 1, // Single row for app-wide statistics
    val totalWordsLearned: Int = 0,
    val totalCorrectAnswers: Int = 0,
    val totalAttempts: Int = 0,
    val lastStudyDate: Long = System.currentTimeMillis(),
    val streak: Int = 0
) 