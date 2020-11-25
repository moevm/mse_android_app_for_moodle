package info.moevm.moodle.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import info.moevm.moodle.network.ApiClient
import info.moevm.moodle.network.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeRepository {

    private var apiInterface: ApiInterface? = null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    fun fetchAllPosts(): LiveData<List<PostModel>> {
        val data = MutableLiveData<List<PostModel>>()

        apiInterface?.fetchAllPosts()?.enqueue(object : Callback<List<PostModel>> {

            override fun onFailure(call: Call<List<PostModel>>, t: Throwable) {
                data.value = null
            }

            override fun onResponse(
                call: Call<List<PostModel>>,
                response: Response<List<PostModel>>
            ) {

                val res = response.body()
                if (response.code() == 200 && res != null) {
                    data.value = res
                } else {
                    data.value = null
                }

            }
        })

        return data

    }

    fun createPost(postModel: PostModel):LiveData<PostModel>{
        val data = MutableLiveData<PostModel>()

        apiInterface?.createPost(postModel)?.enqueue(object : Callback<PostModel>{
            override fun onFailure(call: Call<PostModel>, t: Throwable) {
                data.value = null
            }

            override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                val res = response.body()
                if (response.code() == 201 && res!=null){
                    data.value = res
                }else{
                    data.value = null
                }
            }
        })

        return data

    }

}
