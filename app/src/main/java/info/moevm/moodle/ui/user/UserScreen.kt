package info.moevm.moodle.ui.user

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Bottom
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
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
                        text = "${stringResource(id = R.string.hello)}, ${stringResource(id = R.string.user_name)}",
                        textAlign = TextAlign.Justify,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
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
                        // FIXME: can't access `current`
                        GlobalScope.launch {
                            DataStoreUser(context).addUser("", "", "")
                            Toast.makeText(context, "logOUT", Toast.LENGTH_SHORT).show()
                        }
                        // TODO: maybe we have navigate to the sign in screen?
                        // onSignInSubmitted(Screen.Home)
//                        navigateTo(Screen.SignIn)
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
        CircularImage(
            image = imageResource(id = R.drawable.popov),
            modifier = Modifier.preferredSize(120.dp)
        )
        Spacer(modifier = Modifier.preferredHeight(64.dp))
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
        UserScreen(navigateTo = { }, scaffoldState = scaffoldState)
    }
}

@Preview(name = "User screen in dark theme")
@Composable
fun UserPreviewDark() {
    MOEVMMoodleTheme(darkTheme = true) {
        val scaffoldState = rememberScaffoldState(
            drawerState = rememberDrawerState(DrawerValue.Closed)
        )
        UserScreen(navigateTo = { }, scaffoldState = scaffoldState)
    }
}
