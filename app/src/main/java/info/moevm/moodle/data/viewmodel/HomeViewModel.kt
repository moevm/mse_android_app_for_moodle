package info.moevm.moodle.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import info.moevm.moodle.model.HomeRepository
import info.moevm.moodle.model.PostModel

// class HomeViewModel(application: Application): AndroidViewModel(application){
class HomeViewModel() {
    private var homeRepository: HomeRepository? = null
    var postModelListLiveData: LiveData<List<PostModel>>? = null
    var createPostLiveData: LiveData<PostModel>? = null
    var deletePostLiveData: LiveData<Boolean>? = null

    init {
        homeRepository = HomeRepository()
        postModelListLiveData = MutableLiveData()
        createPostLiveData = MutableLiveData()
        deletePostLiveData = MutableLiveData()
    }

    fun fetchAllPosts() {
        postModelListLiveData = homeRepository?.fetchAllPosts()
    }

    fun createPost(postModel: PostModel) {
        createPostLiveData = homeRepository?.createPost(postModel)
    }

//    fun deletePost(id:Int){
//        deletePostLiveData = homeRepository?.deletePost(id)
//    }
}
