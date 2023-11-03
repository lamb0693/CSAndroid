package com.example.csapp

import android.app.AlertDialog
import android.content.Context

class SimpleNotiDialog(context : Context, title : String, message : String) : AlertDialog.Builder(context) {
    init{
        setTitle(title)
        setMessage(message)
        setPositiveButton("확인") { dialog, _ ->
            // Handle the OK button click here
            dialog.dismiss()
        }
    }

    fun showDialog(){
        this.create().show()
    }
}