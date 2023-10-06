package com.example.csapp.ui.drawimage

import android.graphics.PointF
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DrawImageViewModel : ViewModel() {

    val TAG : String = "DrawImageViewModel"

    // 내부에서 설정하는 자료형은 Mutable로 변경가능하도록 설정한다.
    //private val _currentValue = MutableLiveData<Int>()
    // current Line
    private val _pointList = MutableLiveData<MutableList<PointF>>()
    // 만들어 놓은 lineList
    private val _lineList = MutableLiveData<MutableList<Array<PointF>>>()
    // MutableLiveData - 수정 가능
    // LiveData - 값 수정 불가능

    //val currentValue: LiveData<Int> get() = _currentValue
    val pointList : LiveData<MutableList<PointF>> get() = _pointList
    val lineList : LiveData<MutableList<Array<PointF>>> get() = _lineList

    // 뷰모델이 생성될대 초기값 설정해준다.
    init{
        //LiveData로 맵핑이 되어있을때 값을 수정하려면 value를 이용한다.
        //LivaData로는 값 수정이 불가능 하지만 MutableLiveData로 초기화 했기 때문에 수정이 가능하다.
        //_currentValue.value = 0
        _pointList.value = mutableListOf<PointF>()
        _lineList.value = mutableListOf<Array<PointF>>()
    }

    // 현재 선에  새로운 point를 추가한다
    fun updatePointList(newPointF: PointF ){
        //_currentValue.value = _currentValue.value?.plus(input)
        // observer를 위해  새로운 list로 바꾸어 주어야 함  새로운 객체를 생성 대입함
        val updatedList = _pointList.value?: mutableListOf<PointF>()
        updatedList.add(newPointF)
        _pointList.value = updatedList
        Log.i(TAG, "_pointList updated=${_pointList.value}")
    }

    fun registerNewLine(line : Array<PointF>){
        val updatedLineList = _lineList.value?: mutableListOf<Array<PointF>>()
        updatedLineList.add(line)
        _lineList.value = updatedLineList
        Log.i(TAG, "_lineList updated=${_pointList.value}")
    }

    fun deleteLastLine(){
        val updatedLineList = _lineList.value?: mutableListOf<Array<PointF>>()
        if(updatedLineList.size!! > 0 ){
            updatedLineList.removeAt(updatedLineList.size-1)
        }
        _lineList.value = updatedLineList
        Log.i(TAG, "_lineList updated=${_pointList.value}")
    }

    fun clearAll(){
        _lineList.value = mutableListOf<Array<PointF>>()
        Log.i(TAG, "_lineList cleared=${_pointList.value}")
    }

    fun clearPointList(){
        _pointList.value = mutableListOf<PointF>()
        Log.i(TAG, "_pointList cleared=${_pointList.value}")
    }
}