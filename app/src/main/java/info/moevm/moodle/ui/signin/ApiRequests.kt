package info.moevm.moodle.ui.signin

import info.moevm.moodle.model.LoginSuccess
import info.moevm.moodle.model.RandomCatFacts
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
/** TODO
*  wrong login
* remade for post
* kill while if too long*/
interface ApiRequests {
    @GET("/facts/random")
    fun getCatFacts(): Call<RandomCatFacts>

    @GET("/login/token.php")
    fun logIn(@Query("service") serviceName: String, @Query("username") userName: String, @Query("password") passWord: String): Call<LoginSuccess>
}
