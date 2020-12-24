package info.moevm.moodle.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import info.moevm.moodle.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        val data = MutableLiveData<LoginSuccess>()
        api.logIn(APIVariables.MOODLE_MOBILE_APP.value, userName, passWord)
            .enqueue(object : Callback<LoginSuccess> { // асинхронный вызов.
                override fun onResponse(
                    call: Call<LoginSuccess>,
                    response: Response<LoginSuccess>
                ) {
                    val res = response.body()
                    if (response.code() == 200 && res != null) {
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

    fun getCurrentCourses(token: String): CurrentCourses? {
        var data: CurrentCourses? = null
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getCurrentCourses(token, APIVariables.MOD_ASSIGN_GET_ASSIGMENTS.value, APIVariables.MOODLE_WS_REST_FORMAT.value).execute()
                if (response.isSuccessful) {
                    Timber.d("get response " + response.body())
                    data = response.body()!!
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Timber.d("fatal error")
                }
            }
        }
        // WORK IN PROGRESS
        val time = System.currentTimeMillis()
        while (data == null) {
            if (System.currentTimeMillis() - time > 1000)
                break
        }
        Timber.d("answer is received")
        return data
    }
    fun getCourses(token: String): Courses? {
        var data: Courses? = null
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getCourses(token, APIVariables.CORE_COURSE_GET_COURSES_BY_FIELD.value, APIVariables.MOODLE_WS_REST_FORMAT.value).execute()
                if (response.isSuccessful) {
                    Timber.d("get response " + response.body())
                    data = response.body()!!
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Timber.d("fatal error")
                }
            }
        }
        // WORK IN PROGRESS
        val time = System.currentTimeMillis()
        while (data == null) {
            if (System.currentTimeMillis() - time > 1000)
                break
        }
        Timber.d("answer is received")
        return data
    }
}
