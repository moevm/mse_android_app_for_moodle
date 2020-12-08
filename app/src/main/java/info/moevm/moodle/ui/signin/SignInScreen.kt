package info.moevm.moodle.ui.signin

import android.util.Log
import androidx.compose.animation.animate
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import info.moevm.moodle.R
import info.moevm.moodle.model.LoginSuccess
import info.moevm.moodle.model.RandomCatFacts
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.signin.authorization.Email
import info.moevm.moodle.ui.signin.authorization.EmailState
import info.moevm.moodle.ui.signin.authorization.Password
import info.moevm.moodle.ui.signin.authorization.PasswordState
import info.moevm.moodle.ui.theme.MOEVMMoodleTheme
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

sealed class SignInEvent {
    data class SignIn(val email: String, val password: String) : SignInEvent()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SignInScreen(
    navigateTo: (Screen) -> Unit
) {

    // FIXME: return it - just remove warning
    // val snackbarHostState = remember { SnackbarHostState() }

    var brandingBottom by remember { mutableStateOf(0f) }
    val showBranding by remember { mutableStateOf(true) }
    var heightWithBranding by remember { mutableStateOf(0) }

    val currentOffsetHolder = remember { mutableStateOf(0f) }
    currentOffsetHolder.value = animate(
        if (showBranding) 0f else -brandingBottom
    )
    val heightDp = with(DensityAmbient.current) { heightWithBranding.toDp() }

    Scaffold(
        topBar = {
            SignInSignUpTopAppBar(
                topAppBarText = stringResource(id = R.string.sign_in),
            )
        },
        bodyContent = {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .brandingPreferredHeight(showBranding, heightDp)
                    .offsetPx(y = currentOffsetHolder)
                    .onSizeChanged {
                        if (showBranding) {
                            heightWithBranding = it.height
                        }
                    }
            ) {
                Branding(
                    modifier = Modifier.fillMaxWidth().weight(1f).onGloballyPositioned {
                        if (brandingBottom == 0f) {
                            brandingBottom = it.boundsInParent.bottom
                        }
                    }
                )
                SignInSignUpScreen(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SignInContent(
//                            onSignInSubmitted = { email, password ->
//                                SignInEvent.SignIn(email, password)
//                            }
                            onSignInSubmitted = navigateTo
                        )
                    }
                }
            }
        }
    )
}

/**
 * Just "Sign In" text on the top bar of the app
 */
@Composable
fun SignInSignUpTopAppBar(topAppBarText: String) {
    TopAppBar(
        title = {
            Text(
                text = topAppBarText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    )
}

@OptIn(ExperimentalFocus::class)
@Composable
fun SignInContent(
//    onSignInSubmitted: (email: String, password: String) -> Unit
    onSignInSubmitted: (Screen) -> Unit
) {
// retrogfit
    fun getCurrentData(): RandomCatFacts? {
        var data: RandomCatFacts? = null
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequests::class.java)

        Log.d(TAG, "before enter the global scope")
//         global scope - ассинхрон, mb be back later
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "enter the global scope")
                val response = api.getCatFacts().execute()
                if (response.isSuccessful) {
                    Log.i(TAG, "get resopnse " + response.body())

                    data = response.body()!!
                    Log.d(TAG, data.toString())
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
//                    Toast.makeText(
//                        applicationContext,
//                        "Seems like something went wrong...",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            }
        }

        while (data == null) {
            Log.d(TAG, "still null")
        }
        Log.d(TAG, "return data that is " + data.toString())
        return data
    }
    fun tmpTest(): LoginSuccess? {
        var data: LoginSuccess? = null
        val api = Retrofit.Builder()
            .baseUrl(MOODLE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequests::class.java)

        Log.d(TAG, "before enter the global scope")
//         global scope - ассинхрон, mb be back later
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "enter the global scope")
                val response = api.tmpFun().execute()
                if (response.isSuccessful) {
                    Log.i(TAG, "get resopnse " + response.body())

                    data = response.body()!!
                    Log.d(TAG, data.toString())
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
//                    Toast.makeText(
//                        applicationContext,
//                        "Seems like something went wrong...",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            }
        }

        while (data == null) {
            Log.d(TAG, "still null")
        }
        Log.d(TAG, "return data that is " + data.toString())
        return data
    }
    fun checkLogIn(serviceName:String, userName:String , passWord:String ): LoginSuccess?{
        var data: LoginSuccess? = null
        val api = Retrofit.Builder()
            .baseUrl(MOODLE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequests::class.java)

        Log.d(TAG, "1before enter the global scope")
//         global scope - ассинхрон, mb be back later
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "1enter the global scope")
                val response = api.logIn(serviceName, userName, passWord).execute()
                if (response.isSuccessful) {
                    Log.i(TAG, "1get resopnse " + response.body())

                    data = response.body()!!
                    Log.d(TAG, data.toString())
                }
                else
                    Log.d(TAG, "1bad answer"+ response.message())
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
//                    Toast.makeText(
//                        applicationContext,
//                        "Seems like something went wrong...",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            }
        }
        // DANGEROUSE
        while (data == null) {
            Log.d(TAG, "1still null")
        }
        Log.d(TAG, "1return data that is " + data.toString())
        return data
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        val focusRequester = remember { FocusRequester() }
        val emailState = remember { EmailState() }
        Email(emailState, onImeAction = { focusRequester.requestFocus() })

        Spacer(modifier = Modifier.preferredHeight(16.dp))

        val passwordState = remember { PasswordState() }
        Password(
            label = stringResource(id = R.string.password),
            passwordState = passwordState,
            modifier = Modifier.focusRequester(focusRequester),
            onImeAction = {
//                onSignInSubmitted(emailState.text, passwordState.text)
                onSignInSubmitted(Screen.Home)
            }
        )
        Spacer(modifier = Modifier.preferredHeight(16.dp))
        Button(
            onClick = {
//                val user_name = emailState.text
//                val user_password = passwordState.text
                // only for test
                val user_name = "---"
                val user_password = "----"
                Log.d(TAG, "login = " + user_name + "\n password = "+ user_password)
//                val data = getCurrentData()
                val data = tmpTest()
//                val data1 = checkLogIn("moodle_mobile_app", user_name, user_password)
                Log.d(TAG, "in auth " + data.toString())
//                Log.d(TAG, "in auth " + data1.toString())
//                onSignInSubmitted(emailState.text, passwordState.text)


                onSignInSubmitted(Screen.Home)
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            enabled = emailState.isValid && passwordState.isValid
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
