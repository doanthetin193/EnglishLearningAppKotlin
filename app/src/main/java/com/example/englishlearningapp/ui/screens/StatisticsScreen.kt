package com.example.englishlearningapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.englishlearningapp.ui.viewmodel.LearningProgressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: LearningProgressViewModel,
    onNavigateBack: () -> Unit
) {
    val progress by viewModel.progress.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Learning Statistics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Words Learned Card
            StatisticCard(
                title = "Words Learned",
                value = progress?.totalWordsLearned?.toString() ?: "0"
            )

            // Accuracy Card
            StatisticCard(
                title = "Accuracy",
                value = if (progress?.totalAttempts ?: 0 > 0) {
                    val accuracy = (progress?.totalCorrectAnswers?.toFloat() ?: 0f) / 
                        (progress?.totalAttempts?.toFloat() ?: 1f) * 100
                    String.format("%.1f%%", accuracy)
                } else {
                    "0%"
                }
            )

            // Study Streak Card
            StatisticCard(
                title = "Study Streak",
                value = "${progress?.streak ?: 0} days"
            )

            // Total Practice Sessions Card
            StatisticCard(
                title = "Total Practice Attempts",
                value = progress?.totalAttempts?.toString() ?: "0"
            )
        }
    }
}

@Composable
fun StatisticCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }
    }
} 