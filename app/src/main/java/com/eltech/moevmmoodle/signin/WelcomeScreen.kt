package com.eltech.moevmmoodle.signin

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animate
import androidx.compose.foundation.Box
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.contentColor
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
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Constraints.Companion.Infinity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.ui.tooling.preview.Preview
import com.eltech.moevmmoodle.R
import com.eltech.moevmmoodle.com.eltech.moevmmoodle.signin.authorization.Email
import com.eltech.moevmmoodle.com.eltech.moevmmoodle.signin.authorization.EmailState
import com.eltech.moevmmoodle.com.eltech.moevmmoodle.signin.authorization.Password
import com.eltech.moevmmoodle.com.eltech.moevmmoodle.signin.authorization.PasswordState
import com.eltech.moevmmoodle.theme.MoodleTheme
import com.eltech.moevmmoodle.theme.snackbarAction
import kotlinx.coroutines.delay

sealed class SignInEvent {
    data class SignIn(val email: String, val password: String) : SignInEvent()
    object NavigateBack : SignInEvent()
}

@Composable
fun SignIn(onNavigationEvent: (SignInEvent) -> Unit) {
    val showSnackbar = remember { mutableStateOf(false) }
    Stack(modifier = Modifier.fillMaxSize()) {
        ErrorSnackbar(
            showError = showSnackbar.value,
            errorText = stringResource(id = R.string.feature_not_available),
            onDismiss = { showSnackbar.value = false },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalFocus::class)
@Composable
fun WelcomeScreen(onNavigationEvent: (SignInEvent) -> Unit) {
    var brandingBottom by remember { mutableStateOf(0f) }
    var showBranding by remember { mutableStateOf(true) }
    var heightWithBranding by remember { mutableStateOf(0) }

    val currentOffsetHolder = remember { mutableStateOf(0f) }
    currentOffsetHolder.value = animate(
        if (showBranding) 0f else -brandingBottom
    )

    val heightDp = with(DensityAmbient.current) { heightWithBranding.toDp() }
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .brandingPreferredHeight(showBranding, heightDp)
                .offsetPx(y = currentOffsetHolder)
                .onPositioned {
                    if (showBranding) {
                        heightWithBranding = it.size.height
                    }
                }
        ) {
            Branding(
                modifier = Modifier.fillMaxWidth().weight(1f).onPositioned {
                    if (brandingBottom == 0f) {
                        brandingBottom = it.boundsInParent.bottom
                    }
                }
            )
            /**
             * Unwraps login fields
             */
            SignInCreateAccount(
                onNavigationEvent = onNavigationEvent,
                onFocusChange = { focused -> showBranding = !focused },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            )
        }
    }
}

private fun Modifier.brandingPreferredHeight(
    showBranding: Boolean,
    heightDp: Dp
): Modifier {
    return if (!showBranding) {
        Modifier
            .noHeightConstraints()
            .preferredHeight(heightDp)
    } else {
        Modifier
    }
}

@Composable
private fun Branding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Logo(modifier = Modifier.align(Alignment.CenterHorizontally).padding(horizontal = 76.dp))
        ProvideEmphasis(emphasis = EmphasisAmbient.current.high) {
            Text(
                text = stringResource(id = R.string.app_tagline),
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 24.dp).fillMaxWidth()
            )
        }
    }
}

/**
 * Set app logo selected on the basis of theme
 */
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

@OptIn(ExperimentalFocus::class)
@Composable
private fun SignInCreateAccount(
    onNavigationEvent: (SignInEvent) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val showSnackbar = remember { mutableStateOf(false) }
    val emailState = remember { EmailState() }
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
            Text(
                text = stringResource(id = R.string.sign_in_create_account),
                style = MaterialTheme.typography.subtitle2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 24.dp)
            )
        }
        onFocusChange(emailState.isFocused)
        SignInContent(
            onSignInSubmitted = { email, password ->
                onNavigationEvent(SignInEvent.SignIn(email, password))
            }
        )
        ErrorSnackbar(
            showError = showSnackbar.value,
            errorText = stringResource(id = R.string.feature_not_available),
            onDismiss = { showSnackbar.value = false },
            // FIXME: RadioButton
//            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalFocus::class)
@Composable
fun SignInContent(
    onSignInSubmitted: (email: String, password: String) -> Unit,
) {
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
            enabled = emailState.isValid
        ) {
            Text(
                text = stringResource(id = R.string.sign_in)
            )
        }

    }
}

fun Modifier.noHeightConstraints() = this then NoHeightConstraints

/**
 * A modifier that removes any height constraints and positions the wrapped layout at
 * the top of the available space. This should be provided in Compose b/158559319
 */
object NoHeightConstraints : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureScope.MeasureResult {
        val placeable = measurable.measure(
            constraints.copy(
                minHeight = 0,
                maxHeight = Infinity
            )
        )
        return layout(
            placeable.width,
            min(placeable.height.toDp(), constraints.maxHeight.dp).toIntPx()
        ) {
            placeable.place(0, 0)
        }
    }
}

@Composable
fun ErrorSnackbar(
    showError: Boolean,
    errorText: String,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = { }
) {
    if (!showError) {
        return
    }

    // Make Snackbar disappear after 5 seconds if the user hasn't interacted with it
    launchInComposition() {
        delay(5000L)
        onDismiss()
    }

    Box(modifier = modifier.fillMaxWidth().wrapContentHeight(Alignment.Bottom)) {
        Crossfade(current = showError) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                text = {
                    Text(
                        text = errorText,
                        style = MaterialTheme.typography.body2
                    )
                },
                action = {
                    TextButton(
                        onClick = onDismiss,
                        contentColor = contentColor()
                    ) {
                        Text(
                            text = stringResource(id = R.string.dismiss),
                            color = MaterialTheme.colors.snackbarAction
                        )
                    }
                }
            )
        }
    }
}

@Preview(name = "Welcome light theme")
@Composable
fun WelcomeScreenPreview() {
    MoodleTheme {
        WelcomeScreen {}
    }
}

@Preview(name = "Welcome dark theme")
@Composable
fun WelcomeScreenPreviewDark() {
    MoodleTheme(darkTheme = true) {
        WelcomeScreen {}
    }
}
