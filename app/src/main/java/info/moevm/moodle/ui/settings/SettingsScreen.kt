package info.moevm.moodle.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import info.moevm.moodle.R
import info.moevm.moodle.data.posts.PostsRepository
import info.moevm.moodle.ui.AppDrawer
import info.moevm.moodle.ui.Screen
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private fun Modifier.brandingPreferredHeight(
    showBranding: Boolean,
    heightDp: Dp
): Modifier {
    return if (!showBranding) {
        this
            .wrapContentHeight(unbounded = true)
            .height(heightDp)
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
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Settings,
                closeDrawer = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
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
                        onClick = {
                            coroutineScope.launch {
                                scaffoldState.drawerState.open()
                            }
                        }
                    ) {
                        Icon(ImageVector.vectorResource(id = R.drawable.ic_logo_light), null)
                    }
                }
            )
        }) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SettingsContent(
                    onNavigate = navigateTo
                )
            }
        }
}

@Composable
fun SettingsContent(
    onNavigate: (Screen) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(
                top = 10.dp,
                start = 10.dp,
                end = 10.dp
            )
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Switch(
            checked = false,
            onCheckedChange = { },
            // colors = defaultColors{Color.Black, Color.Black, 0.54f, Color.LTGRAY, Color.LTGRAY, 0.38f, }

        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}
