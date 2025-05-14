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
import com.example.englishlearningapp.navigation.AppNavigation
import com.example.englishlearningapp.ui.theme.EnglishLearningAppTheme
import com.example.englishlearningapp.ui.viewmodel.VocabularyViewModel
import com.example.englishlearningapp.ui.viewmodel.VocabularyViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = VocabularyRepository(database.vocabularyDao())

        setContent {
            EnglishLearningAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: VocabularyViewModel = viewModel(
                        factory = VocabularyViewModelFactory(repository)
                    )
                    AppNavigation(navController = navController, viewModel = viewModel)
                }
            }
        }
    }
}