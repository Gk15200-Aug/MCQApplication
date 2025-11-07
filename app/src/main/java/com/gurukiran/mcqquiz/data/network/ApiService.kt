package com.gurukiran.mcqquiz.data.network

import com.gurukiran.mcqquiz.data.model.Question
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("53846277a8fcb034e482906ccc0d12b2/raw")
    suspend fun fetchQuestions(): Response<List<Question>>
}
