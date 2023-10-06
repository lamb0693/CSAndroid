package com.example.csapp.ui.main

import android.graphics.PointF
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val TAG : String = "MainViewModel"

    private val _accessToken = MutableLiveData<String>()
    private val _refreshToken = MutableLiveData<String>()
    private val _displayName = MutableLiveData<String>()
    val accessToken : LiveData<String> get() = _accessToken
    val refreshToken : LiveData<String> get() = _refreshToken
    val displayName : LiveData<String> get() = _displayName

    // 뷰모델이 생성될대 초기값 설정해준다.
    init{
        _accessToken.value = ""
        _refreshToken.value = ""
        _displayName.value = "anonymous"
    }

    fun setAccessToken(accessToken : String ) : Unit {
        _accessToken.value = accessToken
    }

    fun setRefreshToken(refreshToken : String) : Unit {
        _refreshToken.value = refreshToken
    }

    fun setDisplayName(displayName : String) : Unit {
        _displayName.value = displayName
    }

}