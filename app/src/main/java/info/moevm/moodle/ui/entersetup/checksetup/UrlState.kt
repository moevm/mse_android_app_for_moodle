package info.moevm.moodle.ui.entersetup.checksetup

import info.moevm.moodle.ui.signin.TextFieldState
import java.util.regex.Pattern

// reference: https://stackoverflow.com/questions/3809401/what-is-a-good-regular-expression-to-match-a-url
private const val URL_VALIDATION_REGEX =
    """https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)"""

class UrlState :
    TextFieldState(validator = ::isUrlValid, errorFor = ::urlValidationError)

/**
 * Returns an error to be displayed or null if no error was found
 */
@Suppress("unused")
private fun urlValidationError(url: String): String {
    url.length // TODO: remove it: just for suppress warnings
    return "Invalid url"
}

private fun isUrlValid(url: String): Boolean {
    return Pattern.matches(URL_VALIDATION_REGEX, url)
}
