package com.gurukiran.mcqquiz.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gurukiran.mcqquiz.data.cache.AssetProvider
import com.gurukiran.mcqquiz.data.model.Question
import com.gurukiran.mcqquiz.data.model.QuizResult
import com.gurukiran.mcqquiz.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class QuizRepository(private val context: Context) {
    private val api = RetrofitClient.api
    private val assets = AssetProvider(context)

    suspend fun getQuestions(): Result<QuizResult> = withContext(Dispatchers.IO) {
        try {
            val resp = api.fetchQuestions()
            if (resp.isSuccessful) {
                val body = resp.body()
                if (!body.isNullOrEmpty()) {
                    return@withContext Result.success(
                        QuizResult(questions = body, fromCache = false)
                    )
                }
            }

            val fallback = assets.loadQuestionsFromAssets()
            if (fallback.isNotEmpty()) {
                return@withContext Result.success(
                    QuizResult(questions = fallback, fromCache = true)
                )
            }

            Result.failure(HttpException(resp))

        } catch (e: IOException) {
            val fallback = assets.loadQuestionsFromAssets()
            if (fallback.isNotEmpty()) {
                Result.success(
                    QuizResult(questions = fallback, fromCache = true)
                )
            } else Result.failure(e)

        } catch (e: Exception) {
            val fallback = assets.loadQuestionsFromAssets()
            if (fallback.isNotEmpty()) {
                Result.success(
                    QuizResult(questions = fallback, fromCache = true)
                )
            } else Result.failure(e)
        }
    }

    fun loadFromAssetsFallback(): List<Question> {
        val json = context.assets.open("questions.json")
            .bufferedReader()
            .use { it.readText() }
        return Gson().fromJson(json, object : TypeToken<List<Question>>() {}.type)
    }
}
