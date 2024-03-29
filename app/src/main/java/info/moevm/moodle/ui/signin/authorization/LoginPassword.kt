package info.moevm.moodle.ui.signin.authorization

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focusObserver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import info.moevm.moodle.R
import info.moevm.moodle.ui.signin.TextFieldState

@OptIn(ExperimentalFocus::class)
@Composable
fun Login(
    modifier: Modifier = Modifier,
    loginState: TextFieldState = remember { LoginState() },
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = loginState.text,
        onValueChange = {
            loginState.text = it
        },
        modifier = modifier.fillMaxWidth().focusObserver { focusState ->
            val focused = focusState == FocusState.Active
            loginState.onFocusChange(focused)
            if (!focused) {
                loginState.enableShowErrors()
            }
        },
        label = {
            Providers(AmbientContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = stringResource(id = R.string.username),
                    style = MaterialTheme.typography.body2
                )
            }
        },
        textStyle = MaterialTheme.typography.body2,
        isErrorValue = loginState.showErrors(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        onImeActionPerformed = { action, softKeyboardController ->
            if (action == ImeAction.Done) {
                softKeyboardController?.hideSoftwareKeyboard()
            }
            onImeAction()
        }
    )

    loginState.getError()?.let { error -> TextFieldError(textError = error) }
}

@OptIn(ExperimentalFocus::class)
@Composable
fun Password(
    label: String,
    passwordState: TextFieldState,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    val showPassword = remember { mutableStateOf(false) }
    OutlinedTextField(
        value = passwordState.text,
        onValueChange = {
            passwordState.text = it
            passwordState.enableShowErrors()
        },
        modifier = modifier.fillMaxWidth().focusObserver { focusState ->
            val focused = focusState == FocusState.Active
            passwordState.onFocusChange(focused)
            if (!focused) {
                passwordState.enableShowErrors()
            }
        },
        textStyle = MaterialTheme.typography.body2,
        label = {
            Providers(AmbientContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.body2
                )
            }
        },
        trailingIcon = {
            if (showPassword.value) {
                IconButton(onClick = { showPassword.value = false }) {
                    Icon(imageVector = Icons.Filled.Visibility)
                }
            } else {
                IconButton(onClick = { showPassword.value = true }) {
                    Icon(imageVector = Icons.Filled.VisibilityOff)
                }
            }
        },
        visualTransformation = if (showPassword.value) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        isErrorValue = passwordState.showErrors(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        onImeActionPerformed = { action, softKeyboardController ->
            if (action == ImeAction.Done) {
                softKeyboardController?.hideSoftwareKeyboard()
            }
            onImeAction()
        }
    )

    passwordState.getError()?.let { error -> TextFieldError(textError = error) }
}

/**
 * To be removed when [TextField]s support error
 */
@Composable
fun TextFieldError(textError: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.preferredWidth(16.dp))
        Text(
            text = textError,
            modifier = Modifier.fillMaxWidth(),
            style = AmbientTextStyle.current.copy(color = MaterialTheme.colors.error)
        )
    }
}
