package com.gurukiran.mcqquiz.data.cache

import android.content.Context
import com.gurukiran.mcqquiz.data.model.Question
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class AssetProvider(private val context: Context) {
    fun loadQuestionsFromAssets(): List<Question> {
        return try {
            val json = context.assets.open("questions.json").bufferedReader().use { it.readText() }
            val moshi = Moshi.Builder().build()
            val type = Types.newParameterizedType(List::class.java, Question::class.java)
            val adapter = moshi.adapter<List<Question>>(type)
            adapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
