package com.example.csapp.ui.drawimage

import android.graphics.Point
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.csapp.GlobalVariable
import com.example.csapp.R
import com.example.csapp.RetrofitScalarObject
import com.example.csapp.databinding.ActivityDrawImageBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException


class DrawImageActivity : AppCompatActivity() {
    lateinit var myView : DrawImageView

    suspend fun uploadImage(strFileName : String) : String {
        Log.i("uploadImage@DrawImageActivity", "uploadImage executed")
        try {
            val strToken : String? = GlobalVariable.getAccessToken()
            if(strToken == null) return ("accesstoken null")
            var username : String? = GlobalVariable.getUserName()
            if(username == null) return ("username null")

            var filePart: MultipartBody.Part? = null
            if (strFileName != null) {
                val fileDir = this.applicationContext.filesDir
                val upFile = File(fileDir, strFileName)
                val requestBodyFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), upFile)
                filePart = MultipartBody.Part.createFormData("file", upFile.name, requestBodyFile)
            }

            // return이 plain text라 scalar인 service를 사용한다
            val response = withContext(Dispatchers.IO) {
                RetrofitScalarObject.getApiService().createBoard("Bearer:"+strToken,
                    username, "PAINT", "그림판 이미지 입니다", filePart).execute()
            }

            // response 를 처리 성공하면 counselList를 새로 불러 온다
            if(response.isSuccessful){
                val result = response.body() as String
                Log.i("uploadChatMessage >>>>", "$result")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DrawImageActivity.applicationContext,"upload에 성공하였습니다", Toast.LENGTH_SHORT)
                }
                return "success"
            }else {
                Log.i("login >>", "bad request ${response.code()}")
                Toast.makeText(this@DrawImageActivity.applicationContext,"upload실패 ${response.code()}", Toast.LENGTH_SHORT)
                return "error bad request ${response.code()}"
            }
        } catch (e: Throwable) {
            return e.message!!
            Toast.makeText(this@DrawImageActivity.applicationContext,"upload실패 ${e.message}", Toast.LENGTH_SHORT)

        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bnImageView = ActivityDrawImageBinding.inflate(layoutInflater)
        setContentView(bnImageView.root)

        setSupportActionBar(bnImageView.toolbar2)

        myView  = DrawImageView(this)

        val layoutDrawImage = findViewById<FrameLayout>(R.id.frameLayoutView)
        layoutDrawImage.addView(myView)

        // if file이 있으면 불러온다
        val strFileName : String? = intent.getStringExtra("paint_file_path")
        if(strFileName != null ){
            Log.i("getExtra", strFileName)
            myView.readImageFromFile(strFileName)
        }

        bnImageView.buttonClear.setOnClickListener(View.OnClickListener {
            myView.clearAll()
        })

        bnImageView.buttonEraseLast.setOnClickListener(View.OnClickListener {
            myView.removeLastLine()
        })

        bnImageView.buttonReturn.setOnClickListener(View.OnClickListener {
            finish()
        })

        bnImageView.buttonUpload.setOnClickListener{
            //   현재의 lineList를 저장하고 file이름을 return으로 받음
            val strSavedFilename : String = myView.saveCurrentImage()


            // file upload
            GlobalScope.launch {
                val ret : String = uploadImage(strSavedFilename )
                Log.i("onCreate@Main>>", "lauch Result $ret")
                uploadImage(strSavedFilename )
            }
        }
    }
}