package info.moevm.moodle.ui.signin.authorization

import info.moevm.moodle.ui.signin.TextFieldState
import java.util.regex.Pattern

private const val PASSWORD_VALIDATION_REGEX = "([A-Za-z0-9]{2,15})\$"

class PasswordState :
    TextFieldState(validator = ::isPasswordValid, errorFor = ::passwordValidationError)

class ConfirmPasswordState(private val passwordState: PasswordState) : TextFieldState() {
    override val isValid
        get() = passwordAndConfirmationValid(passwordState.text, text)

    override fun getError(): String? {
        return if (showErrors()) {
            passwordConfirmationError()
        } else {
            null
        }
    }
}

private fun passwordAndConfirmationValid(password: String, confirmedPassword: String): Boolean {
    return isPasswordValid(password) && password == confirmedPassword
}

private fun isPasswordValid(password: String): Boolean {
    return Pattern.matches(PASSWORD_VALIDATION_REGEX, password)
}

@Suppress("unused")
private fun passwordValidationError(password: String): String {
    password.length // TODO: remove it: just for suppress warnings
    return "Invalid password"
}

private fun passwordConfirmationError(): String {
    return "Passwords don't match"
}
