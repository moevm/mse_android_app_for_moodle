package info.moevm.moodle.ui

import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
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
import info.moevm.moodle.ui.components.*
import info.moevm.moodle.ui.state.UiState
import java.util.*

@Composable
fun OverviewScreen(
        navigateTo: (Screen) -> Unit,
        scaffoldState: ScaffoldState = rememberScaffoldState()
) {

    val coroutineScope = rememberCoroutineScope()

    OverviewScreen(
            navigateTo = navigateTo,
            scaffoldState = scaffoldState
    )
}

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
        LaunchedTask(posts.hasError) {
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

    /**
     * A Scaffold is a layout which implements the basic material design layout structure
     */
    val allScreens = SettingsScreen.values().toList()
    var currentScreen by savedInstanceState { SettingsScreen.Overview }
    Scaffold(
            topBar = {
                RallyTopAppBar(
                        allScreens = allScreens,
                        onTabSelected = { screen -> currentScreen = screen },
                        currentScreen = currentScreen
                )
            }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            currentScreen.content(onScreenChange = { screen -> currentScreen = screen })
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
fun OverviewBody(onScreenChange: (SettingsScreen) -> Unit = {}) {
    ScrollableColumn(contentPadding = PaddingValues(16.dp)) {
        AlertCard()
        Spacer(Modifier.preferredHeight(RallyDefaultPadding))
        AccountsCard(onScreenChange)
        Spacer(Modifier.preferredHeight(RallyDefaultPadding))
        BillsCard(onScreenChange)
    }
}

/**
 * The Alerts card within the Rally Overview screen.
 */
@Composable
private fun AlertCard() {
    var showDialog by remember { mutableStateOf(false) }
    val alertMessage = "Heads up, you've used up 90% of your Shopping budget for this month."

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
                modifier = Modifier.padding(start = RallyDefaultPadding, end = RallyDefaultPadding)
            )
            AlertItem(alertMessage)
        }
    }
}

@Composable
private fun AlertHeader(onClickSeeAll: () -> Unit) {
    Row(
        modifier = Modifier.padding(RallyDefaultPadding).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.high) {
            Text(
                text = "Alerts",
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
                text = "SEE ALL",
                style = MaterialTheme.typography.button,
            )
        }
    }
}

@Composable
private fun AlertItem(message: String) {
    Row(
        modifier = Modifier.padding(RallyDefaultPadding),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.high) {
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
            Column(Modifier.padding(RallyDefaultPadding)) {
                Text(text = title, style = MaterialTheme.typography.subtitle2)
                val amountText = "Кол-во" + formatAmount(
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
 * The Accounts card within the Rally Overview screen.
 */
@Composable
private fun AccountsCard(onScreenChange: (SettingsScreen) -> Unit) {
    val amount = UserData.courses.map { account -> account.percent }.sum()
    OverviewScreenCard(
        title = stringResource(R.string.accounts),
        amount = amount,
        onClickSeeAll = {
            onScreenChange(SettingsScreen.Courses)
        },
        data = UserData.courses,
        colors = { it.color },
        values = { it.percent }
    ) { account ->
        SuccessCoursesRow(
            name = account.name,
            number = account.number,
            percent = account.percent,
            color = account.color
        )
    }
}

/**
 * The Bills card within the Rally Overview screen.
 */
@Composable
private fun BillsCard(onScreenChange: (SettingsScreen) -> Unit) {
    val amount = UserData.students.map { bill -> bill.amount }.sum()
    OverviewScreenCard(
        title = stringResource(R.string.bills),
        amount = amount * 1f,
        onClickSeeAll = {
            onScreenChange(SettingsScreen.Students)
        },
        data = UserData.students,
        colors = { it.color },
        values = { it.amount * 1f }
    ) { bill ->
        SuccessStudentsRow(
            name = bill.course,
            mark = bill.mark,
            amount = bill.amount,
            color = bill.color
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

private val RallyDefaultPadding = 12.dp

private const val SHOWN_ITEMS = 3