package info.moevm.moodle.ui

import androidx.navigation.NavHostController
import info.moevm.moodle.ui.Screen.*
import info.moevm.moodle.ui.ScreenName.*

/**
 * Screen names (used for serialization)
 */
enum class ScreenName { ENTER_SETUP, SIGN_IN, HOME, USER, INTERESTS, STATISTICS, SETTINGS, COURSE_CONTENT, FAKE_ARTICLE, PREVIEW_TEST, TEST, ARTICLE, PREVIEW, ADD }

/**
 * Class defining the screens we have in the app:
 * - enter setup;
 * - sign in;
 * - home;
 * - article details;
 * - interests
 */
sealed class Screen(val id: ScreenName) {
    object EnterSetup : Screen(ENTER_SETUP)
    object SignIn : Screen(SIGN_IN)
    object Home : Screen(HOME)
    object Interests : Screen(INTERESTS)
    object User : Screen(USER)
    object Statistics : Screen(STATISTICS)
    object Settings : Screen(SETTINGS)
    object CourseContent : Screen(COURSE_CONTENT)
    object Article : Screen(ARTICLE)
    object PreviewQuiz : Screen(PREVIEW_TEST)
    object Test : Screen(TEST)
    object Preview : Screen(PREVIEW)
    object Add : Screen(ADD)
    data class FakeArticle(val postId: String) : Screen(FAKE_ARTICLE)

    object ArticleArgs {
        const val PostId = "postId"
    }
}

class Actions(navController: NavHostController) {
    val select: (Screen) -> Unit = { screen ->

        when (screen) {
            is FakeArticle -> {
                navController.navigate("${screen.id.name}/${screen.postId}")
            }
            else -> {
                navController.popBackStack(
                    navController.graph.startDestinationId,
                    navController.currentDestination?.id != navController.graph.startDestinationId
                )
                navController.navigate(screen.id.name)
            }
        }
    }

    val upPress: () -> Unit = {
        navController.popBackStack()
    }
}
