package com.rahulsoni.quotesapps.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rahulsoni.quotesapps.presentation.DetailsScreen
import com.rahulsoni.quotesapps.presentation.NewsScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(), startDestination: String = "news"
) {
    val navActions = NavActions(navController)

    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        composable("news") {
            NewsScreen(onActions = navActions::handleNewsScreenActions)
        }

        composable("details") { backStackEntry ->
            DetailsScreen(onActions = navActions::handleDetailsScreenActions)
        }
    }
}

