package com.example.csapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitObject {
    //private const val BASE_URL = "http://10.100.203.29:8080"
    private const val BASE_URL = "http://192.168.200.182:8080"

    private fun getRetrofit():Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getApiService():RetrofitService{
        return getRetrofit().create(RetrofitService::class.java)
    }
}