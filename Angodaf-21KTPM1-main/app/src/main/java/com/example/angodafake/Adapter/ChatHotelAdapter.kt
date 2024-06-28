package com.example.angodafake.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.R
import com.example.angodafake.Utilities.ChatAdapterUtils
import com.example.angodafake.db.Hotel

class ChatHotelAdapter(private val context: Context, private var hotel: MutableList<Hotel>) : RecyclerView.Adapter<ChatHotelAdapter.MyViewHolder>() {
    var onItemClick: ((Hotel) -> Unit)? = null
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(hotel: Hotel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val hotelItem = LayoutInflater.from(parent.context).inflate(R.layout.custom_chat_hotel_adapter, parent, false)
        return MyViewHolder(hotelItem)
    }

    override fun getItemCount(): Int {
        return hotel.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = hotel[position]

        holder.nameHotel.text = currentItem.name

        ChatAdapterUtils.getChatRoomByPartnerID(currentItem.ID!!) {chatRooms ->
            holder.totalChat.text = chatRooms.size.toString()

            var newMessage: Int = 0

            for (chatRoom in chatRooms) {
                if (chatRoom.Last_Message_Sender != currentItem.ID) {
                    newMessage++
                }
            }
            holder.waitChat.text = newMessage.toString()
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentItem)
        }
    }

    class MyViewHolder(hotelItem: View) : RecyclerView.ViewHolder(hotelItem) {
        val nameHotel: TextView = hotelItem.findViewById(R.id.hotelName)
        val waitChat: TextView = hotelItem.findViewById(R.id.waitChat)
        val totalChat: TextView = hotelItem.findViewById(R.id.totalChat)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Hotel>) {
        hotel = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: ChatHotelAdapter.OnItemClickListener) {
        this.listener = listener
    }
}