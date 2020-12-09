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
val TAG = "ApiActivity"
// retrofit
class ApiActivity {
    // function to test work of retrofit
    public fun getCurrentData(): RandomCatFacts? {
        var data: RandomCatFacts? = null
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequests::class.java)

        Log.d(TAG, "before enter the global scope")
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

    public fun checkLogIn(serviceName: String, userName: String, passWord: String): LoginSuccess? {
        var data: LoginSuccess? = null
        val api = Retrofit.Builder()
            .baseUrl(MOODLE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequests::class.java)

        Log.d(TAG, "before enter the global scope") // global scope - ассинхрон, корутина
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "enter the global scope")
                val response = api.logIn(serviceName, userName, passWord).execute()
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
        return data
    }
}
