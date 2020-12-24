package info.moevm.moodle.ui.entersetup.checksetup

import info.moevm.moodle.ui.signin.TextFieldState
import java.util.regex.Pattern

private const val TOKEN_VALIDATION_REGEX = """[A-Za-z0-9]{32}"""

class TokenState :
    TextFieldState(validator = ::isTokenValid, errorFor = ::tokenValidationError)

/**
 * Returns an error to be displayed or null if no error was found
 */
@Suppress("unused")
private fun tokenValidationError(token: String): String {
    token.length // TODO: remove it: just for suppress warnings
    return "Invalid token"
}

private fun isTokenValid(token: String): Boolean {
    return Pattern.matches(TOKEN_VALIDATION_REGEX, token)
}
