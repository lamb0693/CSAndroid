package com.example.csapp.ui.drawimage

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

class DrawImageView(context : Context) : View(context) {
    val TAG = "DrawImageView"

    val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.FILL
        color = Color.BLUE
        strokeWidth = 5.0f
    }

    private lateinit var viewModel : DrawImageViewModel

    init {
        val currentActivity = context as ViewModelStoreOwner
        viewModel = ViewModelProvider(currentActivity)[DrawImageViewModel::class.java]

        val lifecycleOwner = context as LifecycleOwner
//        viewModel.currentValue.observe(lifecycleOwner, Observer {
//            //binding.numberTextview.text = it.toString()
//            Log.i(TAG, "value changed")
//        })

        viewModel.pointList.observe(lifecycleOwner, Observer {
            Log.i(TAG, "pointList changed")
            invalidate()
        })
        viewModel.lineList.observe(lifecycleOwner, Observer {
            //binding.numberTextview.text = it.toString()
            Log.i(TAG, "lineList changed")
            invalidate()
        })
    }

    private fun customDrawLine(line : Array<PointF>, canvas : Canvas? ){
        for( i in 0..line.size-2){
            canvas?.drawLine(line.get(i).x, line.get(i).y, line.get(i+1).x, line.get(i+1).y, paint)
        }
        //invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        lateinit var curLine : Array<PointF>
        if(viewModel.pointList.value != null){
            val line : MutableList<PointF> = viewModel.pointList.value as MutableList<PointF>
            curLine = line.toTypedArray()
            Log.i(TAG, "pointList=>curLine ${curLine}")
            customDrawLine(curLine, canvas)
        }

        lateinit var lineList : MutableList<Array<PointF>>
        if(viewModel.lineList.value != null){
            val lineList : MutableList<Array<PointF>> = viewModel.lineList.value as MutableList<Array<PointF>>
            for(line in lineList){
                customDrawLine(line, canvas)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        //viewModel.updateValue(6)
        if(event!=null){
            viewModel.updatePointList(PointF(event.x, event.y))

            Log.i(TAG, "onTouchEvent: ")
            when (event.actionMasked){
                MotionEvent.ACTION_DOWN -> {
                    Log.i(TAG, "Touch Down")
                }
                MotionEvent.ACTION_MOVE -> {
                    Log.i(TAG, "Touch Move")
                }
                MotionEvent.ACTION_UP   -> {
                    Log.i(TAG, "Touch up")
                    if(viewModel.pointList.value != null){
                        val pointList = viewModel.pointList.value as MutableList<PointF>
                        val pointArray : Array<PointF> = pointList.toTypedArray()
                        viewModel.registerNewLine(pointArray)
                    }

                    viewModel.clearPointList()
                }
            }
        }

        // invalidate() observer  에서 onDraw를 부르니 중복하면 안됨

        return true // true로 바꾸어야 up 할 때까지의 event가 씹히지 않는다
        //return super.onTouchEvent(event)
    }

    fun removeLastLine() : Unit{
        viewModel.deleteLastLine();
        // invalidate() observer  에서 onDraw를 부르니 중복하면 안됨
    }

    fun clearAll() : Unit{
        viewModel.clearAll()
        // invalidate() observer  에서 onDraw를 부르니 중복하면 안됨
    }

}