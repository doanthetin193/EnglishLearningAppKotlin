package com.example.englishlearningapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.englishlearningapp.ui.screens.AddEditVocabularyScreen
import com.example.englishlearningapp.ui.screens.PracticeScreen
import com.example.englishlearningapp.ui.screens.VocabularyListScreen
import com.example.englishlearningapp.ui.screens.StatisticsScreen
import com.example.englishlearningapp.ui.viewmodel.VocabularyViewModel
import com.example.englishlearningapp.ui.viewmodel.LearningProgressViewModel
import com.example.englishlearningapp.data.entity.Vocabulary
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState

sealed class Screen(val route: String) {
    object VocabularyList : Screen("vocabularyList")
    object AddVocabulary : Screen("addVocabulary")
    object EditVocabulary : Screen("editVocabulary/{vocabularyId}")
    object Practice : Screen("practice/{topic}")
    object Statistics : Screen("statistics")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    vocabularyViewModel: VocabularyViewModel,
    learningProgressViewModel: LearningProgressViewModel
) {
    val vocabularyList by vocabularyViewModel.vocabularyList.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.VocabularyList.route
    ) {
        composable(Screen.VocabularyList.route) {
            VocabularyListScreen(
                viewModel = vocabularyViewModel,
                onNavigateToAdd = {
                    navController.navigate(Screen.AddVocabulary.route)
                },
                onNavigateToEdit = { vocabulary ->
                    navController.navigate("editVocabulary/${vocabulary.id}")
                },
                onNavigateToPractice = { topic ->
                    navController.navigate("practice/${topic ?: "all"}")
                },
                onNavigateToStatistics = {
                    navController.navigate(Screen.Statistics.route)
                }
            )
        }

        composable(Screen.AddVocabulary.route) {
            AddEditVocabularyScreen(
                onSave = { vocabulary ->
                    vocabularyViewModel.addVocabulary(vocabulary)
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.EditVocabulary.route) { backStackEntry ->
            val vocabularyId = backStackEntry.arguments?.getString("vocabularyId")?.toLongOrNull()
            val vocabulary = vocabularyList.find { it.id == vocabularyId }
            
            AddEditVocabularyScreen(
                vocabulary = vocabulary,
                onSave = { updatedVocabulary ->
                    vocabularyViewModel.updateVocabulary(updatedVocabulary)
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Practice.route) { backStackEntry ->
            val topic = backStackEntry.arguments?.getString("topic")
            val filteredList = if (topic == "all") {
                vocabularyList
            } else {
                vocabularyList.filter { it.topic == topic }
            }
            
            PracticeScreen(
                vocabularyList = filteredList,
                learningProgressViewModel = learningProgressViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                viewModel = learningProgressViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
} 