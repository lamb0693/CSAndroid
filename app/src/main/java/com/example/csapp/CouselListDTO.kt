package com.example.csapp

import com.google.gson.annotations.SerializedName

data class CouselListDTO(
    @SerializedName("board_id") var board_id : Long,
    @SerializedName("name") var name : String,
    @SerializedName("content") var content : String,
    @SerializedName("bReplied") var bReplied : Boolean,
    @SerializedName("message") var message : String,
    @SerializedName("strUpdatedAt") var strUpdatedAt : String,
)
