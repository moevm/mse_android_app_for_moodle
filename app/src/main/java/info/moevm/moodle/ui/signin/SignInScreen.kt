package info.moevm.moodle.ui.signin

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.animation.animate
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focusRequester
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import info.moevm.moodle.R
import info.moevm.moodle.api.DataStoreMoodleUser
import info.moevm.moodle.api.DataStoreUser
import info.moevm.moodle.api.MoodleApi
import info.moevm.moodle.model.LoginSuccess
import info.moevm.moodle.model.MoodleUser
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.signin.authorization.Login
import info.moevm.moodle.ui.signin.authorization.LoginState
import info.moevm.moodle.ui.signin.authorization.Password
import info.moevm.moodle.ui.signin.authorization.PasswordState
import info.moevm.moodle.ui.theme.MOEVMMoodleTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class SignInEvent {
    data class SignIn(val email: String, val password: String) : SignInEvent()
}

fun showMessage(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, message, duration).show()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SignInScreen(
    navigateTo: (Screen) -> Unit
) {
    var brandingBottom by remember { mutableStateOf(0f) }
    val showBranding by remember { mutableStateOf(true) }
    var heightWithBranding by remember { mutableStateOf(0) }

    val currentOffsetHolder = remember { mutableStateOf(0f) }
    currentOffsetHolder.value = animate(
        if (showBranding) 0f else -brandingBottom
    )
    val heightDp = with(AmbientDensity.current) { heightWithBranding.toDp() }

    Scaffold(
        topBar = {
            SignInSignUpTopAppBar(
                topAppBarText = stringResource(id = R.string.sign_in),
                onSetupTouch = navigateTo
            )
        },
        bodyContent = {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .brandingPreferredHeight(showBranding, heightDp)
                    .offset({ mutableStateOf(0f).value }, { currentOffsetHolder.value })
                    .onSizeChanged {
                        if (showBranding) {
                            heightWithBranding = it.height
                        }
                    }
            ) {

                SignInSignUpScreen(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SignInContent(
                            // TODO: check correction of login key
//                            onSignInSubmitted = { email, password ->
//                                SignInEvent.SignIn(email, password)
//                            }
                            onSignInSubmitted = navigateTo
                        )
                    }
                }
                Branding(
                    modifier = Modifier.fillMaxWidth().weight(1f).onGloballyPositioned {
                        if (brandingBottom == 0f) {
                            brandingBottom = it.boundsInParent.bottom
                        }
                    }
                )
            }
        }
    )
}

/**
 * Just "Sign In" text on the top bar of the app
 */
@Composable
fun SignInSignUpTopAppBar(
    topAppBarText: String,
    onSetupTouch: (Screen) -> Unit
) {
    val image = vectorResource(id = R.drawable.settings)
    TopAppBar(
        title = {
            Text(
                text = topAppBarText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                    .padding(end = 12.dp)
            )
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    )
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.End
    ) {
        IconButton(
            onClick = {
                onSetupTouch(Screen.EnterSetup)
            }
        ) {
            Row {
                Image(imageVector = image)
            }
        }
    }
}

