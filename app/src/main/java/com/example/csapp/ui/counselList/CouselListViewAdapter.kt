package com.example.csapp.ui.counselList

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.csapp.CouselListDTO
import com.example.csapp.databinding.ItemsRecyclerBinding

class CouselListViewAdapter(var counselList: MutableList<CouselListDTO>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun setData(newData: MutableList<CouselListDTO>) {
        counselList = newData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        return CounselListViewHolder(ItemsRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.i("onBindViewHolder>>", "position : $position")
        val binding = (holder as CounselListViewHolder).binding
        Log.i("onBindViewHolder", counselList.toString())
        Log.i("onBindViewHolder", "Data at position $position: ${counselList[position]}")
        try {
            binding.tvRvName.text = counselList[position].name
            binding.tvRvContent.text = counselList[position].content
            binding.tvRvMessage.text = counselList[position].message
        } catch (e: Exception) {
            Log.e("Adapter", "Error binding data at position $position: ${e.message}")
        }
    }

    override fun getItemCount(): Int {
        // to be modified
        Log.i("getItemCount@CouselListViewAdapter>>", "${counselList}")
        Log.i("getItemCount@CouselListViewAdapter>>", "${counselList.size}")
        return counselList.size;
    }

}