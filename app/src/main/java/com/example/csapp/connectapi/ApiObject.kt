package com.example.csapp.connectapi

import com.example.csapp.Cons
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObject {
    //private const val BASE_URL = "http://10.100.203.29:8080"
    //private const val BASE_URL = "http://192.168.200.182:8080"

    private fun getRetrofit():Retrofit{
        return Retrofit.Builder()
            .baseUrl(Cons.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getApiService(): RetrofitService {
        return getRetrofit().create(RetrofitService::class.java)
    }
}