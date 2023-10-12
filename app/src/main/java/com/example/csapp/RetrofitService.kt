package com.example.csapp

import okhttp3.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetrofitService {
    @Headers("Content-Type: application/json")
    @POST("/auth/login")
    fun login(
        @Body param : Map<String, String>
    ): retrofit2.Call<TokenStructure>

    @FormUrlEncoded
    @POST("/api/board/list")
    fun listBoard(
        @Header("Authorization") authToken: String?,
        @Field("tel") tel: String?,
        @Field("noOfDisplay") noOfDisplay: Int
    ): retrofit2.Call<List<CouselListDTO>>
}