package com.example.csapp.dto

import com.google.gson.annotations.SerializedName

data class TokenStructure(
    @SerializedName("accessToken") var accessToken : String,
    @SerializedName("refreshToken") var refreshToken : String
)

