package info.moevm.moodle.ui

import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import info.moevm.moodle.ui.Screen.*
import info.moevm.moodle.ui.ScreenName.*

/**
 * Screen names (used for serialization)
 */
enum class ScreenName { SIGN_IN, HOME, INTERESTS, ARTICLE }

/**
 * Class defining the screens we have in the app:
 * - sign in;
 * - home;
 * - article details;
 * - interests
 */
sealed class Screen(val id: ScreenName) {
    object SignIn : Screen(SIGN_IN)
    object Home : Screen(HOME)
    object Interests : Screen(INTERESTS)
    data class Article(val postId: String) : Screen(ARTICLE)

    object ArticleArgs {
        const val PostId = "postId"
    }
}

class Actions(navController: NavHostController) {
    val select: (Screen) -> Unit = { screen ->

        when (screen) {
            is Article -> {
                navController.navigate("${screen.id.name}/${screen.postId}")
            }
            else -> {
                navController.popBackStack(
                    navController.graph.startDestination,
                    navController.currentDestination?.id != navController.graph.startDestination
                )
                navController.navigate(screen.id.name)
            }
        }
    }

    val upPress: () -> Unit = {
        navController.popBackStack()
    }
}