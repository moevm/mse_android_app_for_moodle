package info.moevm.moodle.ui.signin

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import info.moevm.moodle.R
import info.moevm.moodle.api.DataStoreUser
import info.moevm.moodle.api.MoodleApi
import info.moevm.moodle.data.courses.CourseManager
import info.moevm.moodle.ui.Screen
import timber.log.Timber

@Composable
fun PreviewScreen(
    courseManager: CourseManager,
    navigateTo: (Screen) -> Unit
) {
    PreviewScreenContent()

    fun Context.lifecycleOwner(): LifecycleOwner? {
        var curContext = this
        var maxDepth = 20
        while (maxDepth-- > 0 && curContext !is LifecycleOwner) {
            curContext = (curContext as ContextWrapper).baseContext
        }
        return if (curContext is LifecycleOwner) {
            curContext
        } else {
            null
        }
    }

    val apiclient = MoodleApi()
    val context = LocalContext.current
    val lifeSO = context.lifecycleOwner()
    val dataStore = DataStoreUser(context)
    val inSignInScreen = remember { mutableStateOf(false) }
    val inHomeScreen = remember { mutableStateOf(false) }
    lateinit var tokenState: String

    fun checkToken(token: String) {
        val answ = apiclient.checkToken(token)
        answ.observe(
            lifeSO!!
        ) {
            Timber.tag("Check_token")
                .i("checkLogIn was called with answ: ${answ.value}")
            if (answ.value?.errorcode != "invalidtoken") {
                if (!inHomeScreen.value) {
                    showMessage(context, context.getString(R.string.user_auth))
                    courseManager.setToken(token)
                    navigateTo(Screen.Home)
                    inHomeScreen.value = true
                }
            }
            // else - остаемся
        }
    }

    fun checkLogIn() {
        Timber.tag("Check_token").i("checkLogIn was called")
        dataStore.tokenFlow.asLiveData().observe(
            lifeSO!!
        ) {
            tokenState = it
            if (tokenState != "") {
                checkToken(tokenState)
            } else {
                if (!inSignInScreen.value) {
                    courseManager.setToken(it)
                    navigateTo(Screen.SignIn)
                    inSignInScreen.value = true
                }
            }
        }
    }
    checkLogIn()
}

@Composable
fun PreviewScreenContent() {
    Scaffold {
        BoxWithConstraints(
            Modifier.fillMaxSize()
        ) {
            Branding(Modifier.align(Alignment.Center))
        }
    }
}
