package com.example.csapp.ui.counselList

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.csapp.CouselListDTO
import com.example.csapp.GlobalVariable
import com.example.csapp.R
import com.example.csapp.databinding.ItemsRecyclerBinding

class CouselListViewAdapter(var counselList: MutableList<CouselListDTO>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //**** add for click listener on view Holder (2)  OnItemClickListener 는 밑에
    private var itemClickListener: OnItemClickListener? = null

    //**** add for click listener on view Holder (1)
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    //**** add for click listener on view Holder (3)
    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

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
            if(counselList[position].name.contains("CSR")){
                binding.tvRvName.text = "상담원"
                binding.tvRvName.setTextColor(Color.MAGENTA)
            } else {
                binding.tvRvName.text = counselList[position].name
                binding.tvRvName.setTextColor(Color.BLUE)
            }

            binding.tvRvTimestamp.text = counselList[position].strUpdatedAt
            when(counselList[position].content){
                "AUDIO" -> binding.imageView.setImageResource(R.drawable.speaker)
                "TEXT" -> binding.imageView.setImageResource(R.drawable.keyboard)
                "PAINT" -> binding.imageView.setImageResource(R.drawable.board)
            }
            binding.tvRvMessage.text = counselList[position].message
        } catch (e: Exception) {
            Log.e("Adapter", "Error binding data at position $position: ${e.message}")
        }

        //**** add for click listener on view Holder (4)
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        // to be modified
        Log.i("getItemCount@CouselListViewAdapter>>", "${counselList}")
        Log.i("getItemCount@CouselListViewAdapter>>", "${counselList.size}")
        return counselList.size;
    }

}