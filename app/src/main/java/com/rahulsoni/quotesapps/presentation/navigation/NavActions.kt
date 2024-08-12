package com.rahulsoni.quotesapps.presentation.navigation

import androidx.navigation.NavController
import com.rahulsoni.quotesapps.domain.Article

class NavActions(private val navController: NavController) {

    fun handleNewsScreenActions(actions: NewsScreenActions) {
        when (actions) {
            NewsScreenActions.Back -> {
                navController.popBackStack()
            }

            is NewsScreenActions.NewsDetails -> {
                navController.navigate("details")
            }
        }
    }

    fun handleDetailsScreenActions(actions: DetailsScreenActions) {
        when (actions) {
            DetailsScreenActions.Back -> {
                navController.popBackStack()
            }
        }
    }

}

sealed class NewsScreenActions {
    data object Back : NewsScreenActions()
    data class NewsDetails(val article: Article) : NewsScreenActions()
}

sealed class DetailsScreenActions {
    data object Back : DetailsScreenActions()
}