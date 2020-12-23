package info.moevm.moodle.api

import android.service.controls.ControlsProviderService.TAG
import android.util.Log
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
                        System.out.println(data.value)
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
        val api = Retrofit.Builder()
            .baseUrl(APIVariables.MOODLE_URL.value)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequests::class.java)
        Log.d(TAG, "before enter the global scope") // global scope - ассинхрон, корутина
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "enter the global scope")
                val response = api.getCurrentCourses(token, APIVariables.MOD_ASSIGN_GET_ASSIGMENTS.value, APIVariables.MOODLE_WS_REST_FORMAT.value).execute()
                if (response.isSuccessful) {
                    Log.i(TAG, "get response " + response.body())

                    data = response.body()!!
                    // Log.d(TAG, data.toString())
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "smth went wrong")
                }
            }
        }
        // DANGEROUSE
        while (data == null) {
            Log.d(TAG, "still null")
        }
        Log.d(TAG, "answer is received")
        /*api.getCurrentCourses(token, APIVariables.MOD_ASSIGN_GET_ASSIGMENTS.value, APIVariables.MOODLE_WS_REST_FORMAT.value)
            .enqueue(object : Callback<CurrentCourses> { // асинхронный вызов.
                override fun onResponse(
                    call: Call<CurrentCourses>,
                    response: Response<CurrentCourses>
                ) {
                    val res = response.body()
                    if (response.code() == 200 && res != null) {
                        data.value = res
                        //System.out.println(data.value)
                    } else {
                        data.value = null
                    }
                }

                override fun onFailure(call: Call<CurrentCourses>, t: Throwable) {
                    data.value = null
                }
            })*/

        return data
    }
    fun getCourses(token: String): LiveData<Courses> {
        val data = MutableLiveData<Courses>()
        api.getCourses(token, APIVariables.CORE_COURSE_GET_COURSES_BY_FIELD.value, APIVariables.MOODLE_WS_REST_FORMAT.value)
            .enqueue(object : Callback<Courses> { // асинхронный вызов.
                override fun onResponse(
                    call: Call<Courses>,
                    response: Response<Courses>
                ) {
                    val res = response.body()
                    if (response.code() == 200 && res != null) {
                        data.value = res
                        System.out.println(data.value)
                    } else {
                        data.value = null
                    }
                }

                override fun onFailure(call: Call<Courses>, t: Throwable) {
                    data.value = null
                }
            })
        return data
    }
}
