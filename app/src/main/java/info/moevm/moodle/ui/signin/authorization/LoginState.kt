package info.moevm.moodle.ui.signin.authorization

import info.moevm.moodle.ui.signin.TextFieldState
import java.util.regex.Pattern

private const val LOGIN_VALIDATION_REGEX = "([A-Za-z0-9\\.-_@]{2,35})$"

class LoginState :
    TextFieldState(validator = ::isLoginValid, errorFor = ::loginValidationError)

/**
 * Returns an error to be displayed or null if no error was found
 */
@Suppress("unused")
private fun loginValidationError(email: String): String {
    return "Неверный формат логина"
}

private fun isLoginValid(login: String): Boolean {
    return Pattern.matches(LOGIN_VALIDATION_REGEX, login)
}