@OptIn(ExperimentalFocus::class)
@Composable
fun SignInContent(
    onSignInSubmitted: (Screen) -> Unit
) {

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
    val context = AmbientContext.current
    val lifeSO = context.lifecycleOwner()
    val dataStore = DataStoreUser(context)
    val moodleProfileDataStore = DataStoreMoodleUser(context)
    lateinit var tokenState: String
    lateinit var loginState: String

    fun checkToken(token: String) {
        val answ = apiclient.checkToken(token)
        answ.observe(
            lifeSO!!,
            {
                Timber.tag("Check_token").i("checkLogIn was called with answ: ${answ.value}")
                if (answ.value?.errorcode != "invalidtoken") {
                    showMessage(context, "already login")
                    onSignInSubmitted(Screen.Home)
                }
                // else - остаемся
            }
        )
    }

    fun checkLogIn() {
        Timber.tag("Check_token").i("checkLogIn was called")
        dataStore.tokenFlow.asLiveData().observe(
            lifeSO!!,
            {
                tokenState = it
                if (tokenState != "") {
                    checkToken(tokenState)
                }
            }
        )
    }

    fun setLoginName(token: String, userName: String) {
        val answ = apiclient.getMoodleUserInfo(token, userName)
        answ.observe(
            lifeSO!!,
            {
                Timber.tag("GET_user_info").i("GET value from Moodle: value: ${answ.value}")
                val moodleProfile = answ.value?.get(0)
                    ?: MoodleUser(
                        0, context.resources.getString(R.string.user_name_placeholder),
                        context.resources.getString(R.string.user_img_url_placeholder),
                        context.resources.getString(R.string.user_city_placeholder),
                        context.resources.getString(R.string.user_country_placeholder)
                    )
                GlobalScope.launch(Dispatchers.Main) {
                    moodleProfileDataStore.addMoodleUser(
                        moodleProfile.id,
                        moodleProfile.fullname,
                        moodleProfile.profileimageurl,
                        moodleProfile.city,
                        moodleProfile.country
                    )
                }
            }
        )
    }

    fun checkLoginName() {
        Timber.tag("GET_user_info").i("checkLoginName was called")
        dataStore.loginFlow.asLiveData().observe(
            lifeSO!!,
            { itLogin ->
                loginState = itLogin
                dataStore.tokenFlow.asLiveData().observe(
                    lifeSO,
                    {
                        tokenState = it
                        if (loginState != "" && tokenState != "") {
                            setLoginName(tokenState, loginState)
                        }
                    }
                )
            }
        )
    }

    checkLogIn()

    AmbientContext.current as Activity
    Column(modifier = Modifier.fillMaxWidth()) {
        val loginFocusRequester = remember { FocusRequester() }
        val passwordFocusRequester = remember { FocusRequester() }
        val loginState = remember { LoginState() }
        Login(
            loginState = loginState, onImeAction = { passwordFocusRequester.requestFocus() },
            modifier = Modifier.focusRequester(loginFocusRequester)
        )

        Spacer(modifier = Modifier.preferredHeight(16.dp))

        val passwordState = remember { PasswordState() }
        Password(
            label = stringResource(id = R.string.password),
            passwordState = passwordState,
            modifier = Modifier.focusRequester(passwordFocusRequester)
        )
        Spacer(modifier = Modifier.preferredHeight(16.dp))
        Button(
            onClick = {
                val userName = loginState.text
                val userPassword = passwordState.text
                val data: LiveData<LoginSuccess>?
                data = apiclient.checkLogIn(userName, userPassword)
                data.observe(
                    lifeSO!!,
                    Observer {
                        when {
                            data.value?.token != null -> {
                                tokenState = data.value?.token!!
                                GlobalScope.launch {
                                    // TODO if
                                    dataStore.addUser(userName, userPassword, tokenState)
                                }
                                onSignInSubmitted(Screen.Home)
                            }
                            data.value?.error != null -> {
                                showMessage(context, message = context.resources.getString(R.string.wrong_login))
                            }
                            else -> {
                                showMessage(context, message = context.resources.getString(R.string.network_problems))
                            }
                        }
                    }
                )
                checkLoginName()
                showMessage(context, "checking...", 5000)
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            enabled = loginState.isValid && passwordState.isValid
        ) {
            Text(
                text = stringResource(id = R.string.sign_in)
            )
        }
    }
}

@Composable
fun SignInSignUpScreen(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ScrollableColumn(modifier = modifier) {
        Spacer(modifier = Modifier.preferredHeight(44.dp))
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            content()
        }
    }
}

@Preview(name = "Sign in light theme")
@Composable
fun SignInPreview() {
    MOEVMMoodleTheme {
        SignInScreen {}
    }
}

@Preview(name = "Sign in dark theme")
@Composable
fun SignInPreviewDark() {
    MOEVMMoodleTheme(darkTheme = true) {
        SignInScreen {}
    }
}
