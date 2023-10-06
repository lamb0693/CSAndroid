package com.example.csapp.ui.login

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val displayName: String,
    //... other data fields that may be accessible to the UI
    val accessToken : String,
    val refreshToken : String
)