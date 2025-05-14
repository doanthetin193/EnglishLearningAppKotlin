package com.example.englishlearningapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.englishlearningapp.data.entity.Vocabulary
import com.example.englishlearningapp.ui.viewmodel.VocabularyViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyListScreen(
    viewModel: VocabularyViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Vocabulary) -> Unit,
    onNavigateToPractice: (String?) -> Unit
) {
    val vocabularyList by viewModel.vocabularyList.collectAsState()
    val topics by viewModel.topics.collectAsState()
    val selectedTopic by viewModel.selectedTopic.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vocabulary List") },
                actions = {
                    IconButton(onClick = onNavigateToAdd) {
                        Icon(Icons.Default.Add, contentDescription = "Add Vocabulary")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToPractice(selectedTopic) }
            ) {
                Text("Practice")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Topic Filter
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedTopic == null,
                        onClick = { viewModel.setSelectedTopic(null) },
                        label = { Text("All") }
                    )
                }
                items(topics) { topic ->
                    FilterChip(
                        selected = selectedTopic == topic,
                        onClick = { viewModel.setSelectedTopic(topic) },
                        label = { Text(topic) }
                    )
                }
            }

            // Vocabulary List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(vocabularyList) { vocabulary ->
                    VocabularyCard(
                        vocabulary = vocabulary,
                        onEdit = { onNavigateToEdit(vocabulary) },
                        onDelete = { viewModel.deleteVocabulary(vocabulary) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyCard(
    vocabulary: Vocabulary,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = vocabulary.word,
                    style = MaterialTheme.typography.titleMedium
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = vocabulary.meaning,
                style = MaterialTheme.typography.bodyMedium
            )
            if (vocabulary.example.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Example: ${vocabulary.example}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (vocabulary.pronunciation != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pronunciation: ${vocabulary.pronunciation}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
} 