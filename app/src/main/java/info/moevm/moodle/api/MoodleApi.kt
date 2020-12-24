package info.moevm.moodle.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import info.moevm.moodle.model.APIVariables
import info.moevm.moodle.model.LoginSuccess
import info.moevm.moodle.model.MoodleUser
import info.moevm.moodle.model.WrongToken
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MoodleApi {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
    private val api = Retrofit.Builder()
        .baseUrl(APIVariables.MOODLE_URL.value)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(ApiRequests::class.java)

    fun checkLogIn(userName: String, passWord: String): LiveData<LoginSuccess> {
        Timber.tag("Check_login").e("checkLogIn with API was called")
        val data = MutableLiveData<LoginSuccess>()
        api.logIn(APIVariables.MOODLE_MOBILE_APP.value, userName, passWord)
            .enqueue(object : Callback<LoginSuccess> { // асинхронный вызов.
                override fun onResponse(
                    call: Call<LoginSuccess>,
                    response: Response<LoginSuccess>
                ) {
                    val res = response.body()
                    if (response.code() == 200 && res != null) {
                        Timber.tag("Check_login").e("$res")
                        data.value = res
                    } else {
                        data.value = null
                    }
                }

                override fun onFailure(call: Call<LoginSuccess>, t: Throwable) {
                    data.value = null
                }
            })
        return data
    }

    fun getMoodleUserInfo(userName: String): LiveData<MoodleUser> {
        val data = MutableLiveData<MoodleUser>()
        api.getUserInformation(userName, APIVariables.CORE_USER_GET_USERS_BY_FIELD.value, APIVariables.MOODLE_WS_REST_FORMAT.value)
            .enqueue(object : Callback<MoodleUser> {
                override fun onResponse(
                    call: Call<MoodleUser>,
                    response: Response<MoodleUser>
                ) {
                    val res = response.body()
                    if (response.code() == 200 && res != null) {
                        data.value = res
                        Timber.tag("GET_user_info").e("${data.value}")
                    } else {
                        data.value = null
                    }
                }

                override fun onFailure(call: Call<MoodleUser>, t: Throwable) {
                    data.value = null
                }
            })
        return data
    }

    fun checkToken(token: String): LiveData<WrongToken> {
        val data = MutableLiveData<WrongToken>()
        api.checkTokenLife(token, APIVariables.MOD_ASSIGN_GET_ASSIGMENTS.value, APIVariables.MOODLE_WS_REST_FORMAT.value)
            .enqueue(object : Callback<WrongToken> { // асинхронный вызов.
                override fun onResponse(
                    call: Call<WrongToken>,
                    response: Response<WrongToken>
                ) {
                    val res = response.body()
                    if (response.code() == 200 && res != null) {
                        data.value = res
                    } else {
                        data.value = null
                    }
                }

                override fun onFailure(call: Call<WrongToken>, t: Throwable) {
                    data.value = null
                }
            })
        return data
    }
}
