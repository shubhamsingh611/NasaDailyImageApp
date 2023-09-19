package com.example.nasaimagesapp.Api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitHelper {
    //Retrofit Implementation for API Calling

    private const val BASE_URL = "https://api.nasa.gov/planetary/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}