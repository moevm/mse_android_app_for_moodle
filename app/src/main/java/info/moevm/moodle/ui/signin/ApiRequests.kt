package info.moevm.moodle.ui.signin

import info.moevm.moodle.model.LoginSuccess
import info.moevm.moodle.model.RandomCatFacts
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
/*TODO
*  push
* format???
* on butt too
* remade for post
* kill while if too long*/
//service=moodle_mobile_app&username=test&password=test@22G
interface ApiRequests {
    @GET("/facts/random")
    fun getCatFacts(): Call<RandomCatFacts>

    @GET("/login/token.php")
    fun logIn(@Query("service") serviceName:String, @Query("username") userName:String , @Query("password") passWord:String ):Call<LoginSuccess>

}
