package com.example.csapp.data

import android.util.Log
import com.example.csapp.connectapi.RetrofitObject
import com.example.csapp.dto.TokenStructure
import com.example.csapp.data.model.LoggedInUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    // suspend로 바꿈 --  부르는 곳도  suspend나  coroutine으로 바꾸어 줌
    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication
//            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
////            return Result.Success(fakeUser)
            var strAccessToken : String? = null
            var strRefreshToken : String? = null

            val param = mutableMapOf<String, String>()
            param["tel"] = username
            param["password"] = password

            //  suspend 안에서 withContext(Dispatcher.IO) main thread가 아닌 thread로 실행
            val response = withContext(Dispatchers.IO) {
                RetrofitObject.getApiService().login(param).execute()
            }

            // response 를 처리
            if(response.isSuccessful){
                val tokens : TokenStructure = response.body() as TokenStructure
                strAccessToken = tokens.accessToken
                strRefreshToken = tokens.refreshToken
                Log.i("login >>", "loginSuccess")
                val user = LoggedInUser(username, username, strAccessToken, strRefreshToken) // username 에 username넣음
                return Result.Success(user)
            }else {
                Log.i("login >>", "bad request ${response.code()}")
                Log.i("login >>", "login Fail")
                return Result.Error(RuntimeException("check Id and password"))
            }
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }

    }

    fun logout() {
        // TODO: revoke authentication
    }
}