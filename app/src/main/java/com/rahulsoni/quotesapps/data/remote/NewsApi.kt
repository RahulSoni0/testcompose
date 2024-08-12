package com.rahulsoni.quotesapps.data.remote

import com.rahulsoni.quotesapps.domain.Article
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String
    ): NewsResponse

    companion object {
        const val BASE_URL = "https://newsapi.org/v2/"
    }
}

data class ArticleDto(
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?
)

fun ArticleDto.toArticle(): Article {
    return Article(
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage
    )
}

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<ArticleDto>
)

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val error: String) : ApiResponse<Nothing>()
    data object Loading : ApiResponse<Nothing>()
}