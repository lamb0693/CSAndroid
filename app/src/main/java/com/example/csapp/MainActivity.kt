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
import com.example.csapp.data.Result
import com.example.csapp.data.model.LoggedInUser
import com.example.csapp.databinding.ActivityMainBinding
import com.example.csapp.ui.counselList.CouselListViewAdapter
import com.example.csapp.ui.login.LoginActivity
import com.example.csapp.ui.main.MainViewModel
import com.example.csapp.ui.register.CreateMemberActivity
import com.google.android.material.internal.ContextUtils.getActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class MainActivity : AppCompatActivity() {
    lateinit var  bndMain : ActivityMainBinding

    lateinit var counselList : List<CouselListDTO>

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
            val accessToken = result.data?.getStringExtra("ACCESS_TOKEN")
            val refreshToken = result.data?.getStringExtra("REFRRESH_TOKEN")
            // viewModel observer에서 GlobalVariable을 사용 => 먼저 설정해 두어야 함
            GlobalVariable.getInstance()?.setAccessToken(accessToken)
            GlobalVariable.getInstance()?.setUserName(displayName)
            GlobalVariable.getInstance()?.setRefreshToken(refreshToken)
//**************   global varaiable 은 viewModel을 쓰면 없어져 버린다. ***********//
            if(displayName!=null) viewModel.setDisplayName(displayName)
            if(accessToken!=null) viewModel.setAccessToken(accessToken)
            if(refreshToken!=null) viewModel.setRefreshToken(refreshToken)
//            Log.i("getREsult@MainActivity>>", "displayName : $displayName")
//            Log.i("getREsult@MainActivity>>", "access token : $accessToken")
//            Log.i("getREsult@MainActivity>>", "refreshToken : $refreshToken")

//          이 result는 binding 되기 전에 시행이라 이렇게 하면 안됨
//            bndMain.textUserName.setText(displayName)

        }
    }

    // 이것은 activity 만들면서 바로 만들어지므로 사용 가능
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

        Log.i("onCreate@Main>> ", "onCreate@Main is called")

        val bndMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bndMain.root)

        checkPermission()

        /* viewModel 설정및 observer 선언 */
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

//        viewModel.accessToken.observe(this) {
//            Log.i("onCreate@Main", "accessTokenChanged ${viewModel.accessToken.value}")
//        }

        // viewModel에서  set 해도 다른 intent 갔다 오면 없어짐
        // viewmodel이 바뀌면 GlobalVariable을 불러 사용
        // 그냥 오면 result 가 없이 돌아오니 GlobalVarialble의 변수를 썼다
        viewModel.displayName.observe(this) {
            Log.i("onCreate@Main", "displayNameChanged ${viewModel.displayName.value}")
            bndMain.textUserName.text = viewModel.displayName.value
            if(viewModel.displayName.value != "anonymous") bndMain.buttonLogin.isEnabled = false
        }

        GlobalScope.launch {
            val ret : String = getCounselListFromServer()
            Log.i("onCreate@Main>>", "lauch Result $ret")
        }

        // data 를 set  한 후  RecyclerView를 init
        initRecyclerView(bndMain)

        /*  viewModel 설정 끝 */
        /* global variable에서  usernaem이 있으면 불러옴 */
        // 없어도 될듯 함 ..
//        val userName :String? = GlobalVariable.getInstance()?.getUserName()
//        if(userName != null) {
//            bndMain.textUserName.setText(userName)
//        } else {
//            Log.i("onCreate@Main>> ", "userName in globalVariable is null")
//        }

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

        // enter 시 keyboard 내리기 ==> avd 에서는 안됨  why?
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

    // Server 로 부터 counselList를 가져 온다
    private suspend fun getCounselListFromServer() : String {
        Log.i("getCounselListFromServer@Main", "getCounselListFromServer executed")
        try {
            val strToken : String? = GlobalVariable.getInstance()?.getAccessToken()
            if(strToken == null) return ("accesstoken null")
            var username : String? = GlobalVariable.getInstance()?.getUserName()
            if(username == null) return ("username null")
            //  suspend 안에서 withContext(Dispatcher.IO) main thread가 아닌 thread로 실행
            val response = withContext(Dispatchers.IO) {
                RetrofitObject.getApiService().listBoard("Bearer:"+strToken, username, 10).execute()
            }

            // response 를 처리
            if(response.isSuccessful){
                counselList = response.body() as MutableList<CouselListDTO>
                Log.i("getCounselListFromServer >>", "Success")
                return "success"
            }else {
                Log.i("login >>", "bad request ${response.code()}")
                Log.i("login >>", "login Fail")
                return "error bad request ${response.code()}"
            }
        } catch (e: Throwable) {
            return e.message!!
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