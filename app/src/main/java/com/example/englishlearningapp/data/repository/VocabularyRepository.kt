package com.example.englishlearningapp.data.repository

import com.example.englishlearningapp.data.dao.VocabularyDao
import com.example.englishlearningapp.data.entity.Vocabulary
import kotlinx.coroutines.flow.Flow

class VocabularyRepository(private val vocabularyDao: VocabularyDao) {
    val allVocabulary: Flow<List<Vocabulary>> = vocabularyDao.getAllVocabulary()
    
    fun getVocabularyByTopic(topic: String): Flow<List<Vocabulary>> {
        return vocabularyDao.getVocabularyByTopic(topic)
    }

    fun getUnlearnedVocabulary(): Flow<List<Vocabulary>> {
        return vocabularyDao.getUnlearnedVocabulary()
    }

    suspend fun insertVocabulary(vocabulary: Vocabulary) {
        vocabularyDao.insertVocabulary(vocabulary)
    }

    suspend fun updateVocabulary(vocabulary: Vocabulary) {
        vocabularyDao.updateVocabulary(vocabulary)
    }

    suspend fun deleteVocabulary(vocabulary: Vocabulary) {
        vocabularyDao.deleteVocabulary(vocabulary)
    }

    fun getAllTopics(): Flow<List<String>> {
        return vocabularyDao.getAllTopics()
    }
} 