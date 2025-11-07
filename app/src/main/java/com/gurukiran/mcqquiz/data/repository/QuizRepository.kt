package com.gurukiran.mcqquiz.data.repository

import android.content.Context
import com.gurukiran.mcqquiz.data.cache.AssetProvider
import com.gurukiran.mcqquiz.data.model.Question
import com.gurukiran.mcqquiz.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class QuizRepository(private val context: Context) {
    private val api = RetrofitClient.api
    private val assets = AssetProvider(context)

    suspend fun getQuestions(): Result<List<Question>> = withContext(Dispatchers.IO) {
        try {
            val resp = api.fetchQuestions()
            if (resp.isSuccessful) {
                val body = resp.body()
                if (!body.isNullOrEmpty()) return@withContext Result.success(body)
            }
            val fallback = assets.loadQuestionsFromAssets()
            if (fallback.isNotEmpty()) Result.success(fallback)
            else Result.failure(HttpException(resp))
        } catch (e: IOException) {
            val fallback = assets.loadQuestionsFromAssets()
            if (fallback.isNotEmpty()) Result.success(fallback)
            else Result.failure(e)
        } catch (e: Exception) {
            val fallback = assets.loadQuestionsFromAssets()
            if (fallback.isNotEmpty()) Result.success(fallback)
            else Result.failure(e)
        }
    }
}
