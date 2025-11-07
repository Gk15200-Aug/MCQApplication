package com.gurukiran.mcqquiz.data.model

data class QuizResult(
    val questions: List<Question>,
    val fromCache: Boolean
)
