package com.example.csapp.ui.counselList

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.csapp.databinding.ItemsRecyclerBinding

class CounselListViewHolder(val binding : ItemsRecyclerBinding): RecyclerView.ViewHolder(binding.root) {
    lateinit  var tvName : TextView
    init {
        tvName = binding.tvRvName

        tvName.setOnClickListener(View.OnClickListener {
            Log.i(">>", adapterPosition.toString())
        })
    }

}