package info.moevm.moodle.ui.signin

import android.util.Log
import androidx.compose.animation.animate
import androidx.compose.foundation.AmbientContentColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import info.moevm.moodle.R
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.signin.authorization.Email
import info.moevm.moodle.ui.signin.authorization.EmailState
import info.moevm.moodle.ui.signin.authorization.Password
import info.moevm.moodle.ui.signin.authorization.PasswordState
import info.moevm.moodle.ui.theme.MOEVMMoodleTheme
import info.moevm.moodle.ui.theme.snackbarAction

sealed class SignInEvent {
    data class SignIn(val email: String, val password: String) : SignInEvent()
    object NavigateBack : SignInEvent()
}

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
private fun Branding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Logo(modifier = Modifier.align(Alignment.CenterHorizontally).padding(horizontal = 76.dp))
        ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.high) {
            Text(
                text = stringResource(id = R.string.app_tagline),
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 24.dp).fillMaxWidth()
            )
        }
    }
}

@Composable
private fun Logo(
    lightTheme: Boolean = MaterialTheme.colors.isLight,
    modifier: Modifier = Modifier
) {
    val assetId = if (lightTheme) {
        R.drawable.ic_logo_light
    } else {
        R.drawable.ic_logo_dark
    }
    Image(
        asset = vectorResource(id = assetId),
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SignInScreen(
        navigateTo: (Screen) -> Unit
) {

    Log.i("!@#", "SignInScreen function called")

    val snackbarHostState = remember { SnackbarHostState() }

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
            SignInSignUpTopAppBar(
                topAppBarText = stringResource(id = R.string.sign_in),
                // TODO: process back btn press, maybe here (see Jetservey example)
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
                SignInSignUpScreen(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SignInContent(
                            onSignInSubmitted = { email, password ->
                                SignInEvent.SignIn(email, password)
                            }
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
fun SignInSignUpTopAppBar(topAppBarText: String) {
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
fun SignInContent(
    onSignInSubmitted: (email: String, password: String) -> Unit,
) {
    Log.i("!@#", "SignInContent function called")

    Column(modifier = Modifier.fillMaxWidth()) {
        val focusRequester = remember { FocusRequester() }
        val emailState = remember { EmailState() }
        Email(emailState, onImeAction = { focusRequester.requestFocus() })

        Spacer(modifier = Modifier.preferredHeight(16.dp))

        val passwordState = remember { PasswordState() }
        Password(
            label = stringResource(id = R.string.password),
            passwordState = passwordState,
            modifier = Modifier.focusRequester(focusRequester),
            onImeAction = { onSignInSubmitted(emailState.text, passwordState.text) }
        )
        Spacer(modifier = Modifier.preferredHeight(16.dp))
        Button(
            onClick = { onSignInSubmitted(emailState.text, passwordState.text) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            enabled = emailState.isValid && passwordState.isValid
        ) {
            Text(
                text = stringResource(id = R.string.sign_in)
            )
        }
    }
}

@Composable
fun SignInSignUpScreen(
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

@Preview(name = "Sign in light theme")
@Composable
fun SignInPreview() {
    MOEVMMoodleTheme {
        SignInScreen(navigateTo = {})
    }
}

@Preview(name = "Sign in dark theme")
@Composable
fun SignInPreviewDark() {
    MOEVMMoodleTheme(darkTheme = true) {
        SignInScreen(navigateTo = {})
    }
}