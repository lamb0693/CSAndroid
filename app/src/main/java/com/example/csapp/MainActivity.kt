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
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.csapp.databinding.ActivityMainBinding
import com.example.csapp.ui.counselList.CouselListViewAdapter
import com.example.csapp.ui.drawimage.DrawImageActivity
import com.example.csapp.ui.login.LoginActivity
import com.example.csapp.ui.main.MainViewModel
import com.example.csapp.ui.register.CreateMemberActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory


class MainActivity : AppCompatActivity() {
    lateinit var  bndMain : ActivityMainBinding
    //lateinit var counselList : List<CouselListDTO>
    lateinit var counselListViewAdapter : CouselListViewAdapter
    // 이것은 activity 만들면서 바로 만들어지므로 사용 가능
    private lateinit var viewModel : MainViewModel

    /*
     * permissionLauncjer
     */
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

    /*
     *loginActivity로 부터의 result를 받는다.
     */
    private val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result->
        if(result.resultCode == Activity.RESULT_OK){
            val displayName = result.data?.getStringExtra("USER_NAME")
            val accessToken = result.data?.getStringExtra("ACCESS_TOKEN")
            val refreshToken = result.data?.getStringExtra("REFRRESH_TOKEN")

            // 값을 viewModel에 설정
            if(displayName!=null) viewModel.setDisplayName(displayName)
            if(accessToken!=null) viewModel.setAccessToken(accessToken)
            if(refreshToken!=null) viewModel.setRefreshToken(refreshToken)

            // 없어질 경우를 대비해 globalVariable에 저장
            GlobalVariable.getInstance()?.setAccessToken(accessToken)
            GlobalVariable.getInstance()?.setUserName(displayName)
            GlobalVariable.getInstance()?.setRefreshToken(refreshToken)

//          이 result는 binding 되기 전에 시행이라 이렇게 하면 안됨
//            bndMain.textUserName.setText(displayName)
        }
    }

    /*
     *퍼미션을 체크하고 없으면 요청한다
     */
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

    /*
     * Recycler View를 초기화  viewModel 을 초기화 후 사용해야 함
     */
    fun initRecyclerView(binding : ActivityMainBinding){
        counselListViewAdapter = CouselListViewAdapter(viewModel.counselList.value ?: mutableListOf())
        binding.recyclerView.layoutManager =LinearLayoutManager(this)
        binding.recyclerView.adapter = counselListViewAdapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this,LinearLayoutManager.VERTICAL))

        // adapter에 내가 만든 listener를 설정한다.
        counselListViewAdapter.setOnItemClickListener(object : CouselListViewAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // Handle the item click here
                //val clickedItem = yourCounselListData[position]
                Log.i("counselListViewAdapter setOnItemClickListener>> position : ", "${position}")
                // Perform the desired action when an item is clicked
                launchCustomDialog()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // 여기서 부르면 null 이 됨,  따라서 밑의 사항은 안 됨
        val txtUserName : TextView? = findViewById<TextView>(R.id.textUserName)
        val btnLogin  = findViewById<Button>(R.id.buttonLogin)
        if(txtUserName == null) Log.i("onResume@MainActivity", "txtUserName : null")
        txtUserName?.text = viewModel.displayName.value
        //binding이 없어 사망한다
        //if(bndMain!=null && viewModel.displayName.value != "anonymous") bndMain.buttonLogin.isEnabled = false
        Log.i("onResume@MainActivity>>", "is Called" )

        GlobalScope.launch {
            val ret : String = getCounselListFromServer()
            Log.i("onResume@Main>>", "lauch Result $ret")
        }
    }

    /*
     *MainViewModel을 초기화 하고   observer를 설정
    */
    fun initializeViewModel(bndMain : ActivityMainBinding){
        /* viewModel 설정및 observer 선언 */
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.accessToken.observe(this) {
            Log.i("onCreate@Main", "accessTokenChanged ${viewModel.accessToken.value}")
        }

        // viewModel에서  set 해도 다른 activity 갔다 오면 없어짐
        // viewmodel이 바뀌면(즉 mainActivity가 사망했다 다시 created) textUserName을 ##GlobalVariable##을 불러 세팅한다
        viewModel.displayName.observe(this) {
            Log.i("onCreate@Main", "displayNameChanged ${viewModel.displayName.value}")
            var username : String? = GlobalVariable.getInstance()?.getUserName()
            if(username != null && !username.equals("anonymous")){
                Log.i("displayName.observe  username>>", username)
                bndMain.textUserName.text  = username
                bndMain.buttonLogin.isEnabled = false
                // username(tel)이 있으면 상담 내역을 불러와서  setting 한다
                GlobalScope.launch {
                    val ret : String = getCounselListFromServer()
                    Log.i("displayName.observe@Main>>", "lauch Result $ret")
                }
            } else {
                Log.i("displayName.observe  username >>", "null")
                bndMain.textUserName.text  = "anonymous"
                bndMain.buttonLogin.isEnabled = true
            }
        }

        viewModel.counselList.observe(this) { newList->
            Log.i("onCreate@Main", "counselList in viewModel changed ${viewModel.counselList.value}")
            counselListViewAdapter.setData(newList)
            counselListViewAdapter.notifyDataSetChanged()
        }
    }

    /*
    * plus button에 대한  floating menu 설정
    * binding이 아직 없으니 methoid 설정 단계에서  error. 매개변수로  binding 넣어야
    */
    fun showPulsPopupMenu(binding : ActivityMainBinding){
        Log.i("onCreate>>", "+ button clicked")
        val popUpMenu = PopupMenu(this, binding.buttonPlus)
        popUpMenu.menuInflater.inflate(R.menu.plus_menu, popUpMenu.menu)
        popUpMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuPicture -> {
                    Log.i("onCreate>>", "menuPicture clicked")
                    return@setOnMenuItemClickListener true
                }
                R.id.menuPaint -> {
                    Log.i("onCreate>>", "menuPaint clicked")
                    val intent = Intent(this, DrawImageActivity::class.java)
                    startActivity(intent)
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
        popUpMenu.show()
    }

    // chatting message를 upload
    suspend fun uploadChatMessage(binding: ActivityMainBinding) : String {
        Log.i("uploadChatMessage@Main", "uploadChatMessage executed")
        try {
            val strToken : String? = GlobalVariable.getInstance()?.getAccessToken()
            if(strToken == null) return ("accesstoken null")
            var username : String? = GlobalVariable.getInstance()?.getUserName()
            if(username == null) return ("username null")
            if(binding.editMessage.text.toString().equals("")) return ("no message")

            // return이 plain text라 scalar인 service를 사용한다
            val response = withContext(Dispatchers.IO) {
                RetrofitScalarObject.getApiService().createBoard("Bearer:"+strToken,
                    username, "TEXT", binding.editMessage.text.toString(), null).execute()
            }

            // response 를 처리 성공하면 counselList를 새로 불러 온다
            if(response.isSuccessful){
                val result = response.body() as String
                Log.i("uploadChatMessage >>>>", "$result")
                withContext(Dispatchers.Main) {
                    getCounselListFromServer()
                    binding.editMessage.setText("")
                }
                return "success"
            }else {
                Log.i("login >>", "bad request ${response.code()}")
                return "error bad request ${response.code()}"
            }
        } catch (e: Throwable) {
            return e.message!!
        }

    }

    fun launchCustomDialog(){

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("onCreate@Main>> ", "onCreate@Main is called")

        val bndMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bndMain.root)

        checkPermission()
        initializeViewModel(bndMain)

        // viewModel 이 필요하다 ViewModel 설정후  초기화
        initRecyclerView(bndMain)

        // CounselList를 읽어온다  accessToken이 없으면 되돌아 온다.
        GlobalScope.launch {
            val ret : String = getCounselListFromServer()
            Log.i("onCreate@Main>>", "lauch Result $ret")
        }

        /* button 설정 */
//        bndMain.buttonUploadImage.setOnClickListener{
//            val intent = Intent(this, DrawImageActivity::class.java)
//            startActivity(intent)
//        }
        /***** Login button ******/
        bndMain.buttonLogin.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            getResult.launch(intent)
        })

        /***** menu 로 이동 ******/
        bndMain.btnRegister.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, CreateMemberActivity::class.java)
            startActivity(intent)
        })

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
        // focus 잃으면 keyboard 내리기 ?
        bndMain.editMessage.setOnFocusChangeListener{view, hasFocus ->
            Log.i("onCreate>>", "focus changed to $hasFocus")
            if(!hasFocus){
                val inputManager :InputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager ;
                inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }

        // 플러스 버튼에 대한  Listener
        bndMain.buttonPlus.setOnClickListener(){
            showPulsPopupMenu(bndMain)
        }

        bndMain.buttonSendMessage.setOnClickListener{
            Log.i("onCreate>>", "sendMessageButton Clicked")
            GlobalScope.launch {
                val ret : String = uploadChatMessage(bndMain)
                Log.i("buttonSendMessage@Main>>", "lauch Result $ret")
            }
        }
    }

    /*
     * Server 로 부터 counselList를 가져 온다
     * 가져오는 숫자 나중에 조절
     */
    private suspend fun getCounselListFromServer() : String {
        Log.i("getCounselListFromServer@Main", "getCounselListFromServer executed")
        try {
            val strToken : String? = GlobalVariable.getInstance()?.getAccessToken()
            if(strToken == null) return ("accesstoken null")
            var username : String? = GlobalVariable.getInstance()?.getUserName()
            if(username == null) return ("username null")
            //  suspend Networdk function은 안에서 main thread가 아닌 thread로 실행
            val response = withContext(Dispatchers.IO) {
                RetrofitObject.getApiService().listBoard("Bearer:"+strToken, username, 50).execute()
            }

            // response 를 처리
            if(response.isSuccessful){
                val result = response.body() as MutableList<CouselListDTO>
                // main의 변수를 건드리려면 Dispatchers.Main에서 시행
                withContext(Dispatchers.Main) {
                    viewModel.setCounselList(result)
                    Log.i("getCounselListFromServer >>counselList", "$result")
                }

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