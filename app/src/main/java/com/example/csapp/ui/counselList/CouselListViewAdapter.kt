package com.example.csapp.ui.counselList

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.csapp.databinding.ItemsRecyclerBinding

class CouselListViewAdapter(val datas : List<String>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder  =
        CounselListViewHolder(ItemsRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.i("onBindViewHolder>>", "position : $position")
        val binding = (holder as CounselListViewHolder).binding
        Log.i("onBindViewHolder", datas.toString())
        Log.i("onBindViewHolder", "Data at position $position: ${datas[position]}")
        try {
            binding.tvRvName.text = datas[position]
        } catch (e: Exception) {
            Log.e("Adapter", "Error binding data at position $position: ${e.message}")
        }
    }

    override fun getItemCount(): Int {
        // to be modified
        return datas.size;
    }

}