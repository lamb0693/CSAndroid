package com.example.csapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.csapp.databinding.ActivityMainBinding
import com.example.csapp.ui.counselList.CouselListViewAdapter
import com.example.csapp.ui.login.LoginActivity
import com.example.csapp.ui.main.MainViewModel
import com.example.csapp.ui.register.CreateMemberActivity
import com.google.android.material.internal.ContextUtils.getActivity


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


    fun checkPermission(){
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
    }

    private lateinit var couselListViewAdapter : CouselListViewAdapter
    val datas : List<String> = listOf<String>("Tom", "Jane", "Smith", "Wilson", "홍길동", "임꺽정",
        "야마모토", "다나카", "와타나베", "링링", "웨이웨이", "위고", "프랑소아")
    fun initRecyclerView(binding : ActivityMainBinding){
        binding.recyclerView.layoutManager =LinearLayoutManager(this)
        binding.recyclerView.adapter = CouselListViewAdapter(datas)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this,LinearLayoutManager.VERTICAL))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bndMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bndMain.root)

        checkPermission()
        initRecyclerView(bndMain)

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

        /* button 설정 */
//        bndMain.buttonUploadImage.setOnClickListener{
//            val intent = Intent(this, DrawImageActivity::class.java)
//            startActivity(intent)
//        }
        /***** menu 로 이동 ******/
//        bndMain.buttonLogin.setOnClickListener(View.OnClickListener {
//            val intent = Intent(this, LoginActivity::class.java)
//            getResult.launch(intent)
//        })
        /***** menu 로 이동 ******/
//        bndMain.btnRegister.setOnClickListener(View.OnClickListener {
//            val intent = Intent(this, CreateMemberActivity::class.java)
//            startActivity(intent)
//        })

        bndMain.editMessage.setOnEditorActionListener { _, actionId, event ->
            Log.i("onCreate>>", "Enter key in editMesssage")
            //return@setOnEditorActionListener true
            true// event를 propagation 하지 않고 consume
        }

        // enter 시 keyboard 내리기 ==> Error
        bndMain.editMessage.setOnKeyListener(View.OnKeyListener { view, keyCode, event ->
            Log.i("onCreate>>","key clicked" )
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val inputManager :InputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager ;
                inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                return@OnKeyListener true
            }
            false
        })
        bndMain.editMessage.setOnFocusChangeListener{view, hasFocus ->
            Log.i("onCreate>>", "focus changed to $hasFocus")
            if(!hasFocus){
                val inputManager :InputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager ;
                inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }


        bndMain.buttonPlus.setOnClickListener(){
            Log.i("onCreate>>", "+ button clicked")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //menu?.clear()
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId){
        R.id.menuLogin -> {
            Log.i("onCreate>>", "menu1 clicked")
            val intent = Intent(this, LoginActivity::class.java)
            getResult.launch(intent)
            true // 소모시켰으면 true
        }
        R.id.menuRegister-> {
            Log.i("onCreate>>", "menu2 clicked")
            val intent = Intent(this, CreateMemberActivity::class.java)
            startActivity(intent)
            true
        }
        else -> {
            Log.i("onCreate>>", "else selected")
            super.onOptionsItemSelected(item)
        }
        //return super.onOptionsItemSelected(item) 위의 else에 넣어줌
    }
}