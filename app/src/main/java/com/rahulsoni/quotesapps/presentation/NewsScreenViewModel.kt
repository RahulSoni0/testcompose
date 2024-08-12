package com.rahulsoni.quotesapps.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahulsoni.quotesapps.data.remote.ApiResponse
import com.rahulsoni.quotesapps.domain.Article
import com.rahulsoni.quotesapps.domain.GetTopHeadlinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class NewsScreenViewModel @Inject constructor(
    private val getTopHeadLines: GetTopHeadlinesUseCase
) : ViewModel() {

    var uiState by mutableStateOf(NewsUiState())
        private set

    var uiSideEffect by mutableStateOf<NewsSideEffects>(NewsSideEffects.None)
        private set

    private val _searchQuery = MutableStateFlow("us")

    init {

    }

    private fun debounceSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)  // Adjust debounce time as needed
                .distinctUntilChanged()
                .collectLatest { query ->
                    fetchNews(query)
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        uiState = uiState.copy(searchQuery = query)
        _searchQuery.value = query
        debounceSearchQuery()
    }

    private suspend fun fetchNews(query: String) {
        // Replace with actual data fetching logic
        getTopHeadLines.invoke(query).catch {}.collect {
            when (it) {
                is ApiResponse.Error -> {
                    Log.d(TAG, "getNewsList: ${it.error}")
                }

                ApiResponse.Loading -> {
                    uiState = uiState.copy(isLoading = false)
                }

                is ApiResponse.Success -> {
                    uiState = uiState.copy(isLoading = false, articles = it.data)
                }
            }
        }
    }

    fun onEvent(events: NewsEvents) {
        when (events) {
            NewsEvents.OnBackClick -> {
                uiSideEffect = NewsSideEffects.Back
            }

            is NewsEvents.OnNewsClick -> {
                uiSideEffect = NewsSideEffects.OnNewsClick(events.newsItem)
            }

            NewsEvents.OnReload -> {
                getNewsList()
            }
        }
    }

    fun getNewsList(country: String = "us") {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            getTopHeadLines.invoke(country)
                .catch {
                }.collect {
                    when (it) {
                        is ApiResponse.Error -> {
                            Log.d(TAG, "getNewsList: ${it.error}")
                        }

                        ApiResponse.Loading -> {
                            uiState = uiState.copy(isLoading = false)
                        }

                        is ApiResponse.Success -> {
                            uiState = uiState.copy(isLoading = false, articles = it.data)
                        }
                    }
                }

        }
    }

    companion object {
        private val TAG = "NewsScreenViewModel"
    }

}


data class NewsUiState(
    val isLoading: Boolean = false,
    val articles: List<Article>? = emptyList(),
    val searchQuery: String = ""
)

sealed interface NewsEvents {
    data class OnNewsClick(val newsItem: Article) : NewsEvents
    data object OnBackClick : NewsEvents
    data object OnReload : NewsEvents
}

sealed interface NewsSideEffects {
    data object None : NewsSideEffects
    data object Back : NewsSideEffects
    data object Reload : NewsSideEffects
    data class OnNewsClick(val newsItem: Article) : NewsSideEffects
}