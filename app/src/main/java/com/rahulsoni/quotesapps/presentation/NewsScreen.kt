package com.rahulsoni.quotesapps.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rahulsoni.quotesapps.R
import com.rahulsoni.quotesapps.domain.Article
import com.rahulsoni.quotesapps.presentation.navigation.NewsScreenActions
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    viewModel: NewsScreenViewModel = hiltViewModel(), onActions: (NewsScreenActions) -> Unit
) {
    val uiState = viewModel.uiState

    val onEvent = remember(key1 = viewModel) {
        return@remember viewModel::onEvent
    }

    val pullToRefreshState = rememberPullToRefreshState()
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            delay(1000)
            viewModel.getNewsList("cn")
            pullToRefreshState.endRefresh()
        }
    }
    LaunchedEffect(true) {
        viewModel.getNewsList()
    }
    LaunchedEffect(key1 = viewModel.uiSideEffect) {
        handleSideEffect(viewModel.uiSideEffect, onActions)
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(pullToRefreshState.nestedScrollConnection)) {
        NewsList(viewModel, onEvent)
        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = pullToRefreshState,
        )
    }
}

fun handleSideEffect(uiSideEffect: NewsSideEffects, actions: (NewsScreenActions) -> Unit) {

    when (uiSideEffect) {
        NewsSideEffects.Back -> {
            actions.invoke(NewsScreenActions.Back)
        }

        NewsSideEffects.None -> {

        }

        is NewsSideEffects.OnNewsClick -> {
            actions.invoke(NewsScreenActions.NewsDetails(uiSideEffect.newsItem))
        }

        NewsSideEffects.Reload -> {

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsList(viewModel: NewsScreenViewModel, onEvent: (NewsEvents) -> Unit) {
    val uiState = viewModel.uiState
    if (uiState.isLoading) {
        CircularProgressIndicator()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(query = uiState.searchQuery, onQueryChanged = { query ->
            viewModel.onSearchQueryChanged(query)
        })
        if (uiState.articles?.isNotEmpty() == true) {
            LazyColumn {
                itemsIndexed(
                    uiState.articles,
                    key = { _, item -> item.title }) { index, newsItem ->
                    NewsItem(newsItem) { onEvent(NewsEvents.OnNewsClick(newsItem)) }
                    if (index < uiState.articles.size - 1) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}


@Composable
fun NewsItem(article: Article, onClick: () -> Unit = {}) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable { onClick() }) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            article.urlToImage?.let { imageUrl ->
                AsyncImage(
                    model = article.urlToImage,
                    contentDescription = "image icon",
                    error = painterResource(R.drawable.baseline_fireplace_24),
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterVertically),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(
                    text = article.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.description.orEmpty(),
                    fontSize = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChanged: (String) -> Unit) {
    TextField(value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Search articles...") })
}
