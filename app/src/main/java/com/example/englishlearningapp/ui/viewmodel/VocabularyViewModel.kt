package com.example.englishlearningapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.englishlearningapp.data.entity.Vocabulary
import com.example.englishlearningapp.data.repository.VocabularyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VocabularyViewModel(private val repository: VocabularyRepository) : ViewModel() {
    private val _vocabularyList = MutableStateFlow<List<Vocabulary>>(emptyList())
    val vocabularyList: StateFlow<List<Vocabulary>> = _vocabularyList.asStateFlow()

    private val _topics = MutableStateFlow<List<String>>(emptyList())
    val topics: StateFlow<List<String>> = _topics.asStateFlow()

    private val _selectedTopic = MutableStateFlow<String?>(null)
    val selectedTopic: StateFlow<String?> = _selectedTopic.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allVocabulary.collect { vocabulary ->
                _vocabularyList.value = vocabulary
            }
        }
        viewModelScope.launch {
            repository.getAllTopics().collect { topics ->
                _topics.value = topics
            }
        }
    }

    fun setSelectedTopic(topic: String?) {
        _selectedTopic.value = topic
        viewModelScope.launch {
            if (topic != null) {
                repository.getVocabularyByTopic(topic).collect { vocabulary ->
                    _vocabularyList.value = vocabulary
                }
            } else {
                repository.allVocabulary.collect { vocabulary ->
                    _vocabularyList.value = vocabulary
                }
            }
        }
    }

    fun addVocabulary(vocabulary: Vocabulary) {
        viewModelScope.launch {
            repository.insertVocabulary(vocabulary)
        }
    }

    fun updateVocabulary(vocabulary: Vocabulary) {
        viewModelScope.launch {
            repository.updateVocabulary(vocabulary)
        }
    }

    fun deleteVocabulary(vocabulary: Vocabulary) {
        viewModelScope.launch {
            repository.deleteVocabulary(vocabulary)
        }
    }
}

class VocabularyViewModelFactory(private val repository: VocabularyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VocabularyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VocabularyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 