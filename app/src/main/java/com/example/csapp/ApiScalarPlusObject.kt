package com.example.csapp

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitScalarPlusObject {
    private const val BASE_URL = "http://10.100.203.29:8080"

    private fun getRetrofit(): Retrofit {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson)) // Add ScalarsConverterFactory
            .build()
    }

    fun getApiService():RetrofitService{
        return getRetrofit().create(RetrofitService::class.java)
    }
}