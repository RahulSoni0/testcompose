package com.rahulsoni.quotesapps.data.remote

import com.rahulsoni.quotesapps.domain.Article
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoteNewsDataSource @Inject constructor(
    private val api: NewsApi
) {
    fun getTopHeadlines(country: String, apikey: String): Flow<ApiResponse<List<ArticleDto>>> = flow {
        try {
            val response = api.getTopHeadlines(country, apikey)
            if (response.status == "ok") {
                emit(ApiResponse.Success(response.articles))
            } else {
                emit(ApiResponse.Error("Unexpected status: ${response.status}"))
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.localizedMessage ?: "Unknown error"))
        }
    }
}