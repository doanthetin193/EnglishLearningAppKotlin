package com.example.englishlearningapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.englishlearningapp.data.entity.Vocabulary
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVocabularyScreen(
    vocabulary: Vocabulary? = null,
    onSave: (Vocabulary) -> Unit,
    onNavigateBack: () -> Unit
) {
    var word by remember { mutableStateOf(vocabulary?.word ?: "") }
    var meaning by remember { mutableStateOf(vocabulary?.meaning ?: "") }
    var example by remember { mutableStateOf(vocabulary?.example ?: "") }
    var pronunciation by remember { mutableStateOf(vocabulary?.pronunciation ?: "") }
    var topic by remember { mutableStateOf(vocabulary?.topic ?: "") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (vocabulary == null) "Add Vocabulary" else "Edit Vocabulary") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (word.isNotBlank() && meaning.isNotBlank() && topic.isNotBlank()) {
                                try {
                                    val newVocabulary = Vocabulary(
                                        id = vocabulary?.id ?: 0,
                                        word = word,
                                        meaning = meaning,
                                        example = example,
                                        pronunciation = pronunciation.takeIf { it.isNotBlank() },
                                        topic = topic
                                    )
                                    onSave(newVocabulary)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Vocabulary saved successfully")
                                        onNavigateBack()
                                    }
                                } catch (e: Exception) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Error saving vocabulary: ${e.message}")
                                    }
                                }
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please fill in all required fields")
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = word,
                onValueChange = { word = it },
                label = { Text("Word") },
                modifier = Modifier.fillMaxWidth(),
                isError = word.isBlank()
            )

            OutlinedTextField(
                value = meaning,
                onValueChange = { meaning = it },
                label = { Text("Meaning") },
                modifier = Modifier.fillMaxWidth(),
                isError = meaning.isBlank()
            )

            OutlinedTextField(
                value = example,
                onValueChange = { example = it },
                label = { Text("Example (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = pronunciation,
                onValueChange = { pronunciation = it },
                label = { Text("Pronunciation (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                label = { Text("Topic") },
                modifier = Modifier.fillMaxWidth(),
                isError = topic.isBlank()
            )
        }
    }
} 