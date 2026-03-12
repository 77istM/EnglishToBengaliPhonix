package com.example.englishtobengaliphonix

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TracingScreen(
    viewModel: TracingViewModel = viewModel()
) {
    val userPath         by viewModel.userPath.collectAsState()
    val isPlaying        by viewModel.isPlaying.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val currentLetter    = viewModel.currentLetter

    val letterPath = remember(currentLetter) { scalePath(currentLetter.mainPath) }
    val guidePaths = remember(currentLetter) { currentLetter.guidePaths.map { scalePath(it) } }

    LaunchedEffect(currentLetter) {
        viewModel.playSound()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Category selector bar ────────────────────────────────────────────
        CategorySelector(
            selectedCategory = selectedCategory,
            onCategorySelected = { viewModel.selectCategory(it) }
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // ── Main tracing content ─────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LetterHeader(
                letterName = currentLetter.name,
                isPlaying = isPlaying,
                onPlayClick = { viewModel.playSound() }
            )

            TracingCanvas(
                letterPath = letterPath,
                guidePaths = guidePaths,
                userPath = userPath,
                onDragStart = { offset -> viewModel.startPath(offset) },
                onDrag      = { position -> viewModel.dragTo(position) },
                onDragEnd   = { /* future stroke-end logic */ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            NavigationButtons(
                onPrevious = { viewModel.previousLetter() },
                onClear    = { viewModel.clearPath() },
                onNext     = { viewModel.nextLetter() }
            )
        }
    }
}