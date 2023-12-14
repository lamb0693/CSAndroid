package com.example.csapp.ui.drawimage

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.csapp.util.GlobalVariable
import com.example.csapp.R
import com.example.csapp.connectapi.RetrofitScalarObject
import com.example.csapp.util.SimpleNotiDialog
import com.example.csapp.util.SocketManager
import com.example.csapp.databinding.ActivityDrawImageBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class DrawImageActivity : AppCompatActivity() {
    lateinit var myView : DrawImageView
    private var connectionStatus : Int = 0

    private var  socketManager: SocketManager = SocketManager.getInstance()

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

            if(response.isSuccessful){
                val result = response.body() as String
                Log.i("uploadChatMessage >>>>", "$result")
                // connection안 된 경우에는 counselList를  불러 올 필요가 없다.  MainActivity로 가면 resume에서 불러 온다
                if(connectionStatus == 2){
                    socketManager.sendUpdateBoardMessage()
                }
                runOnUiThread {
                    SimpleNotiDialog(this@DrawImageActivity, "upload", "그림 파일이 업로드 됨").showDialog()
//                    Toast.makeText(this@DrawImageActivity,"upload에 성공하였습니다", Toast.LENGTH_SHORT)
                }
                return "success"
            }else {
                Log.i("login >>", "bad request ${response.code()}")
                runOnUiThread{
                    Toast.makeText(this@DrawImageActivity,"upload실패 ${response.code()}", Toast.LENGTH_SHORT).show()
                }
                return "error bad request ${response.code()}"
            }
        } catch (e: Throwable) {
            return e.message!!
            runOnUiThread{
                Toast.makeText(this@DrawImageActivity,"upload실패 ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // socketManager instance를 얻음
        if(socketManager==null) socketManager = SocketManager.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // socketManager instance를 얻음
        if(socketManager==null) socketManager = SocketManager.getInstance()

        // intent를 이용해서 onConnect값을 바꾸어 주어야 함
        connectionStatus =  intent.getIntExtra("status", 0)

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

            var username : String? = GlobalVariable.getUserName()
            if(username == null || username.equals("")) {
                Toast.makeText(applicationContext, "not login state", Toast.LENGTH_SHORT)
            } else {
                // file upload
                GlobalScope.launch {
                    val ret : String = uploadImage(strSavedFilename )
                    Log.i("onCreate@Main>>", "lauch Result $ret")
                }
            }
        }
    }
}