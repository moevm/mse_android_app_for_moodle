package info.moevm.moodle.api

import info.moevm.moodle.model.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRequests {
    @GET("/login/token.php")
    fun logIn(@Query("service") serviceName: String, @Query("username") userName: String, @Query("password") passWord: String): Call<LoginSuccess>
    @GET("/webservice/rest/server.php")
    fun getCourses(@Query("wstoken") wsToken: String, @Query("wsfunction") wsFunction: String, @Query("moodlewsrestformat") moodlewsRestFormat: String): Call<Courses>
    @GET("/webservice/rest/server.php")
    fun getCurrentCourses(@Query("wstoken") wsToken: String, @Query("wsfunction") wsFunction: String, @Query("moodlewsrestformat") moodlewsRestFormat: String): Call<CurrentCourses>
}
