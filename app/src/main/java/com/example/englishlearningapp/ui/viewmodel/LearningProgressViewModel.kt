package com.example.englishlearningapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.englishlearningapp.data.entity.LearningProgress
import com.example.englishlearningapp.data.repository.LearningProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LearningProgressViewModel(private val repository: LearningProgressRepository) : ViewModel() {
    private val _progress = MutableStateFlow<LearningProgress?>(null)
    val progress: StateFlow<LearningProgress?> = _progress.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeProgress()
            repository.learningProgress.collect { progress ->
                _progress.value = progress
            }
        }
    }

    fun updateProgress(isCorrect: Boolean = false) {
        viewModelScope.launch {
            repository.incrementAttempts()
            if (isCorrect) {
                repository.incrementCorrectAnswers()
            }
            repository.updateStudyStreak()
        }
    }

    fun incrementWordsLearned() {
        viewModelScope.launch {
            repository.incrementWordsLearned()
        }
    }
}

class LearningProgressViewModelFactory(
    private val repository: LearningProgressRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LearningProgressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LearningProgressViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 