package com.rahulsoni.quotesapps.domain

import com.rahulsoni.quotesapps.data.remote.ApiResponse
import com.rahulsoni.quotesapps.data.remote.ArticleDto
import kotlinx.coroutines.flow.Flow


interface NewsRepository {
    fun getTopHeadlines(country: String): Flow<ApiResponse<List<Article>>>
}