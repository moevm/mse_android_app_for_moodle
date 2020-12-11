package info.moevm.moodle.ui.signin

import info.moevm.moodle.model.LoginSuccess
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRequests {
    @GET("/login/token.php")
    fun logIn(@Query("service") serviceName: String, @Query("username") userName: String, @Query("password") passWord: String): Call<LoginSuccess>
}
