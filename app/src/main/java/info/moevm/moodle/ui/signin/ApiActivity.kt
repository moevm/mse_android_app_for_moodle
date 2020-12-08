package info.moevm.moodle.ui.signin

import android.util.Log
import info.moevm.moodle.model.RandomCatFacts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://cat-fact.herokuapp.com"
const val MOODLE = "http://e.moevm.info"
//const val MOODLE = "https://10.0.2.2:1010"
val TAG = "ApiActivity"

class ApiActivity {
    public fun getCurrentData() {
        val api = Retrofit.Builder()
            .baseUrl(MOODLE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequests::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getCatFacts().awaitResponse()
                if (response.isSuccessful) {

                    val data = response.body()!!
                    Log.d(TAG, data.toString())

//                    withContext(Dispatchers.Main) {
//                        tv_textView.visibility = View.VISIBLE
//                        tv_timeStamp.visibility = View.VISIBLE
//                        progressBar.visibility = View.GONE
//                        tv_timeStamp.text = data.createdAt
//                        tv_textView.text = data.text
//
//                    }
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
    }
}
