package info.moevm.moodle.ui

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import info.moevm.moodle.api.DataStoreMoodleUser
import info.moevm.moodle.data.AppContainer
import info.moevm.moodle.data.courses.CoursesRepository
import info.moevm.moodle.data.posts.PostsRepository
import info.moevm.moodle.ui.article.ArticleScreen
import info.moevm.moodle.ui.components.StatisticsTopAppBar
import info.moevm.moodle.ui.entersetup.EnterSetupScreen
import info.moevm.moodle.ui.home.HomeScreen
import info.moevm.moodle.ui.interests.InterestsScreen
import info.moevm.moodle.ui.settings.SettingsScreen
import info.moevm.moodle.ui.signin.SignInScreen
import info.moevm.moodle.ui.statistics.SettingsScreenForStatistics
import info.moevm.moodle.ui.theme.MOEVMMoodleTheme
import info.moevm.moodle.ui.user.UserScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@Composable
fun MOEVMMoodleApp(appContainer: AppContainer) {
    MOEVMMoodleTheme {
        AppContent(
            coursesRepository = appContainer.coursesRepository,
            postsRepository = appContainer.postsRepository
        )
    }
}

@SuppressLint("UnusedCrossfadeTargetStateParameter")
@Composable
private fun AppContent(
    postsRepository: PostsRepository,
    coursesRepository: CoursesRepository
) {
    val navController = rememberNavController()
    val actions = remember(navController) { Actions(navController) }
    val scaffoldState = rememberScaffoldState()

    val context = LocalContext.current
    val lifeSO = context.applicationContext
    val moodleProfileDataStore = DataStoreMoodleUser(lifeSO)

    val fullNameMoodleUser: String
    val cityMoodleUser: String
    val countryMoodleUser: String

    runBlocking {
        withContext(Dispatchers.IO) {
            fullNameMoodleUser = moodleProfileDataStore.getFullNameCurrent()
            cityMoodleUser = moodleProfileDataStore.getCityCurrent()
            countryMoodleUser = moodleProfileDataStore.getCountryCurrent()
        }
    }

    Crossfade(navController.currentBackStackEntryAsState()) {
        Surface(color = MaterialTheme.colors.background) {
            NavHost(navController, startDestination = ScreenName.SIGN_IN.name) {
                composable(ScreenName.ENTER_SETUP.name) {
                    EnterSetupScreen(
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
                        scaffoldState = scaffoldState,
                        fullNameMoodleProfile = fullNameMoodleUser,
                        cityMoodleProfile = cityMoodleUser,
                        countryMoodleProfile = countryMoodleUser
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
                    var currentScreen by rememberSaveable { mutableStateOf(SettingsScreenForStatistics.Overview) }
                    val coroutineScope = rememberCoroutineScope()
                    Scaffold(
                        scaffoldState = scaffoldState,
                        topBar = {
                            StatisticsTopAppBar(
                                scaffoldState = scaffoldState,
                                allScreens = allScreens,
                                onTabSelected = { screen -> currentScreen = screen },
                                currentScreen = currentScreen
                            )
                        },
                        drawerContent = {
                            AppDrawer(
                                currentScreen = Screen.Statistics,
                                closeDrawer = {
                                    coroutineScope.launch {
                                        scaffoldState.drawerState.close()
                                    }
                                },
                                navigateTo = actions.select
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
