package info.moevm.moodle.ui.entersetup.checksetup

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import info.moevm.moodle.ui.signin.TextFieldState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FiledEnter(
    fieldState: TextFieldState = remember { TokenState() },
    imeAction: ImeAction = ImeAction.Next,
    labelVal: String,
    onImeAction: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = fieldState.text,
        onValueChange = {
            fieldState.text = it
        },
        label = {
                Text(
                    text = labelVal,
                    style = MaterialTheme.typography.body2
                )
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                val focused = focusState.isFocused
                fieldState.onFocusChange(focused)
                if (!focused) {
                    fieldState.enableShowErrors()
                }
            },
        textStyle = MaterialTheme.typography.body2,
        isError = fieldState.showErrors(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                onImeAction()
            },
            onNext = { onImeAction() }
        )
    )

    fieldState.getError()?.let { error -> TextFieldError(textError = error) }
}

/**
 * To be removed when [TextField]s support error
 */
@Composable
fun TextFieldError(textError: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = textError,
            modifier = Modifier.fillMaxWidth(),
            style = LocalTextStyle.current.copy(color = MaterialTheme.colors.error)
        )
    }
}
