package com.example.mystoryapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapp.api.Config
import com.example.mystoryapp.response.ListStoryItem
import com.example.mystoryapp.response.Story
import com.example.mystoryapp.response.UploadStory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryModel :ViewModel() {

    private val storyList = MutableLiveData<List<ListStoryItem>>()
    val _storyList : LiveData<List<ListStoryItem>> = storyList
    private val storyResponse = MutableLiveData<UploadStory>()
    val _storyResponse : LiveData<UploadStory> = storyResponse
    private val _loading = MutableLiveData<Boolean>()
    val loading :LiveData<Boolean> = _loading


    fun getStory(token: String){
        _loading.postValue(true)
        Config.getApiService().getStoryList(token, 20)
            .enqueue(object : Callback<Story>{
                override fun onResponse(call: Call<Story>, response: Response<Story>) {
                    _loading.postValue(false)
                    if (response.isSuccessful){
                        storyList.postValue(response.body()?.listStory)
                    }

                }

                override fun onFailure(call: Call<Story>, t: Throwable) {
                    Log.e(StoryModel::class.java.simpleName, "onFailure Call: ${t.message}")
                }
            })
    }

    fun uploadStory(token: String, image: MultipartBody.Part, desc: RequestBody){
        _loading.postValue(true)
        Config.getApiService().uploadImage(token, image, desc)
            .enqueue(object : Callback<UploadStory>{
                override fun onResponse(call: Call<UploadStory>, response: Response<UploadStory>) {
                    _loading.postValue(false)
                    if (response.isSuccessful){
                        storyResponse.postValue(response.body())
                        Log.d("yuk bisa","${response.body()}")
                    }
                }

                override fun onFailure(call: Call<UploadStory>, t: Throwable) {
                    Log.e(StoryModel::class.java.simpleName, "onFailure Call ${t.message}")
                }

            })

    }
}