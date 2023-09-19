package com.example.nasaimagesapp.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nasaimagesapp.Api.DailyImageService
import com.example.nasaimagesapp.Model.DailyImageModel
import java.lang.Exception

class DailyImageRepository (
    private val dailyImageService : DailyImageService
){
    private val dailyImageLiveData = MutableLiveData<Response<DailyImageModel>>()

    val dailyImage : LiveData<Response<DailyImageModel>>
        get() = dailyImageLiveData

    suspend fun getDailyImage(){
            try {
                val result = dailyImageService.getDailyImage()
                if(result?.body() !=null){
                    dailyImageLiveData.postValue(Response.Success(result.body()))
                }
            }
            catch (e : Exception){
                dailyImageLiveData.postValue(Response.Error(e.message.toString()))
            }
    }
}