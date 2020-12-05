package info.moevm.moodle.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import info.moevm.moodle.data.AppContainer
import info.moevm.moodle.data.courses.CoursesRepository
import info.moevm.moodle.data.posts.PostsRepository
import info.moevm.moodle.ui.article.ArticleScreen
import info.moevm.moodle.ui.components.StatisticsTopAppBar
import info.moevm.moodle.ui.home.HomeScreen
import info.moevm.moodle.ui.interests.InterestsScreen
import info.moevm.moodle.ui.settings.SettingsScreen
import info.moevm.moodle.ui.signin.SignInScreen
import info.moevm.moodle.ui.signin.TokenAuthScreen
import info.moevm.moodle.ui.statistics.SettingsScreenForStatistics
import info.moevm.moodle.ui.theme.MOEVMMoodleTheme
import info.moevm.moodle.ui.user.UserScreen

@Composable
fun MOEVMMoodleApp(appContainer: AppContainer) {
    MOEVMMoodleTheme {
        AppContent(
            coursesRepository = appContainer.coursesRepository,
            postsRepository = appContainer.postsRepository
        )
    }
}

@Composable
private fun AppContent(
    postsRepository: PostsRepository,
    coursesRepository: CoursesRepository
) {
    val navController = rememberNavController()
    val actions = remember(navController) { Actions(navController) }
    val scaffoldState = rememberScaffoldState()

    Crossfade(navController.currentBackStackEntryAsState()) {
        Surface(color = MaterialTheme.colors.background) {
            NavHost(navController, startDestination = ScreenName.TOKEN_AUTH.name) {
                composable(ScreenName.TOKEN_AUTH.name) {
                    TokenAuthScreen(
                        navigateTo = actions.select
                    )
                }
                composable(ScreenName.SIGN_IN.name) {
                    SignInScreen(
                        navigateTo = actions.select
                    )
                }
                composable(ScreenName.HOME.name) {
                    HomeScreen(
                        navigateTo = actions.select,
                        postsRepository = postsRepository,
                        scaffoldState = scaffoldState
                    )
                }
                composable(ScreenName.USER.name) {
                    UserScreen(
                        navigateTo = actions.select,
                        scaffoldState = scaffoldState
                    )
                }
                composable(ScreenName.INTERESTS.name) {
                    InterestsScreen(
                        navigateTo = actions.select,
                        coursesRepository = coursesRepository,
                        scaffoldState = scaffoldState
                    )
                }
                composable(ScreenName.ARTICLE.name + "/{${Screen.ArticleArgs.PostId}}") {
                    val postId =
                        requireNotNull(it.arguments?.getString(Screen.ArticleArgs.PostId))
                    ArticleScreen(
                        postId = postId,
                        postsRepository = postsRepository,
                        onBack = actions.upPress
                    )
                }
                composable(ScreenName.STATISTICS.name) {
                    val allScreens = SettingsScreenForStatistics.values().toList()
                    var currentScreen by savedInstanceState { SettingsScreenForStatistics.Overview }
                    Scaffold(
                        topBar = {
                            StatisticsTopAppBar(
                                allScreens = allScreens,
                                onTabSelected = { screen -> currentScreen = screen },
                                currentScreen = currentScreen
                            )
                        }
                    ) { innerPadding ->
                        Box(Modifier.padding(innerPadding)) {
                            currentScreen.Content(onScreenChange = { screen -> currentScreen = screen })
                        }
                    }
                }
                composable(ScreenName.SETTINGS.name) {
                    SettingsScreen(
                        navigateTo = actions.select,
                        scaffoldState = scaffoldState,
                    )
                }
            }
        }
    }
}
