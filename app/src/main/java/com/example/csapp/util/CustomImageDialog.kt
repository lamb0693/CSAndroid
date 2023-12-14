package com.example.csapp.util

import android.app.Dialog
import android.content.Context
import android.widget.ImageView
import com.example.csapp.R


class CustomImageDialog(context: Context) : Dialog(context) {

    public lateinit var imageView: ImageView
    init {
        setContentView(R.layout.custom_img_dialog) // Use your custom layout
        imageView = findViewById(R.id.ivDisplayImageDlg)
    }

}