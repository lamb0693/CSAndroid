package com.example.csapp.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.csapp.RetrofitObject
import com.example.csapp.TokenStructure
import com.example.csapp.databinding.ActivityCreateMemberBinding


class CreateMemberActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityCreateMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val param = mutableMapOf<String, String>()
            param["tel"] = "01031795981"
            param["password"] = "00000000"
            RetrofitObject.getApiService().login(param )
                .enqueue(object : retrofit2.Callback<TokenStructure> {
                    override fun onResponse(
                        call: retrofit2.Call<TokenStructure>,
                        response: retrofit2.Response<TokenStructure>
                    ) {

                        Log.i("login", "response : ${response.body()}")
                    }

                    override fun onFailure(call: retrofit2.Call<TokenStructure>, t: Throwable) {
                        Log.i("login", "response : fail")
                    }

                })


        }

    }

}


