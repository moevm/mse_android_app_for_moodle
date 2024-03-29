package info.moevm.moodle.ui.entersetup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import info.moevm.moodle.R
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.entersetup.checksetup.FiledEnter
import info.moevm.moodle.ui.entersetup.checksetup.TokenState
import info.moevm.moodle.ui.entersetup.checksetup.UrlState
import info.moevm.moodle.ui.theme.MOEVMMoodleTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EnterSetupScreen(
    navigateTo: (Screen) -> Unit
) {
    Scaffold(
        topBar = {
            EnterSetupScreenTopAppBar(
                topAppBarText = stringResource(id = R.string.token_url_setup),
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EnterSetupPartScreen(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    EnterSetupContent(
                        onSettingsSubmitted = navigateTo
                    )
                }
            }
        }
    }
}

@Composable
fun EnterSetupScreenTopAppBar(topAppBarText: String) {
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

@Composable
fun EnterSetupContent(
    onSettingsSubmitted: (Screen) -> Unit
) {

    Column(modifier = Modifier.fillMaxWidth()) {

        val tokenState = remember { TokenState() }
        val urlState = remember { UrlState() }
        FiledEnter(
            fieldState = tokenState,
            onImeAction = {
                onSettingsSubmitted(Screen.SignIn)
            },
            labelVal = stringResource(id = R.string.token)
        )
        Spacer(modifier = Modifier.height(16.dp))
        FiledEnter(
            fieldState = urlState,
            onImeAction = {
                onSettingsSubmitted(Screen.SignIn)
            },
            labelVal = stringResource(id = R.string.url)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.enter_screen_info),
            style = MaterialTheme.typography.body2
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                onSettingsSubmitted(Screen.SignIn)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.current_setup_use)
            )
        }
    }
}

@Composable
fun EnterSetupPartScreen(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(Modifier.verticalScroll(scrollState)) {
        Spacer(modifier = Modifier.height(44.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            content()
        }
    }
}

@Preview(name = "Enter Setup screen in light theme")
@Composable
fun EnterSetupPreview() {
    MOEVMMoodleTheme {
        EnterSetupScreen {}
    }
}

@Preview(name = "Enter Setup screen in dark theme")
@Composable
fun EnterSetupPreviewDark() {
    MOEVMMoodleTheme(darkTheme = true) {
        EnterSetupScreen {}
    }
}
