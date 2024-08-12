package com.rahulsoni.quotesapps.data.remote

import com.rahulsoni.quotesapps.domain.Article
import com.rahulsoni.quotesapps.domain.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteNewsDataSource
) : NewsRepository {

    override fun getTopHeadlines(country: String): Flow<ApiResponse<List<Article>>> = flow {
        emit(ApiResponse.Loading)
        remoteDataSource.getTopHeadlines(country, "494d770a8bdd4babb6b3c1d23bda5846")
            .collect { response ->
                when (response) {
                    is ApiResponse.Success -> {    // Save raw DTOs to local database // Emit raw DTOs
                        emit(ApiResponse.Success(response.data.map { it.toArticle() }))
                    }

                    is ApiResponse.Error -> emit(ApiResponse.Error(response.error))
                    ApiResponse.Loading -> emit(ApiResponse.Loading)
                }
            }
    }
}

