package com.example.csapp.ui.drawimage

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.csapp.PointSerializable
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.ObjectInputStream
import java.io.OutputStreamWriter


class DrawImageView(context : Context) : View(context) {
    val TAG = "DrawImageView"
    lateinit var contextParent : Context

    val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.FILL
        color = Color.BLUE
        strokeWidth = 5.0f
    }

    private lateinit var viewModel : DrawImageViewModel

    init {
        this.contextParent = context as Context
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

    fun saveCurrentImage() : String {
        lateinit var strFileName : String
        Toast.makeText(contextParent, "saving", Toast.LENGTH_SHORT)

        try {
            val strTime : Long = System.currentTimeMillis()
            strFileName = "boardImage" + strTime + ".csr"

            //viewModel의 lineList를  ArrayList<ArrayList<PointF>>로 변경
            val lineList : Array<Array<PointF>>? = viewModel.lineList.value?.toTypedArray()
            val lineListAL  = ArrayList<ArrayList<PointSerializable>>()

            if(lineList != null){
                for(line : Array<PointF> in lineList){
                    var lineAL = ArrayList<PointSerializable>()
                    for(point : PointF in line){
                        lineAL.add( PointSerializable(point.x.toInt(), point.y.toInt()))
                    }
                    lineListAL.add( lineAL )
                }
            }

            // Gson을 이용 Json으로 변경
            val gson = Gson()
            val jsonOutput = gson.toJson(lineList)

            // Save the JSON to a file
            try {
                val fileOutputStream = context.openFileOutput(strFileName, Context.MODE_PRIVATE)
                val outputStreamWriter = OutputStreamWriter(fileOutputStream)
                    outputStreamWriter.use {
                        it.write(jsonOutput)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }


            Log.i("saveCurrentImage@DrawImageView>>", "file saved")
            Toast.makeText(contextParent, "file saved", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.i("saveCurrentImage@DrawImageView>>", "saving to file error : ${e.message}")
            Toast.makeText(contextParent, "saving to file error : ${e.message}", Toast.LENGTH_SHORT).show()
        }

        return strFileName
    }

//      Json을 이용해 저장하는 방법으로 대치 예정
//    fun saveCurrentImage() : String {
//        lateinit var strFileName : String
//        Toast.makeText(contextParent, "saving", Toast.LENGTH_SHORT)
//
//        try {
//            val strTime : Long = System.currentTimeMillis()
//            strFileName = "boardImage" + strTime + ".csr"
//            //val fileConent = "Hello World"
//
//            // Serialize the object to a byte array
//            val bos = ByteArrayOutputStream()
//            val oos = ObjectOutputStream(bos)
//
//
//            //viewModel의 lineList를  ArrayList<ArrayList<PointF>>로 변경
//            val lineList : Array<Array<PointF>>? = viewModel.lineList.value?.toTypedArray()
//            val lineListAL  = ArrayList<ArrayList<PointSerializable>>()
//
//            if(lineList != null){
//                for(line : Array<PointF> in lineList){
//                    var lineAL = ArrayList<PointSerializable>()
//                    for(point : PointF in line){
//                        lineAL.add( PointSerializable(point.x.toInt(), point.y.toInt()))
//                    }
//                    lineListAL.add( lineAL )
//                }
//            }
//            // oos  에 출력
//            oos.writeObject(lineListAL)
//            oos.flush()
//
//            // fileoutputStream을 열고 저장
//            var fos : FileOutputStream = context.openFileOutput(strFileName, Context.MODE_PRIVATE )
//            fos.write(bos.toByteArray())
//            fos.flush()
//            fos.close()
//
//            oos.close()
//            bos.close()
//
//            Log.i("saveCurrentImage@DrawImageView>>", "file saved")
//            Toast.makeText(contextParent, "file saved", Toast.LENGTH_SHORT).show()
//
//        } catch (e: Exception) {
//            Log.i("saveCurrentImage@DrawImageView>>", "saving to file error : ${e.message}")
//            Toast.makeText(contextParent, "saving to file error : ${e.message}", Toast.LENGTH_SHORT).show()
//        }
//
//        return strFileName
//    }

    // file을 읽어서 viewModel에 upload
    fun readImageFromFile(strFileName : String){
        val fileDirectory = contextParent.filesDir // Get the directory where your files are stored

        // file 열어서 Array<Array<PointF>> 형식으로 저장
        try {
            // Open the file for reading
            val fis = FileInputStream(File(fileDirectory, strFileName))
            val ois = ObjectInputStream(fis)

            // Load the data as ArrayList<ArrayList<PointSerializable>>
            val loadedData = ois.readObject() as ArrayList<ArrayList<PointSerializable>>

            // Initialize an empty Array<Array<PointF>>
            val data: Array<Array<PointF>> = Array(loadedData.size) { Array(0) { PointF(0f, 0f) } }

            // Convert the data into the desired structure
            for (i in loadedData.indices) {
                val row = loadedData[i]
                data[i] = Array(row.size) {
                    PointF(row[it].x.toFloat(), row[it].y.toFloat())
                }
            }
            Log.i("read file ... ", "${data}")
            // Now 'data' contains the converted data as an Array<Array<PointF>>
            ois.close()

            viewModel.setLineList(data)

        } catch (e: Exception) {
            Log.e("read file ... ", "${e.message}")
        }
    }


}