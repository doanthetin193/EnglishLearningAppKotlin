package com.example.englishlearningapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.englishlearningapp.data.database.AppDatabase
import com.example.englishlearningapp.data.repository.VocabularyRepository
import com.example.englishlearningapp.data.repository.LearningProgressRepository
import com.example.englishlearningapp.navigation.AppNavigation
import com.example.englishlearningapp.ui.theme.EnglishLearningAppTheme
import com.example.englishlearningapp.ui.viewmodel.VocabularyViewModel
import com.example.englishlearningapp.ui.viewmodel.VocabularyViewModelFactory
import com.example.englishlearningapp.ui.viewmodel.LearningProgressViewModel
import com.example.englishlearningapp.ui.viewmodel.LearningProgressViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        val vocabularyRepository = VocabularyRepository(database.vocabularyDao())
        val learningProgressRepository = LearningProgressRepository(database.learningProgressDao())

        setContent {
            EnglishLearningAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val vocabularyViewModel: VocabularyViewModel = viewModel(
                        factory = VocabularyViewModelFactory(vocabularyRepository)
                    )
                    val learningProgressViewModel: LearningProgressViewModel = viewModel(
                        factory = LearningProgressViewModelFactory(learningProgressRepository)
                    )
                    AppNavigation(
                        navController = navController,
                        vocabularyViewModel = vocabularyViewModel,
                        learningProgressViewModel = learningProgressViewModel
                    )
                }
            }
        }
    }
}