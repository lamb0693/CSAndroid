package com.example.csapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider

import com.example.csapp.databinding.ActivityMainBinding
import com.example.csapp.ui.drawimage.DrawImageActivity
import com.example.csapp.ui.login.LoginActivity
import com.example.csapp.ui.main.MainViewModel
import com.example.csapp.ui.register.CreateMemberActivity

class MainActivity : AppCompatActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        // xml에 있는 권한 file들 하나하나에 대한 설정값을 체크해서 각각
        if (it.all { permission -> permission.value == true }) {
            Toast.makeText(this, "permission permitted...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "permission denied...", Toast.LENGTH_SHORT).show()
        }
    }

    private val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result->
        if(result.resultCode == Activity.RESULT_OK){
            val displayName = result.data?.getStringExtra("USER_NAME")
            if(displayName!=null) viewModel.setDisplayName(displayName)
            val accessToken = result.data?.getStringExtra("ACCESS_TOKEN")
            if(accessToken!=null) viewModel.setAccessToken(accessToken)
            val refreshToken = result.data?.getStringExtra("REFRRESH_TOKEN")
            if(refreshToken!=null) viewModel.setRefreshToken(refreshToken)
            Log.i("getREsult@MainActivity>>", "displayName : $displayName")
            Log.i("getREsult@MainActivity>>", "access token : $accessToken")
            Log.i("getREsult@MainActivity>>", "refreshToken : $refreshToken")
        }
    }

    private lateinit var viewModel : MainViewModel

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


        bndMain.buttonUploadImage.setOnClickListener{
            val intent = Intent(this, DrawImageActivity::class.java)
            startActivity(intent)
//            val testToast:Toast
//            Toast.makeText(this@MainActivity, "clicked", Toast.LENGTH_SHORT).show()
        }

        /* viewModel 설정및 observer 선언 */
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.accessToken.observe(this) {
            Log.i("onCreate@Main", "accessTokenChanged ${viewModel.accessToken.value}")
        }

        viewModel.displayName.observe(this) {
            Log.i("onCreate@Main", "displayNameChanged ${viewModel.displayName.value}")
            bndMain.textUserName.setText(viewModel.displayName.value)
            if(viewModel.displayName.value != "anonymous") bndMain.buttonLogin.isEnabled = false
        }
        /*  viewModel 설정 끝 */

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