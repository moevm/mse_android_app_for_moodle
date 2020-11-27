package info.moevm.moodle.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import info.moevm.moodle.R
import info.moevm.moodle.data.posts.PostsRepository
import info.moevm.moodle.ui.AppDrawer
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.theme.changeTheme

private fun Modifier.brandingPreferredHeight(
    showBranding: Boolean,
    heightDp: Dp
): Modifier {
    return if (!showBranding) {
        this.wrapContentHeight(unbounded = true)
            .preferredHeight(heightDp)
    } else {
        this
    }
}
@Composable
fun SettingsScreen(
    navigateTo: (Screen) -> Unit,
    postsRepository: PostsRepository,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    SettingsScreen(
        navigateTo = navigateTo,
        scaffoldState = scaffoldState
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable

fun SettingsScreen(
    navigateTo: (Screen) -> Unit,
    scaffoldState: ScaffoldState
) {

    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Settings,
                closeDrawer = { scaffoldState.drawerState.close() },
                navigateTo = navigateTo
            )
        },
        topBar = {
            val title = stringResource(id = R.string.settings_name)
            TopAppBar(
                modifier = Modifier.testTag("topAppBarSettings"),
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.testTag("appDrawer"),
                        onClick = { scaffoldState.drawerState.open() },
                    ) {
                        Icon(vectorResource(R.drawable.ic_logo_light))
                    }
                }
            )
        },
        bodyContent = {
            Column(modifier = Modifier.fillMaxWidth()) {
                SettingsContent(
                    onNavigate = navigateTo
                )
            }
        }
    )
}

@OptIn(ExperimentalFocus::class)
@Composable
fun SettingsContent(
    isDarkTheme: Boolean = false,
    onNavigate: (Screen) -> Unit
) {
    Column(
        modifier = Modifier.padding(
            top = 10.dp,
            start = 10.dp,
            end = 10.dp
        ).fillMaxWidth().wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.preferredHeight(16.dp))
        Button(
            onClick = {
                changeTheme()
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        ) {
            Text(
                text = stringResource(id = R.string.change_theme)
            )
        }
        Spacer(modifier = Modifier.preferredHeight(16.dp))
    }
}
