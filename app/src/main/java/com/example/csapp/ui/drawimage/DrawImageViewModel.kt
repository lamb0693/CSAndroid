package com.example.csapp.ui.drawimage

import android.graphics.PointF
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DrawImageViewModel : ViewModel() {

    val TAG : String = "DrawImageViewModel"

    // 현재의 선
    private val _pointList = MutableLiveData<MutableList<PointF>>()
    // 만들어 놓은 lineList
    private val _lineList = MutableLiveData<MutableList<Array<PointF>>>()

    val pointList : LiveData<MutableList<PointF>> get() = _pointList
    val lineList : LiveData<MutableList<Array<PointF>>> get() = _lineList

    // 뷰모델이 생성될대 초기값 설정해준다.
    init{
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

    fun setLineList(lineList : Array<Array<PointF>>){
        Log.i("setLineList", lineList.toString())
        val newLineList = mutableListOf<Array<PointF>>()
        for(line :Array<PointF> in lineList){
            newLineList.add(line)
        }
        _lineList.value = newLineList
        Log.i(TAG, "_lineList made=${_lineList.value}")
    }
}