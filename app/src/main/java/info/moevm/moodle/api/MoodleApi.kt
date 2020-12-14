package info.moevm.moodle.api

import info.moevm.moodle.model.APIVariables
import info.moevm.moodle.model.LoginSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class MoodleApi {
    private val api = Retrofit.Builder()
        .baseUrl(APIVariables.MOODLE_URL.value)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiRequests::class.java)

    fun checkLogIn(userName: String, passWord: String): LoginSuccess? {
        var data: LoginSuccess? = null
        // global scope - ассинхрон, корутина
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.logIn(APIVariables.MOODLE_MOBILE_APP.value, userName, passWord).execute()
                if (response.isSuccessful) {
                    data = response.body()!!
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Timber.e("smth went wrong %s", e.message)
                }
            }
        }
        val time = System.currentTimeMillis()
        while (data == null) {
            if (System.currentTimeMillis() - time > 2000)
                break
        }
        return data
    }
}
