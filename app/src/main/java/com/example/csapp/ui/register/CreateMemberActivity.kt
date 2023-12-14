package com.example.csapp.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.csapp.dto.RegisterDTO
import com.example.csapp.connectapi.RetrofitScalarPlusObject
import com.example.csapp.databinding.ActivityCreateMemberBinding
import kotlinx.coroutines.launch


class CreateMemberActivity : AppCompatActivity() {


    suspend fun register(dto : RegisterDTO)  : String {
        var returnVal : String = ""
        RetrofitScalarPlusObject.getApiService().register(dto)
            .enqueue(object : retrofit2.Callback<String> {

                override fun onResponse(
                    call: retrofit2.Call<String>,
                    response: retrofit2.Response<String>
                ) {
                    Log.i("login", "response : ${response.body()}")
                    returnVal = response.body().toString()
                }

                override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                    Log.i("login", "response : fail")
                    returnVal = t.message.toString()
                }
            })
        return returnVal
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityCreateMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            var dto : RegisterDTO = RegisterDTO(
                binding.editTel.text.toString(),
                binding.editName.text.toString(),
                binding.editPassword1.text.toString()
            )

            lifecycleScope.launch {
                var ret = register(dto)
                Log.i("return from register", "$ret")
            }

        }

    }

}


