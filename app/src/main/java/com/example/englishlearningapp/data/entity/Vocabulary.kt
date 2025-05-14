package com.example.englishlearningapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary")
data class Vocabulary(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val meaning: String,
    val example: String,
    val pronunciation: String?,
    val topic: String,
    val isLearned: Boolean = false,
    val lastReviewed: Long = System.currentTimeMillis()
) 