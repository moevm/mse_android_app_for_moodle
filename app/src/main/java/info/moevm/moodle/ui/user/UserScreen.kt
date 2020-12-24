package info.moevm.moodle.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import info.moevm.moodle.R
import info.moevm.moodle.api.DataStoreUser
import info.moevm.moodle.ui.AppDrawer
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.components.CircularImage
import info.moevm.moodle.ui.theme.MOEVMMoodleTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserScreen(
    fullNameMoodleProfile: String,
    cityMoodleProfile: String,
    countryMoodleProfile: String,
    navigateTo: (Screen) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.User,
                closeDrawer = { scaffoldState.drawerState.close() },
                navigateTo = navigateTo
            )
        },
        topBar = {
            TopAppBar(
                modifier = Modifier.testTag("topAppBarHome"),
                title = {
                    Text(
                        text = "${stringResource(id = R.string.hello)}, $fullNameMoodleProfile",
                        textAlign = TextAlign.Justify,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                            .padding(end = 16.dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.testTag("appDrawer"),
                        onClick = { scaffoldState.drawerState.open() },
                    ) {
                        Icon(vectorResource(R.drawable.ic_logo_light))
                    }
                },
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp
            )
        },
        bodyContent = {
            Column(modifier = Modifier.fillMaxWidth()) {
                UserContent(
                    cityMoodleProfile,
                    countryMoodleProfile,
                    onNavigate = navigateTo
                )
            }

            val context = AmbientContext.current

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        GlobalScope.launch {
                            DataStoreUser(context).addUser("", "", "")
                        }
                        navigateTo(Screen.SignIn)
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 8.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.log_out)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalFocus::class)
@Composable
fun UserContent(
    cityMoodleProfile: String,
    countryMoodleProfile: String,
    onNavigate: (Screen) -> Unit
) {
    Column(
        modifier = Modifier.padding(
            start = 10.dp,
            end = 10.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.preferredHeight(16.dp))
        CircularImage(
            image = imageResource(id = R.drawable.avatar),
            modifier = Modifier.preferredSize(120.dp)
        )
        Spacer(modifier = Modifier.preferredHeight(16.dp))
        Text(
            text = "$cityMoodleProfile, $countryMoodleProfile",
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.h5
        )
        Spacer(modifier = Modifier.preferredHeight(32.dp))
        Button(
            onClick = {
                onNavigate(Screen.Home)
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        ) {
            Text(
                text = stringResource(id = R.string.all_courses)
            )
        }
        Spacer(modifier = Modifier.preferredHeight(16.dp))
        Button(
            onClick = {
                // TODO: Go to setup screen
                onNavigate(Screen.Home)
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        ) {
            Text(
                text = stringResource(id = R.string.setups)
            )
        }
    }
}

@Preview(name = "User screen light theme")
@Composable
fun UserPreview() {
    MOEVMMoodleTheme {
        val scaffoldState = rememberScaffoldState(
            drawerState = rememberDrawerState(DrawerValue.Closed)
        )
        UserScreen(navigateTo = { }, scaffoldState = scaffoldState, fullNameMoodleProfile = "", cityMoodleProfile = "", countryMoodleProfile = "")
    }
}

@Preview(name = "User screen in dark theme")
@Composable
fun UserPreviewDark() {
    MOEVMMoodleTheme(darkTheme = true) {
        val scaffoldState = rememberScaffoldState(
            drawerState = rememberDrawerState(DrawerValue.Closed)
        )
        UserScreen(navigateTo = { }, scaffoldState = scaffoldState, fullNameMoodleProfile = "", cityMoodleProfile = "", countryMoodleProfile = "")
    }
}
