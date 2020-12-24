package info.moevm.moodle.ui.entersetup.checksetup

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focusObserver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import info.moevm.moodle.R
import info.moevm.moodle.ui.signin.TextFieldState

@OptIn(ExperimentalFocus::class)
@Composable
fun FiledEnter(
    fieldState: TextFieldState = remember { TokenState() },
    imeAction: ImeAction = ImeAction.Next,
    labelVal: String,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = fieldState.text,
        onValueChange = {
            fieldState.text = it
        },
        label = {
            Providers(AmbientContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = labelVal,
                    style = MaterialTheme.typography.body2
                )
            }
        },
        modifier = Modifier.fillMaxWidth().focusObserver { focusState ->
            val focused = focusState == FocusState.Active
            fieldState.onFocusChange(focused)
            if (!focused) {
                fieldState.enableShowErrors()
            }
        },
        textStyle = MaterialTheme.typography.body2,
        isErrorValue = fieldState.showErrors(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        onImeActionPerformed = { action, softKeyboardController ->
            if (action == ImeAction.Done) {
                softKeyboardController?.hideSoftwareKeyboard()
            }
            onImeAction()
        }
    )

    fieldState.getError()?.let { error -> TextFieldError(textError = error) }
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
