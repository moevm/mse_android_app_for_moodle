package info.moevm.moodle.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import info.moevm.moodle.data.courses.*
import info.moevm.moodle.model.*
import info.moevm.moodle.model.APIVariables
import info.moevm.moodle.model.LoginSuccess
import info.moevm.moodle.model.MoodleUser
import info.moevm.moodle.model.WrongToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.*
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
        Timber.tag("Check_login").i("checkLogIn with API was called")
        val data = MutableLiveData<LoginSuccess>()
        api.logIn(APIVariables.MOODLE_MOBILE_APP.value, userName, passWord)
            .enqueue(object : Callback<LoginSuccess> { // асинхронный вызов.
                override fun onResponse(
                    call: Call<LoginSuccess>,
                    response: Response<LoginSuccess>
                ) {
                    val res = response.body()
                    if (response.code() == 200 && res != null) {
                        Timber.tag("Check_login").i("$res")
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
        while (data == null && System.currentTimeMillis() - time < 1000) {}
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
        while (data == null && System.currentTimeMillis() - time < 1000) {}
        Timber.d("answer is received")
        return data
    }

    fun getMoodleUserInfo(token: String, userLogin: String): LiveData<List<MoodleUser>> {
        Timber.tag("GET_user_info").i("getMoodleUserInfo was called with token: |$token|, userLogin: |$userLogin|")
        val data = MutableLiveData<List<MoodleUser>>()
        api.getUserInformation(token, APIVariables.CORE_USER_GET_USERS_BY_FIELD.value, APIVariables.MOODLE_WS_REST_FORMAT.value, "username", userLogin)
            .enqueue(object : Callback<List<MoodleUser>> {
                override fun onResponse(
                    call: Call<List<MoodleUser>>,
                    response: Response<List<MoodleUser>>
                ) {
                    val res = response.body()
                    if (response.code() == 200 && res != null) {
                        data.value = res
                        Timber.tag("GET_user_info").i("GOT response from server with: ${data.value?.get(0)}")
                    } else {
                        data.value = null
                        Timber.tag("GET_user_info").i("No correct user data response from server")
                    }
                }

                override fun onFailure(call: Call<List<MoodleUser>>, t: Throwable) {
                    Timber.tag("GET_user_info").i("onFailure GET user info was called because ${t.cause}, call: $call")
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

    fun getCourseContent(token: String, courseId: String): List<CourseMoodleContentData>? {
        var data: List<CourseMoodleContentData>? = null
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getCourseContent(token, APIVariables.CORE_COURSE_GET_CONTENTS.value, courseId, APIVariables.MOODLE_WS_REST_FORMAT.value).execute()
                if (response.isSuccessful) {
                    Timber.d("get response " + response.body())
                    data = response.body()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val z = e.message // TODO убрать
                    Timber.d("fatal error")
                }
            }
        }
        // WORK IN PROGRESS
        val time = System.currentTimeMillis()
        while (data == null && System.currentTimeMillis() - time < 1000) {}
        Timber.d("answer is received")
        return data
    }

    fun getLessonPages(token: String, lessonId: String): LessonPages? {
        var data: LessonPages? = null
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getLessonPages(token, APIVariables.MOD_LESSON_GET_PAGES.value, lessonId, APIVariables.MOODLE_WS_REST_FORMAT.value).execute()
                if (response.isSuccessful) {
                    Timber.d("get response " + response.body())
                    data = response.body()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val z = e.message // TODO убрать
                    Timber.d("fatal error")
                }
            }
        }
        // WORK IN PROGRESS
        val time = System.currentTimeMillis()
        while (data == null && System.currentTimeMillis() - time < 1000) {}
        Timber.d("answer is received")
        return data
    }

    fun getQuizAttempts(token: String, quizid: String, status: String = "all"): QuizAttempts? {
        var data: QuizAttempts? = null
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getQuizAttempts(token, APIVariables.MOD_QUIZ_GET_USER_ATTEMPTS.value, quizid, status, APIVariables.MOODLE_WS_REST_FORMAT.value).execute()
                if (response.isSuccessful) {
                    Timber.d("get response " + response.body())
                    data = response.body()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val z = e.message // TODO убрать
                    Timber.d("fatal error")
                }
            }
        }
        // WORK IN PROGRESS
        val time = System.currentTimeMillis()
        while (data == null && System.currentTimeMillis() - time < 1000) {}
        Timber.d("answer is received")
        return data
    }

    fun getQuizInProgress(token: String, attemptid: String, page: String): QuizInProgress? {
        var data: QuizInProgress? = null
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getQuizInProgress(token, APIVariables.MOD_QUIZ_GET_ATTEMPT_DATA.value, attemptid, page, APIVariables.MOODLE_WS_REST_FORMAT.value).execute()
                if (response.isSuccessful) {
                    Timber.d("get response " + response.body())
                    data = response.body()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val z = e.message // TODO убрать
                    Timber.d("fatal error")
                }
            }
        }
        // WORK IN PROGRESS
        val time = System.currentTimeMillis()
        while (data == null && System.currentTimeMillis() - time < 1000) {}
        Timber.d("answer is received")
        return data
    }

    fun getQuizFinished(token: String, attemptid: String, page: String): QuizFinished? {
        var data: QuizFinished? = null
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getQuizFinished(token, APIVariables.MOD_QUIZ_GET_ATTEMPT_REVIEW.value, attemptid, page, APIVariables.MOODLE_WS_REST_FORMAT.value).execute()
                if (response.isSuccessful) {
                    Timber.d("get response " + response.body())
                    data = response.body()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val z = e.message // TODO убрать
                    Timber.d("fatal error")
                }
            }
        }
        // WORK IN PROGRESS
        val time = System.currentTimeMillis()
        while (data == null && System.currentTimeMillis() - time < 1000) {}
        Timber.d("answer is received")
        return data
    }

    fun getQuizFinished(token: String, attemptid: String): QuizFinished? {
        var data: QuizFinished? = null
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getQuizFinished(token, APIVariables.MOD_QUIZ_GET_ATTEMPT_REVIEW.value, attemptid, APIVariables.MOODLE_WS_REST_FORMAT.value).execute()
                if (response.isSuccessful) {
                    Timber.d("get response " + response.body())
                    data = response.body()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val z = e.message // TODO убрать
                    Timber.d("fatal error")
                }
            }
        }
        // WORK IN PROGRESS
        val time = System.currentTimeMillis()
        while (data == null && System.currentTimeMillis() - time < 1000) {}
        Timber.d("answer is received")
        return data
    }

    fun startNewAttempt(token: String, quizid: String): QuizAttempt? {
        var data: QuizAttempt? = null
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.startNewAttempt(token, APIVariables.MOD_QUIZ_START_ATTEMPT.value, quizid, APIVariables.MOODLE_WS_REST_FORMAT.value).execute()
                if (response.isSuccessful) {
                    Timber.d("get response " + response.body())
                    data = response.body()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val z = e.message // TODO убрать
                    Timber.d("fatal error")
                }
            }
        }

        // WORK IN PROGRESS
        val time = System.currentTimeMillis()
        while (data == null && System.currentTimeMillis() - time < 1000) {}
        Timber.d("answer is received")
        return data
    }
}
