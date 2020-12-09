package info.moevm.moodle.ui.signin

import android.util.Log
import androidx.compose.animation.animate
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
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

sealed class SignInEvent {
    data class SignIn(val email: String, val password: String) : SignInEvent()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SignInScreen(
    navigateTo: (Screen) -> Unit
) {

    // FIXME: return it - just remove warning
    // val snackbarHostState = remember { SnackbarHostState() }

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
//                            onSignInSubmitted = { email, password ->
//                                SignInEvent.SignIn(email, password)
//                            }
                            onSignInSubmitted = navigateTo
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
//    onSignInSubmitted: (email: String, password: String) -> Unit
    onSignInSubmitted: (Screen) -> Unit
) {
    val apiclient = ApiActivity()

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
            onImeAction = {
//                onSignInSubmitted(emailState.text, passwordState.text)
                //todo add check here to!!!!
                onSignInSubmitted(Screen.Home)
            }
        )
        Spacer(modifier = Modifier.preferredHeight(16.dp))
        Button(
            onClick = {
                val userName = emailState.text
                val userPassword = passwordState.text
                val data = apiclient.checkLogIn("moodle_mobile_app", userName, userPassword)
                if(data?.token!= null){
                    //todo toast
                    Log.d(TAG, "you are welcome");
                    onSignInSubmitted(Screen.Home)
                } else{
                    Log.d(TAG, data?.error.toString())
                }
//                onSignInSubmitted(emailState.text, passwordState.text)
            },
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
        SignInScreen {}
    }
}

@Preview(name = "Sign in dark theme")
@Composable
fun SignInPreviewDark() {
    MOEVMMoodleTheme(darkTheme = true) {
        SignInScreen {}
    }
}
