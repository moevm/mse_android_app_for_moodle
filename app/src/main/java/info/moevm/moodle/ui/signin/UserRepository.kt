package info.moevm.moodle.ui.signin

import androidx.compose.runtime.Immutable

sealed class User {
    @Immutable
    data class LoggedInUser(val email: String) : User()
    object GuestUser : User()
    object NoUserLoggedIn : User()
}

/**
 * Repository that holds the logged in user.
 *
 * In a production app, this class would also handle the communication with the backend for
 * sign in and sign up.
 */
object UserRepository {

    private var _user: User = User.NoUserLoggedIn
    val user: User
        get() = _user

    @Suppress("unused")
    fun signIn(email: String, password: String) {
        password.length // TODO: remove it: just for suppress warnings
        _user = User.LoggedInUser(email)
    }

    // not usefull
    @Suppress("unused")
    fun signUp(email: String, password: String) {
        password.length // TODO: remove it: just for suppress warnings
        _user = User.LoggedInUser(email)
    }

    fun signInAsGuest() {
        _user = User.GuestUser
    }

    fun isKnownUserEmail(email: String): Boolean {
        // if the email contains "sign up" we consider it unknown
        return !email.contains("signup")
    }
}
