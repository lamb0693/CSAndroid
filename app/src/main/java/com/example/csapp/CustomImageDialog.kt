package com.example.csapp

import android.app.Dialog
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File


class CustomImageDialog(context: Context) : Dialog(context) {

    public lateinit var imageView: ImageView
    init {
        setContentView(R.layout.custom_img_dialog) // Use your custom layout
        imageView = findViewById(R.id.ivDisplayImageDlg)
    }

}