package com.example.csapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.csapp.util.CouselListDTO

class MainViewModel : ViewModel() {

    val TAG : String = "MainViewModel"

    private val _accessToken = MutableLiveData<String>()
    private val _refreshToken = MutableLiveData<String>()
    private val _displayName = MutableLiveData<String>()

    private val _counselList = MutableLiveData<MutableList<CouselListDTO>>()

    val accessToken : LiveData<String> get() = _accessToken
    val refreshToken : LiveData<String> get() = _refreshToken
    val displayName : LiveData<String> get() = _displayName
    val counselList : LiveData<MutableList<CouselListDTO>> get() = _counselList


    private val _connect_status = MutableLiveData<Int>()
    fun getConnectStatus() : LiveData<Int> {
        return _connect_status
    }

    fun setConnectStatus(state : Int) : Unit {
        _connect_status.value = state
    }


    // 뷰모델이 생성될대 초기값 설정해준다.
    init{
        _accessToken.value = ""
        _refreshToken.value = ""
        _displayName.value = "anonymous"
        _counselList.value = mutableListOf<CouselListDTO>()
        // _connect_status.value = 0 setting을 하면  초기화 과정에서 죽음
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

    fun setCounselList(counselList : List<CouselListDTO>){
        //_currentValue.value = _currentValue.value?.plus(input)
        // observer를 위해  새로운 list로 바꾸어 주어야 함  새로운 객체를 생성 대입함
        val newList = mutableListOf<CouselListDTO>()
        for(dto: CouselListDTO in counselList) newList.add(dto)
        _counselList.value = newList
    }

    fun getIdFromCounselList(index : Int) : Long? {
        return _counselList.value?.get(index)?.board_id
    }
}