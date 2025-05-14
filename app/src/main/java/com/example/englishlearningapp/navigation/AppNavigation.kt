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
import com.example.englishlearningapp.ui.viewmodel.VocabularyViewModel

sealed class Screen(val route: String) {
    object VocabularyList : Screen("vocabulary_list")
    object AddVocabulary : Screen("add_vocabulary")
    object EditVocabulary : Screen("edit_vocabulary/{vocabularyId}") {
        fun createRoute(vocabularyId: Long) = "edit_vocabulary/$vocabularyId"
    }
    object Practice : Screen("practice/{topic}") {
        fun createRoute(topic: String?) = "practice/${topic ?: "all"}"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: VocabularyViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.VocabularyList.route
    ) {
        composable(Screen.VocabularyList.route) {
            VocabularyListScreen(
                viewModel = viewModel,
                onNavigateToAdd = {
                    navController.navigate(Screen.AddVocabulary.route)
                },
                onNavigateToEdit = { vocabulary ->
                    navController.navigate(Screen.EditVocabulary.createRoute(vocabulary.id))
                },
                onNavigateToPractice = { topic ->
                    navController.navigate(Screen.Practice.createRoute(topic))
                }
            )
        }

        composable(Screen.AddVocabulary.route) {
            AddEditVocabularyScreen(
                onSave = { vocabulary ->
                    viewModel.addVocabulary(vocabulary)
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditVocabulary.route,
            arguments = listOf(
                navArgument("vocabularyId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val vocabularyId = backStackEntry.arguments?.getLong("vocabularyId") ?: return@composable
            val vocabulary = viewModel.vocabularyList.value.find { it.id == vocabularyId }
            AddEditVocabularyScreen(
                vocabulary = vocabulary,
                onSave = { updatedVocabulary ->
                    viewModel.updateVocabulary(updatedVocabulary)
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Practice.route,
            arguments = listOf(
                navArgument("topic") {
                    type = NavType.StringType
                    defaultValue = "all"
                }
            )
        ) { backStackEntry ->
            val topic = backStackEntry.arguments?.getString("topic")
            val vocabularyList = if (topic == "all") {
                viewModel.vocabularyList.value
            } else {
                viewModel.vocabularyList.value.filter { it.topic == topic }
            }
            PracticeScreen(
                vocabularyList = vocabularyList,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
} 