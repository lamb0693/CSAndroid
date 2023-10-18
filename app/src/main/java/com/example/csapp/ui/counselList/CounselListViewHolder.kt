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

    //===== [Click 이벤트 구현을 위해 추가된 코드] 만든 코드==========================
    interface OnItemClickListener {
        fun onItemClicked(position: Int, data: String?)
    }

    // OnItemClickListener 참조 변수 선언
    private var itemClickListener: OnItemClickListener? = null

    // OnItemClickListener 전달 메소드
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemClickListener = listener
    }
    //===== [Click 이벤트 구현을 위해 추가된 코드] 만든 코드 끝========================

}