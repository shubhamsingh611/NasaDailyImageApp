package com.example.nasaimagesapp.Api

import com.example.nasaimagesapp.Model.DailyImageModel
import retrofit2.Response
import retrofit2.http.GET

interface DailyImageService {
    //Passing API key to get response
    @GET("apod?api_key=KGjXKB7oThqzroew0HHS0aK0vuOhTg4pjNiGkUhV")
    suspend fun getDailyImage() : Response<DailyImageModel>
}