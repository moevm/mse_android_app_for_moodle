package info.moevm.moodle.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import info.moevm.moodle.R
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.components.CircularImage
import info.moevm.moodle.ui.theme.MOEVMMoodleTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserScreen(
    navigateTo: (Screen) -> Unit
) {
    Scaffold(
        topBar = {
            UserScreenTopAppBar(
                topAppBarText = "${stringResource(id = R.string.hello)}, ${stringResource(id = R.string.user_name)}",
            )
        },
        bodyContent = {
            Column(modifier = Modifier.fillMaxWidth()) {
                UserContent(
                    onNavigate = navigateTo
                )
            }
        }
    )
}

/**
 * A greeting to the user
 */
@Composable
fun UserScreenTopAppBar(topAppBarText: String) {
    TopAppBar(
        title = {
            Text(
                text = topAppBarText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
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
                // Go to setup screen
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
        UserScreen {}
    }
}

@Preview(name = "User screen in dark theme")
@Composable
fun UserPreviewDark() {
    MOEVMMoodleTheme(darkTheme = true) {
        UserScreen {}
    }
}
