package com.example.englishlearningapp.data.dao

import androidx.room.*
import com.example.englishlearningapp.data.entity.Vocabulary
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {
    @Query("SELECT * FROM vocabulary ORDER BY word ASC")
    fun getAllVocabulary(): Flow<List<Vocabulary>>

    @Query("SELECT * FROM vocabulary WHERE topic = :topic")
    fun getVocabularyByTopic(topic: String): Flow<List<Vocabulary>>

    @Query("SELECT * FROM vocabulary WHERE isLearned = 0")
    fun getUnlearnedVocabulary(): Flow<List<Vocabulary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabulary(vocabulary: Vocabulary)

    @Update
    suspend fun updateVocabulary(vocabulary: Vocabulary)

    @Delete
    suspend fun deleteVocabulary(vocabulary: Vocabulary)

    @Query("SELECT DISTINCT topic FROM vocabulary")
    fun getAllTopics(): Flow<List<String>>
} 