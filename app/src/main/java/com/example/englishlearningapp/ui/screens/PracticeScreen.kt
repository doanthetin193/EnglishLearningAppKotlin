package com.example.englishlearningapp.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.englishlearningapp.data.entity.Vocabulary
import com.example.englishlearningapp.ui.viewmodel.LearningProgressViewModel

enum class PracticeMode {
    MULTIPLE_CHOICE,
    FILL_IN_BLANK,
    MATCHING,
    FLASHCARD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    vocabularyList: List<Vocabulary>,
    learningProgressViewModel: LearningProgressViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedMode by remember { mutableStateOf<PracticeMode?>(null) }
    var currentIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var showResults by remember { mutableStateOf(false) }

    // Keep track of learned words to avoid counting duplicates
    var learnedWords by remember { mutableStateOf(setOf<Long>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Practice") },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectedMode == null) {
                // Mode Selection
                Text(
                    text = "Select Practice Mode",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                PracticeMode.values().forEach { mode ->
                    Button(
                        onClick = { 
                            selectedMode = mode
                            // Reset learned words when starting new practice
                            learnedWords = setOf()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(mode.name.replace("_", " ").lowercase().capitalize())
                    }
                }
            } else if (!showResults) {
                // Practice Content
                when (selectedMode) {
                    PracticeMode.MULTIPLE_CHOICE -> MultipleChoicePractice(
                        vocabulary = vocabularyList[currentIndex],
                        options = vocabularyList.shuffled().take(4).map { it.meaning },
                        onAnswer = { isCorrect ->
                            learningProgressViewModel.updateProgress(isCorrect)
                            if (isCorrect && !learnedWords.contains(vocabularyList[currentIndex].id)) {
                                learningProgressViewModel.incrementWordsLearned()
                                learnedWords = learnedWords + vocabularyList[currentIndex].id
                                score++
                            }
                            if (currentIndex < vocabularyList.size - 1) {
                                currentIndex++
                            } else {
                                showResults = true
                            }
                        }
                    )
                    PracticeMode.FILL_IN_BLANK -> FillInBlankPractice(
                        vocabulary = vocabularyList[currentIndex],
                        onAnswer = { isCorrect ->
                            learningProgressViewModel.updateProgress(isCorrect)
                            if (isCorrect && !learnedWords.contains(vocabularyList[currentIndex].id)) {
                                learningProgressViewModel.incrementWordsLearned()
                                learnedWords = learnedWords + vocabularyList[currentIndex].id
                                score++
                            }
                            if (currentIndex < vocabularyList.size - 1) {
                                currentIndex++
                            } else {
                                showResults = true
                            }
                        }
                    )
                    PracticeMode.MATCHING -> MatchingPractice(
                        vocabularyList = vocabularyList,
                        onComplete = { correctMatches, matchedWordIds ->
                            score = correctMatches
                            showResults = true
                            // Update progress for each attempt
                            repeat(vocabularyList.size) { index ->
                                learningProgressViewModel.updateProgress(index < correctMatches)
                            }
                            // Increment words learned for newly learned words only
                            matchedWordIds.forEach { wordId ->
                                if (!learnedWords.contains(wordId)) {
                                    learningProgressViewModel.incrementWordsLearned()
                                    learnedWords = learnedWords + wordId
                                }
                            }
                        }
                    )
                    PracticeMode.FLASHCARD -> FlashcardPractice(
                        vocabulary = vocabularyList[currentIndex],
                        onNext = {
                            if (currentIndex < vocabularyList.size - 1) {
                                // Only count as learned if user marks it as known
                                if (!learnedWords.contains(vocabularyList[currentIndex].id)) {
                                    learningProgressViewModel.incrementWordsLearned()
                                    learnedWords = learnedWords + vocabularyList[currentIndex].id
                                }
                                currentIndex++
                                learningProgressViewModel.updateProgress(true)
                            } else {
                                showResults = true
                            }
                        },
                        onPrevious = {
                            if (currentIndex > 0) {
                                currentIndex--
                            }
                        },
                        onMarkAsLearned = { isLearned ->
                            val wordId = vocabularyList[currentIndex].id
                            if (isLearned && !learnedWords.contains(wordId)) {
                                learningProgressViewModel.incrementWordsLearned()
                                learnedWords = learnedWords + wordId
                            }
                            learningProgressViewModel.updateProgress(isLearned)
                        }
                    )
                    null -> {}
                }
            } else {
                // Results
                Text(
                    text = "Practice Complete!",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Score: $score/${vocabularyList.size}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Text(
                    text = "Words Learned: ${learnedWords.size}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Button(
                    onClick = {
                        selectedMode = null
                        currentIndex = 0
                        score = 0
                        showResults = false
                        learnedWords = setOf()
                    }
                ) {
                    Text("Try Again")
                }
            }
        }
    }
}

@Composable
fun MultipleChoicePractice(
    vocabulary: Vocabulary,
    options: List<String>,
    onAnswer: (Boolean) -> Unit
) {
    // Ensure the correct answer is included and shuffle the options
    val shuffledOptions = remember(options, vocabulary) {
        val otherOptions = options.filter { it != vocabulary.meaning }
        val randomOptions = otherOptions.shuffled().take(3)
        (randomOptions + vocabulary.meaning).shuffled()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = vocabulary.word,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        shuffledOptions.forEach { option ->
            Button(
                onClick = { onAnswer(option == vocabulary.meaning) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(option)
            }
        }
    }
}

@Composable
fun FillInBlankPractice(
    vocabulary: Vocabulary,
    onAnswer: (Boolean) -> Unit
) {
    var answer by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = vocabulary.meaning,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        OutlinedTextField(
            value = answer,
            onValueChange = { answer = it },
            label = { Text("Enter the word") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        Button(
            onClick = { 
                val isCorrect = answer.trim().equals(vocabulary.word, ignoreCase = true)
                onAnswer(isCorrect)
                answer = "" // Reset input field after checking
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Check Answer")
        }
    }
}

@Composable
fun MatchingPractice(
    vocabularyList: List<Vocabulary>,
    onComplete: (Int, Set<Long>) -> Unit
) {
    var selectedWord by remember { mutableStateOf<Vocabulary?>(null) }
    var selectedMeaning by remember { mutableStateOf<String?>(null) }
    var matchedPairs by remember { mutableStateOf(setOf<Pair<Vocabulary, String>>()) }
    
    // Get practice history from preferences
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("practice_history", Context.MODE_PRIVATE) }
    
    // Get words that haven't been practiced recently
    val unpracticedWords = remember(vocabularyList) {
        vocabularyList.filter { word ->
            val lastPracticed = prefs.getLong("last_practiced_${word.word}", 0)
            System.currentTimeMillis() - lastPracticed > 24 * 60 * 60 * 1000 // 24 hours
        }
    }
    
    // Get recently practiced words
    val recentlyPracticedWords = remember(vocabularyList) {
        vocabularyList.filter { word ->
            val lastPracticed = prefs.getLong("last_practiced_${word.word}", 0)
            System.currentTimeMillis() - lastPracticed <= 24 * 60 * 60 * 1000 // 24 hours
        }
    }
    
    // Select 10 words for current session
    val currentSessionWords = remember(vocabularyList, unpracticedWords, recentlyPracticedWords) {
        val words = mutableListOf<Vocabulary>()
        // Add unpracticed words first
        words.addAll(unpracticedWords.take(10))
        // If we need more words, add from recently practiced
        if (words.size < 10) {
            words.addAll(recentlyPracticedWords.shuffled().take(10 - words.size))
        }
        words.shuffled()
    }
    
    // Shuffle meanings separately
    val shuffledMeanings = remember(currentSessionWords) {
        currentSessionWords.map { it.meaning }.shuffled()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Match words with their meanings",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Words Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = "Words",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(currentSessionWords.filter { word ->
                        !matchedPairs.any { it.first == word }
                    }) { word ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedWord = word
                                    if (selectedMeaning != null) {
                                        if (selectedMeaning == word.meaning) {
                                            matchedPairs = matchedPairs + (word to word.meaning)
                                            // Update practice history
                                            prefs.edit().putLong("last_practiced_${word.word}", System.currentTimeMillis()).apply()
                                        }
                                        selectedMeaning = null
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedWord == word) 
                                    MaterialTheme.colorScheme.primaryContainer 
                                else 
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                text = word.word,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
            
            // Meanings Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = "Meanings",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(shuffledMeanings.filter { meaning ->
                        !matchedPairs.any { it.second == meaning }
                    }) { meaning ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedMeaning = meaning
                                    if (selectedWord != null) {
                                        if (selectedWord?.meaning == meaning) {
                                            matchedPairs = matchedPairs + (selectedWord!! to meaning)
                                            // Update practice history
                                            prefs.edit().putLong("last_practiced_${selectedWord!!.word}", System.currentTimeMillis()).apply()
                                        }
                                        selectedWord = null
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedMeaning == meaning) 
                                    MaterialTheme.colorScheme.primaryContainer 
                                else 
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                text = meaning,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
        
        if (matchedPairs.size == currentSessionWords.size) {
            Button(
                onClick = { onComplete(matchedPairs.size, matchedPairs.map { it.first.id }.toSet()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Complete")
            }
        }
    }
}

@Composable
fun FlashcardPractice(
    vocabulary: Vocabulary,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onMarkAsLearned: (Boolean) -> Unit
) {
    var showMeaning by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Instructions
        Text(
            text = "Tap the card to flip between word and meaning",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Flashcard
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { showMeaning = !showMeaning }
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (showMeaning) 
                    MaterialTheme.colorScheme.primaryContainer 
                else 
                    MaterialTheme.colorScheme.surface
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (showMeaning) vocabulary.meaning else vocabulary.word,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                    if (showMeaning && vocabulary.pronunciation != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pronunciation: ${vocabulary.pronunciation}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (showMeaning && vocabulary.example.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Example: ${vocabulary.example}",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { 
                    showMeaning = false  // Reset to show English word
                    onPrevious()
                },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Previous Card")
            }
            Button(
                onClick = { 
                    showMeaning = false  // Reset to show English word
                    onNext()
                },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Next Card")
            }
        }
    }
} 