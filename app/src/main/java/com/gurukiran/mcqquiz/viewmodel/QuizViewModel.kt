package com.gurukiran.mcqquiz.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gurukiran.mcqquiz.data.model.Question
import com.gurukiran.mcqquiz.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first

private val Application.dataStore by preferencesDataStore(name = "quiz_prefs")

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = QuizRepository(application.applicationContext)

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedAnswer = MutableStateFlow<Int?>(null)
    val selectedAnswer: StateFlow<Int?> = _selectedAnswer

    private val _showAnswer = MutableStateFlow(false)
    val showAnswer: StateFlow<Boolean> = _showAnswer

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    private val _skipped = MutableStateFlow(0)
    val skipped: StateFlow<Int> = _skipped

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak

    private val _longestStreak = MutableStateFlow(0)
    val longestStreak: StateFlow<Int> = _longestStreak

    private val _sessionLongestStreak = MutableStateFlow(0)
    val sessionLongestStreak: StateFlow<Int> = _sessionLongestStreak

    private val LONGEST_KEY = intPreferencesKey("longest_streak")

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline

    private var wasOfflineLoad = false

    init {
        loadLongestStreak()
        loadQuestions()
    }

    private fun loadLongestStreak() {
        viewModelScope.launch {
            val prefs = getApplication<Application>().dataStore.data.first()
            _longestStreak.value = prefs[LONGEST_KEY] ?: 0
        }
    }

    private suspend fun saveLongestStreak(value: Int) {
        getApplication<Application>().dataStore.edit { prefs ->
            prefs[LONGEST_KEY] = value
        }
    }

    @SuppressLint("MissingPermission")
    fun observeInternet(context: Context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isOffline.value = false
                if (wasOfflineLoad) loadQuestions() // auto refresh when online
            }

            override fun onLost(network: Network) {
                _isOffline.value = true
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    @SuppressLint("ServiceCast")
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }


    fun loadQuestions() {
        viewModelScope.launch {
            _loading.value = true
            val res = repo.getQuestions()
            res.onSuccess { quizResult ->
                _questions.value = quizResult.questions
                _isOffline.value = false
                wasOfflineLoad = false
            }.onFailure {
                val fallback = repo.loadFromAssetsFallback()
                if (fallback.isNotEmpty()) {
                    _questions.value = fallback
                    _isOffline.value = true
                    wasOfflineLoad = true
                } else {
                    _error.value = "No Internet & No cached questions!"
                }
            }

            _loading.value = false
        }
    }

    fun selectAnswer(index: Int) {
        if (_showAnswer.value) return
        _selectedAnswer.value = index
        _showAnswer.value = true

        val q = _questions.value.getOrNull(_currentIndex.value) ?: return
        if (index == q.answerIndex) {
            _score.value++
            _streak.value++

            if (_streak.value > _sessionLongestStreak.value) {
                _sessionLongestStreak.value = _streak.value
            }

            if (_streak.value > _longestStreak.value) {
                _longestStreak.value = _streak.value
                viewModelScope.launch { saveLongestStreak(_longestStreak.value) }
            }

        } else {
            _streak.value = 0
        }

        viewModelScope.launch {
            delay(1200)
            moveNext()
        }
    }

    fun skipQuestion() {
        if (_showAnswer.value) return
        _skipped.value++
        _streak.value = 0
        moveNext()
    }

    private fun moveNext() {
        _selectedAnswer.value = null
        _showAnswer.value = false
        _currentIndex.value++
    }

    fun restartQuiz() {
        _score.value = 0
        _skipped.value = 0
        _streak.value = 0
        _sessionLongestStreak.value = 0
        _selectedAnswer.value = null
        _showAnswer.value = false
        _currentIndex.value = 0
    }
}
