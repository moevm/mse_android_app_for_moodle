package info.moevm.moodle.ui.signin

import android.util.Log
import info.moevm.moodle.model.LoginSuccess
import info.moevm.moodle.model.RandomCatFacts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://cat-fact.herokuapp.com"
const val MOODLE = "http://e.moevm.info"
// const val MOODLE = "https://10.0.2.2:1010"
const val TAG = "ApiActivity"
class ApiActivity {
    // function to test work of retrofit
    fun getCurrentData(): RandomCatFacts? {
        var data: RandomCatFacts? = null
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequests::class.java)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getCatFacts().execute()
                if (response.isSuccessful) {
                    data = response.body()!!
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                }
            }
        }
        while (data == null) {
        }
        return data
    }

    fun checkLogIn(serviceName: String, userName: String, passWord: String): LoginSuccess? {
        var data: LoginSuccess? = null
        val api = Retrofit.Builder()
            .baseUrl(MOODLE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequests::class.java)
        // global scope - ассинхрон, корутина
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.logIn(serviceName, userName, passWord).execute()
                if (response.isSuccessful) {
                    data = response.body()!!
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "smth went wrong")
                }
            }
        }
        // DANGEROUSE
        while (data == null) {
        }
        return data
    }
}
