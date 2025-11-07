package com.gurukiran.mcqquiz.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Question(
    val id: Int,
    val question: String,
    val options: List<String>,
    @Json(name = "correctOptionIndex")
    val answerIndex: Int
)
