package info.moevm.moodle.ui

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
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

@Composable
private fun AppContent(
    postsRepository: PostsRepository,
    coursesRepository: CoursesRepository
) {
    val navController = rememberNavController()
    val actions = remember(navController) { Actions(navController) }
    val scaffoldState = rememberScaffoldState()

    fun Context.lifecycleOwner(): LifecycleOwner? {
        var curContext = this
        var maxDepth = 20
        while (maxDepth-- > 0 && curContext !is LifecycleOwner) {
            curContext = (curContext as ContextWrapper).baseContext
        }
        return if (curContext is LifecycleOwner) {
            curContext
        } else {
            null
        }
    }

    val context = AmbientContext.current
    val lifeSO = context.applicationContext
    val moodleProfileDataStore = DataStoreMoodleUser(lifeSO)
    val lifeCO = context.lifecycleOwner()

    val fullNameMoodleUser = MutableLiveData<String>()
    val cityMoodleUser = MutableLiveData<String>()
    val countryMoodleUser = MutableLiveData<String>()
    fullNameMoodleUser.observe(lifeCO!!) { }
    cityMoodleUser.observe(lifeCO) { }
    countryMoodleUser.observe(lifeCO) { }

    runBlocking {
        val fullNameMoodleUserString: String
        val cityMoodleUserString: String
        val countryMoodleUserString: String
        withContext(Dispatchers.IO) {
            fullNameMoodleUserString = moodleProfileDataStore.getFullNameCurrent()
            cityMoodleUserString = moodleProfileDataStore.getCityCurrent()
            countryMoodleUserString = moodleProfileDataStore.getCountryCurrent()
        }
        fullNameMoodleUser.value = fullNameMoodleUserString
        cityMoodleUser.value = cityMoodleUserString
        countryMoodleUser.value = countryMoodleUserString
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
                        navigateTo = actions.select,
                        fullNameMoodleUser = fullNameMoodleUser,
                        cityMoodleUser = cityMoodleUser,
                        countryMoodleUser = countryMoodleUser
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
                    var currentScreen by savedInstanceState { SettingsScreenForStatistics.Overview }
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
                                closeDrawer = { scaffoldState.drawerState.close() },
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
