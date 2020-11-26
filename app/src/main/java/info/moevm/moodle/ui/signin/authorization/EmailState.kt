package info.moevm.moodle.ui.signin.authorization

import info.moevm.moodle.ui.signin.TextFieldState
import java.util.regex.Pattern

private const val EMAIL_VALIDATION_REGEX =
    "([A-Za-z]{2,15})\$"
// "^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})\$"

class EmailState :
    TextFieldState(validator = ::isEmailValid, errorFor = ::emailValidationError)

/**
 * Returns an error to be displayed or null if no error was found
 */
@Suppress("unused")
private fun emailValidationError(email: String): String {
    email.length // TODO: remove it: just for suppress warnings
    return "Invalid email"
}

private fun isEmailValid(email: String): Boolean {
    return Pattern.matches(EMAIL_VALIDATION_REGEX, email)
}
