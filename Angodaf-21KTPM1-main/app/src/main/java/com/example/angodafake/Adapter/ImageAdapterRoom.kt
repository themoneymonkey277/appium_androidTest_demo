package com.example.angodafake.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.angodafake.R
import com.example.angodafake.db.Picture_Room

class ImageAdapterRoom : ListAdapter<Picture_Room,ImageAdapterRoom.ViewHolder>(DiffCallback()){

    class DiffCallback : DiffUtil.ItemCallback<Picture_Room>(){
        override fun areItemsTheSame(oldItem: Picture_Room, newItem: Picture_Room): Boolean {
            return oldItem.ID == newItem.ID
        }

        override fun areContentsTheSame(oldItem: Picture_Room, newItem: Picture_Room): Boolean {
            return oldItem == newItem
        }

    }
    class ViewHolder(iteView: View): RecyclerView.ViewHolder(iteView){
        private val imageView = iteView.findViewById<ImageView>(R.id.imageView)

        fun bindData(item: Picture_Room){
            Glide.with(itemView)
                .load(item.url)
                .into(imageView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.image_item_layout,parent,false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageItem = getItem(position)
        holder.bindData(imageItem)
    }
}