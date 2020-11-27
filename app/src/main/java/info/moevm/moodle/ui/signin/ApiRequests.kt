package info.moevm.moodle.ui.signin

import info.moevm.moodle.model.RandomCatFacts
import retrofit2.Call
import retrofit2.http.GET

interface ApiRequests {
    @GET("/facts/random")
    fun getCatFacts(): Call<RandomCatFacts>
}
