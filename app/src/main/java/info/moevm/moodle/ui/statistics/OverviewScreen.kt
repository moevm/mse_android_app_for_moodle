package info.moevm.moodle.ui.statistics

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import info.moevm.moodle.R
import info.moevm.moodle.data.statistics.UserData
import info.moevm.moodle.model.Post
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.SwipeToRefreshLayout
import info.moevm.moodle.ui.components.*
import info.moevm.moodle.ui.state.UiState
import java.util.*

@Composable
fun OverviewScreen(
    navigateTo: (Screen) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {

    OverviewScreen(
        navigateTo = navigateTo,
        scaffoldState = scaffoldState
    )
}

@Suppress("unused")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OverviewScreen(
    posts: UiState<List<Post>>,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: () -> Unit,
    navigateTo: (Screen) -> Unit,
    scaffoldState: ScaffoldState
) {
    if (posts.hasError) {
        val errorMessage = stringResource(id = R.string.load_error)
        val retryMessage = stringResource(id = R.string.retry)

        // Show snackbar using a coroutine, when the coroutine is cancelled the snackbar will
        // automatically dismiss. This coroutine will cancel whenever posts.hasError changes, and
        // only start when posts.hasError is true (due to the above if-check).
        LaunchedEffect(posts.hasError) {
            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = retryMessage
            )
            when (snackbarResult) {
                SnackbarResult.ActionPerformed -> onRefreshPosts()
                SnackbarResult.Dismissed -> onErrorDismiss()
            }
        }
    }

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

@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
    } else {
        SwipeToRefreshLayout(
            refreshingState = loading,
            onRefresh = onRefresh,
            refreshIndicator = {
                Surface(elevation = 10.dp, shape = CircleShape) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .preferredSize(36.dp)
                            .padding(4.dp)
                    )
                }
            },
            content = content,
        )
    }
}

@Composable
fun OverviewBody(onScreenChange: (SettingsScreenForStatistics) -> Unit = {}) {
    ScrollableColumn(contentPadding = PaddingValues(16.dp)) {
        AlertCard()
        Spacer(Modifier.preferredHeight(StatisticsDefaultPadding))
        CoursesCard(onScreenChange)
        Spacer(Modifier.preferredHeight(StatisticsDefaultPadding))
        StudentsCard(onScreenChange)
    }
}

@Composable
private fun AlertCard() {
    var showDialog by remember { mutableStateOf(false) }

    /**
     * TODO:
     * Add alert message for some statistics reasons.
     * For example, for outdated students for course
     */
    val alertMessage = stringResource(R.string.alert_message)

    if (showDialog) {
        StatisticsAlertDialog(
            onDismiss = {
                showDialog = false
            },
            bodyText = alertMessage,
            buttonText = "Dismiss".toUpperCase(Locale.ROOT)
        )
    }
    Card {
        Column {
            AlertHeader {
                showDialog = true
            }
            StatisticsDivider(
                modifier = Modifier.padding(start = StatisticsDefaultPadding, end = StatisticsDefaultPadding)
            )
            AlertItem(alertMessage)
        }
    }
}

@Composable
private fun AlertHeader(onClickSeeAll: () -> Unit) {
    Row(
        modifier = Modifier.padding(StatisticsDefaultPadding).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Providers(
            AmbientContentAlpha provides ContentAlpha.high
        ) {
            Text(
                text = stringResource(R.string.alert),
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        TextButton(
            onClick = onClickSeeAll,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(
                text = stringResource(R.string.see_all),
                style = MaterialTheme.typography.button,
            )
        }
    }
}

@Composable
private fun AlertItem(message: String) {
    Row(
        modifier = Modifier.padding(StatisticsDefaultPadding),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Providers(
            AmbientContentAlpha provides ContentAlpha.high
        ) {
            Text(
                style = MaterialTheme.typography.body2,
                modifier = Modifier.weight(1f),
                text = message
            )
            IconButton(
                onClick = {},
                modifier = Modifier.align(Alignment.Top)
            ) {
                Icon(Icons.Filled.Sort)
            }
        }
    }
}

/**
 * Base structure for cards in the Overview screen.
 */

@Composable
private fun <T> OverviewScreenCard(
    title: String,
    amount: Float,
    onClickSeeAll: () -> Unit,
    values: (T) -> Float,
    colors: (T) -> Color,
    data: List<T>,
    row: @Composable (T) -> Unit
) {
    Card {
        Column {
            Column(Modifier.padding(StatisticsDefaultPadding)) {
                Text(text = title, style = MaterialTheme.typography.subtitle2)
                val amountText = stringResource(R.string.amount) + formatAmount(
                    amount
                )
                Text(text = amountText, style = MaterialTheme.typography.h2)
            }
            OverViewDivider(data, values, colors)
            Column(Modifier.padding(start = 16.dp, top = 4.dp, end = 8.dp)) {
                data.take(SHOWN_ITEMS).forEach { row(it) }
                SeeAllButton(onClick = onClickSeeAll)
            }
        }
    }
}

@Composable
private fun <T> OverViewDivider(
    data: List<T>,
    values: (T) -> Float,
    colors: (T) -> Color
) {
    Row(Modifier.fillMaxWidth()) {
        data.forEach { item: T ->
            Spacer(
                modifier = Modifier
                    .weight(values(item))
                    .preferredHeight(1.dp)
                    .background(colors(item))
            )
        }
    }
}

/**
 * The Courses card
 */
@Composable
private fun CoursesCard(onScreenChange: (SettingsScreenForStatistics) -> Unit) {
    val amount = UserData.courses.map { courses -> courses.percent }.sum()
    OverviewScreenCard(
        title = stringResource(R.string.accounts),
        amount = amount,
        onClickSeeAll = {
            onScreenChange(SettingsScreenForStatistics.Courses)
        },
        data = UserData.courses,
        colors = { it.color },
        values = { it.percent }
    ) { course ->
        SuccessCoursesRow(
            name = course.name,
            number = course.number,
            percent = course.percent,
            color = course.color
        )
    }
}
/**
 * The Students card
 */
@Composable
private fun StudentsCard(onScreenChange: (SettingsScreenForStatistics) -> Unit) {
    val amount = UserData.students.map { students -> students.amount }.sum()
    OverviewScreenCard(
        title = stringResource(R.string.bills),
        amount = amount * 1f,
        onClickSeeAll = {
            onScreenChange(SettingsScreenForStatistics.Students)
        },
        data = UserData.students,
        colors = { it.color },
        values = { it.amount * 1f }
    ) { student ->
        SuccessStudentsRow(
            name = student.course,
            mark = student.mark,
            amount = student.amount,
            color = student.color
        )
    }
}

@Composable
private fun SeeAllButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.preferredHeight(44.dp).fillMaxWidth()
    ) {
        Text(stringResource(R.string.see_all))
    }
}

private val StatisticsDefaultPadding = 12.dp

private const val SHOWN_ITEMS = 3
