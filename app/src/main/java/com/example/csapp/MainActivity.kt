package com.example.csapp

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.RemoteInput
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider

import com.example.csapp.databinding.ActivityMainBinding
import com.example.csapp.ui.drawimage.DrawImageActivity
import com.example.csapp.ui.login.LoginActivity
import com.example.csapp.ui.register.CreateMemberActivity

class MainActivity : AppCompatActivity() {

    val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        // xml에 있는 권한 file들 하나하나에 대한 설정값을 체크해서 각각
        if (it.all { permission -> permission.value == true }) {
            Toast.makeText(this, "permisson permitted...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "permission denied...", Toast.LENGTH_SHORT).show()
        }
    }

    private val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result->
        if(result.resultCode == Activity.RESULT_OK){
            val displayName = result.data?.getStringExtra("USER_NAME")
            val accessToken = result.data?.getStringExtra("ACCESS_TOKEN")
            val refreshToken = result.data?.getStringExtra("REFRRESH_TOKEN")
            Log.i("getREsult@MainActivity>>", "displayName : ${displayName}")
            Log.i("getREsult@MainActivity>>", "access token : ${accessToken}")
            Log.i("getREsult@MainActivity>>", "refreshToken : ${refreshToken}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bndMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bndMain.root)

        // permission을 확인하고, 없으면 요청한다
        val status = ContextCompat.checkSelfPermission(this,
            "android.permission.POST_NOTIFICATIONS")
        if (status == PackageManager.PERMISSION_GRANTED) {
            Log.d(">>", "Permission Granted")
        } else {
            Log.d(">>", "Permission Denied")
            permissionLauncher.launch(
                arrayOf(
                    "android.permission.POST_NOTIFICATIONS"
                )
            )
        }
        //End of request for permission


        bndMain.buttonUploadImage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, DrawImageActivity::class.java)
            startActivity(intent)
//            val testToast:Toast
//            Toast.makeText(this@MainActivity, "clicked", Toast.LENGTH_SHORT).show()
        })

        ViewModelProvider(this)

        bndMain.buttonLogin.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            getResult.launch(intent)
        })

        bndMain.btnRegister.setOnClickListener((View.OnClickListener {
            val intent = Intent(this, CreateMemberActivity::class.java)
            startActivity(intent)
        }))

    }
}