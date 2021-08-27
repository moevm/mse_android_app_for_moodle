package info.moevm.moodle.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserScreen(
    fullNameMoodleProfile: String,
    cityMoodleProfile: String,
    countryMoodleProfile: String,
    navigateTo: (Screen) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.User,
                closeDrawer = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
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
                        onClick = {
                            coroutineScope.launch {
                                scaffoldState.drawerState.open()
                            }
                        }
                    ) {
                        Icon(ImageVector.vectorResource(R.drawable.ic_logo_light), null)
                    }
                },
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp
            )
        }){
        Column(modifier = Modifier.fillMaxWidth()) {
            UserContent(
                cityMoodleProfile,
                countryMoodleProfile,
                onNavigate = navigateTo
            )
        }

        val context = LocalContext.current

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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 8.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.log_out)
                )
            }
        }
    }
}

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
        Spacer(modifier = Modifier.height(16.dp))
        CircularImage(
            image = ImageBitmap.imageResource(id = R.drawable.avatar),
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "$cityMoodleProfile, $countryMoodleProfile",
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.h5
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                onNavigate(Screen.Home)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        ) {
            Text(
                text = stringResource(id = R.string.all_courses)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // TODO: Go to setup screen
                onNavigate(Screen.Home)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
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
