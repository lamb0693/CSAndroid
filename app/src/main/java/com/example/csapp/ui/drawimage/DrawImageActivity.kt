package com.example.csapp.ui.drawimage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.example.csapp.R
import com.example.csapp.databinding.ActivityDrawImageBinding


class DrawImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bnImageView = ActivityDrawImageBinding.inflate(layoutInflater)
        setContentView(bnImageView.root)

        val myView : DrawImageView = DrawImageView(this)

        val layoutDrawImage = findViewById<FrameLayout>(R.id.frameLayoutView)
        layoutDrawImage.addView(myView)

        bnImageView.buttonClear.setOnClickListener(View.OnClickListener {
            myView.clearAll()
        })

        bnImageView.buttonEraseLast.setOnClickListener(View.OnClickListener {
            myView.removeLastLine()
        })

        bnImageView.buttonReturn.setOnClickListener(View.OnClickListener {
            finish()
        })
    }
}