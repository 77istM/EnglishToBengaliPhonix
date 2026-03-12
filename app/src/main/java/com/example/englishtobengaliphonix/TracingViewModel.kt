package com.example.englishtobengaliphonix

import android.app.Application
import android.media.MediaPlayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TracingViewModel(application: Application) : AndroidViewModel(application) {

    private val letters = AlphabetPaths.letters

    // ── Category ranges (match order in AlphabetPaths.letters) ──────────────
    // Vowels:     অ → ঔ  (indices 0–10,  11 letters)
    // Consonants: ক → ঁ  (indices 11–49, 39 letters)
    // Numbers:    ০ → ১০ (indices 50–60, 11 items)
    private val rangeFor = mapOf(
        LetterCategory.ALL         to (0..letters.lastIndex),
        LetterCategory.VOWELS      to (0..10),
        LetterCategory.CONSONANTS  to (11..49),
        LetterCategory.NUMBERS     to (50..letters.lastIndex)
    )

    // ── State ────────────────────────────────────────────────────────────────
    private val _selectedCategory = MutableStateFlow(LetterCategory.ALL)
    val selectedCategory: StateFlow<LetterCategory> = _selectedCategory.asStateFlow()

    private val _currentLetterIndex = MutableStateFlow(0)
    val currentLetterIndex: StateFlow<Int> = _currentLetterIndex.asStateFlow()

    private val _userPath = MutableStateFlow(Path())
    val userPath: StateFlow<Path> = _userPath.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null

    val currentLetter get() = letters[_currentLetterIndex.value]

    // ── Category selection ───────────────────────────────────────────────────
    fun selectCategory(category: LetterCategory) {
        _selectedCategory.value = category
        // Jump to the first letter of the chosen category
        _currentLetterIndex.value = rangeFor[category]!!.first
        clearPath()
    }

    // ── Audio ────────────────────────────────────────────────────────────────
    fun playSound() {
        currentLetter.audioResId?.let { resId ->
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(getApplication(), resId)
            _isPlaying.value = true
            mediaPlayer?.setOnCompletionListener {
                _isPlaying.value = false
            }
            mediaPlayer?.start()
        }
    }

    // ── Navigation (bounded to active category) ──────────────────────────────
    fun nextLetter() {
        val range = rangeFor[_selectedCategory.value]!!
        val next = _currentLetterIndex.value + 1
        _currentLetterIndex.value = if (next > range.last) range.first else next
        clearPath()
    }

    fun previousLetter() {
        val range = rangeFor[_selectedCategory.value]!!
        val prev = _currentLetterIndex.value - 1
        _currentLetterIndex.value = if (prev < range.first) range.last else prev
        clearPath()
    }

    // ── Drawing ──────────────────────────────────────────────────────────────
    fun clearPath() {
        _userPath.value = Path()
    }

    fun startPath(offset: Offset) {
        val newPath = Path().apply {
            addPath(_userPath.value)
            moveTo(offset.x, offset.y)
        }
        _userPath.value = newPath
    }

    fun dragTo(position: Offset) {
        val newPath = Path().apply {
            addPath(_userPath.value)
            lineTo(position.x, position.y)
        }
        _userPath.value = newPath
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}