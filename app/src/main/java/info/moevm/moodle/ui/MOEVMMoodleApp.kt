package info.moevm.moodle.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import info.moevm.moodle.api.DataStoreMoodleUser
import info.moevm.moodle.api.MoodleApi
import info.moevm.moodle.data.AppContainer
import info.moevm.moodle.data.courses.CourseManager
import info.moevm.moodle.data.courses.CoursesRepository
import info.moevm.moodle.data.posts.PostsRepository
import info.moevm.moodle.ui.article.ArticleScreen
import info.moevm.moodle.ui.components.StatisticsTopAppBar
import info.moevm.moodle.ui.coursescontent.CourseContentScreen
import info.moevm.moodle.ui.entersetup.EnterSetupScreen
import info.moevm.moodle.ui.home.HomeScreen
import info.moevm.moodle.ui.interests.InterestsScreen
import info.moevm.moodle.ui.lessoncontent.TestAttemptsScreen
import info.moevm.moodle.ui.lessoncontent.TestScreen
import info.moevm.moodle.ui.settings.SettingsScreen
import info.moevm.moodle.ui.signin.PreviewScreen
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

    val context = LocalContext.current
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
            fullNameMoodleUserString =
                moodleProfileDataStore.getFullNameCurrent()
            cityMoodleUserString = moodleProfileDataStore.getCityCurrent()
            countryMoodleUserString = moodleProfileDataStore.getCountryCurrent()
        }
        fullNameMoodleUser.value = fullNameMoodleUserString
        cityMoodleUser.value = cityMoodleUserString
        countryMoodleUser.value = countryMoodleUserString
    }

    // Нужно, чтобы "привязать" индексы к экранам
    val courseId = remember { mutableStateOf(0) }
    val categoryLessonItemIndex = remember { mutableStateOf(0) }
    val lessonItemIndex = remember { mutableStateOf(0) }
    val taskItemIndex = remember { mutableStateOf(0) }
    val testAttemptId = remember { mutableStateOf(0) }

    val courseManager = CourseManager(
        token = "", // инициализируется после входа в аккаунт в PreviewScreen
        moodleApi = MoodleApi(),
        courseId = courseId,
        categoryLessonItemIndex = categoryLessonItemIndex,
        lessonItemIndex = lessonItemIndex,
        taskItemIndex = taskItemIndex,
        testAttemptId = testAttemptId
    )

    Crossfade(navController.currentBackStackEntryAsState()) {
        Surface(color = MaterialTheme.colors.background) {
            NavHost(navController, startDestination = ScreenName.PREVIEW.name) {
                composable(ScreenName.PREVIEW.name) {
                    PreviewScreen(
                        courseManager = courseManager,
                        navigateTo = actions.select
                    )
                }
                composable(ScreenName.TEST.name) {
                    TestScreen(
                        courseManager = courseManager,
                        onBackPressed = actions.upPress
                    )
                }
                composable(ScreenName.TEST_ATTEMPTS.name) {
                    TestAttemptsScreen(
                        courseManager = courseManager,
                        navigateTo = actions.select,
                        onBackPressed = actions.upPress
                    )
                }
                composable(ScreenName.ARTICLE.name) {
                    info.moevm.moodle.ui.lessoncontent.ArticleScreen(
                        courseManager = courseManager,
                        onBackPressed = actions.upPress
                    )
                }
                composable(ScreenName.COURSE_CONTENT.name) {
                    CourseContentScreen(
                        courseManager = courseManager,
                        navigateTo = actions.select,
                        onBackPressed = actions.upPress
                    )
                }
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
                        fullNameMoodleUser = fullNameMoodleUser,
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
                        courseManager = courseManager,
                        coursesRepository = coursesRepository,
                        scaffoldState = scaffoldState
                    )
                }
                composable(ScreenName.FAKE_ARTICLE.name + "/{${Screen.ArticleArgs.PostId}}") {
                    val postId =
                        requireNotNull(it.arguments?.getString(Screen.ArticleArgs.PostId))
                    ArticleScreen(
                        postId = postId,
                        postsRepository = postsRepository,
                        onBack = actions.upPress
                    )
                }
                composable(ScreenName.STATISTICS.name) {
                    val allScreens =
                        SettingsScreenForStatistics.values().toList()
                    var currentScreen by rememberSaveable {
                        mutableStateOf(
                            SettingsScreenForStatistics.Overview
                        )
                    }
                    val coroutineScope = rememberCoroutineScope()
                    Scaffold(
                        scaffoldState = scaffoldState,
                        topBar = {
                            StatisticsTopAppBar(
                                allScreens = allScreens,
                                onTabSelected = { screen ->
                                    currentScreen = screen
                                },
                                currentScreen = currentScreen,
                                scaffoldState = scaffoldState
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
                            currentScreen.Content(
                                onScreenChange = { screen ->
                                    currentScreen = screen
                                }
                            )
                        }
                    }
                }
                composable(ScreenName.SETTINGS.name) {
                    SettingsScreen(
                        navigateTo = actions.select,
                        scaffoldState = scaffoldState,
                    )
                }
                composable(ScreenName.ADD.name) {
                    // Unused
                    // Баг Jetpack Compose, без этого возникает исключение ArrayIndexOutOfBoundsException
                }
            }
        }
    }
}
