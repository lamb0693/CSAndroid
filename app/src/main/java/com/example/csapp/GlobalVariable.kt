package com.example.csapp

class GlobalVariable {
    private var accessToken: String? = null
    private var refreshToken: String? =null
    private var userName:String? = null
    fun getAccessToken(): String? {
        return accessToken
    }
    fun getRefreshToken(): String? {
        return refreshToken
    }
    fun getUserName() : String? {
        return userName
    }

    fun setAccessToken(data: String?) {
        this.accessToken = data
    }

    fun setRefreshToken(data: String?) {
        this.refreshToken = data
    }

    fun setUserName(data: String?) {
        this.userName = data
    }

    companion object{
        private var instance: GlobalVariable? = null

        @Synchronized
        fun getInstance(): GlobalVariable? {
            if (null == instance) {
                instance = GlobalVariable()
            }
            return instance
        }
    }
}