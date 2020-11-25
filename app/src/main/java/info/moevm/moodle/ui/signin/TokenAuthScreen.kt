package info.moevm.moodle.ui.signin

import androidx.compose.animation.animate
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focusRequester
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import info.moevm.moodle.R
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.signin.authorization.Email
import info.moevm.moodle.ui.signin.authorization.EmailState
import info.moevm.moodle.ui.signin.authorization.Password
import info.moevm.moodle.ui.signin.authorization.PasswordState
import info.moevm.moodle.ui.theme.MOEVMMoodleTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TokenAuthScreen(
    navigateTo: (Screen) -> Unit
) {

    // FIXME: return it - just remove warning
    // val snackbarHostState = remember { SnackbarHostState() }

    // Parameters for branding stuff
    var brandingBottom by remember { mutableStateOf(0f) }
    val showBranding by remember { mutableStateOf(true) }
    var heightWithBranding by remember { mutableStateOf(0) }

    val currentOffsetHolder = remember { mutableStateOf(0f) }
    currentOffsetHolder.value = animate(
        if (showBranding) 0f else -brandingBottom
    )
    val heightDp = with(DensityAmbient.current) { heightWithBranding.toDp() }

    Scaffold(
        topBar = {
            TokenAuthScreenTopAppBar(
                topAppBarText = stringResource(id = R.string.token_auth),
            )
        },
        bodyContent = {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .brandingPreferredHeight(showBranding, heightDp)
                    .offsetPx(y = currentOffsetHolder)
                    .onSizeChanged {
                        if (showBranding) {
                            heightWithBranding = it.height
                        }
                    }
            ) {
                Branding(
                    modifier = Modifier.fillMaxWidth().weight(1f).onGloballyPositioned {
                        if (brandingBottom == 0f) {
                            brandingBottom = it.boundsInParent.bottom
                        }
                    }
                )
                TokenAuthPartScreen(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        TokenAuthContent(
                            //onSignInSubmitted = { email, password ->
                            //    SignInEvent.SignIn(email, password)
                            //}
                            onAuthSubmitted = navigateTo
                        )
                    }
                }
            }
        }
    )
}

/**
 * Just "Sign In" text on the top bar of the app
 */
@Composable
fun TokenAuthScreenTopAppBar(topAppBarText: String) {
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
fun TokenAuthContent(
    // Maybe another navigation as in sign in
    // onSignInSubmitted: (email: String, password: String) -> Unit
    onAuthSubmitted: (Screen) -> Unit
) {

    Column(modifier = Modifier.fillMaxWidth()) {
        val focusRequester = remember { FocusRequester() }

        // Just use ready password class for token handling
        val passwordState = remember { PasswordState() }
        Password(
            label = stringResource(id = R.string.token),
            passwordState = passwordState,
            modifier = Modifier.focusRequester(focusRequester),
            onImeAction = {
                // HANDLE passwordState.text HERE
                onAuthSubmitted(Screen.SignIn)
            }
        )
        Spacer(modifier = Modifier.preferredHeight(16.dp))
        Button(
            onClick = {
                // HANDLE passwordState.text HERE
                // as example: onSignInSubmitted(emailState.text, passwordState.text)
                onAuthSubmitted(Screen.SignIn)
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.token_use)
            )
        }
    }
}

@Composable
fun TokenAuthPartScreen(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ScrollableColumn(modifier = modifier) {
        Spacer(modifier = Modifier.preferredHeight(44.dp))
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            content()
        }
    }
}

@Preview(name = "TokenAuthScreen in light theme")
@Composable
fun TokenAuthPreview() {
    MOEVMMoodleTheme {
        TokenAuthScreen {}
    }
}

@Preview(name = "TokenAuthScreen in dark theme")
@Composable
fun TokenAuthPreviewDark() {
    MOEVMMoodleTheme(darkTheme = true) {
        TokenAuthScreen {}
    }
}
