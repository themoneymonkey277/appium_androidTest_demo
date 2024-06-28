package com.example.angodafake.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.R
import com.example.angodafake.Utilities.HotelUtils
import com.example.angodafake.Utilities.PictureUtils
import com.example.angodafake.Utilities.UserUtils
import com.example.angodafake.db.Chat_Room
import com.squareup.picasso.Picasso

class ChatAdapter(private val context: Context, private var chat: MutableList<Chat_Room>, private var temp: String) : RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {
    var onItemClick: ((Chat_Room) -> Unit)? = null
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(chat: Chat_Room)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val chatItem = LayoutInflater.from(parent.context).inflate(R.layout.custom_chat_adapter, parent, false)
        return MyViewHolder(chatItem)
    }

    override fun getItemCount(): Int {
        return chat.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = chat[position]

        if (temp == "seeClient") {
            holder.name.text = currentItem.Name_User

            if (currentItem.ID_User == currentItem.Last_Message_Sender) {
                if (currentItem.Last_Message!!.length < 40) {
                    holder.lastChat.text = currentItem.Last_Message
                } else {
                    holder.lastChat.text = "${currentItem.Last_Message!!.subSequence(0, 35)}..."
                }
            } else {
                if (currentItem.Last_Message!!.length < 40) {
                    holder.lastChat.text = "Bạn: ${currentItem.Last_Message}"
                } else {
                    holder.lastChat.text = "Bạn: ${currentItem.Last_Message!!.subSequence(0, 30)}..."
                }
            }
        } else {
            holder.name.text = currentItem.Name_Partner

            if (currentItem.ID_Partner == currentItem.Last_Message_Sender) {
                if (currentItem.Last_Message!!.length < 40) {
                    holder.lastChat.text = currentItem.Last_Message
                } else {
                    holder.lastChat.text = "${currentItem.Last_Message!!.subSequence(0, 35)}..."
                }
            } else {
                if (currentItem.Last_Message!!.length < 40) {
                    holder.lastChat.text = "Bạn: ${currentItem.Last_Message}"
                } else {
                    holder.lastChat.text = "Bạn: ${currentItem.Last_Message!!.subSequence(0, 30)}..."
                }
            }
        }

        holder.lastTime.text = currentItem.Last_Message_Time

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentItem)
        }
    }

    class MyViewHolder(chatItem: View) : RecyclerView.ViewHolder(chatItem) {
        val name: TextView = chatItem.findViewById(R.id.textView)
        val lastChat: TextView = chatItem.findViewById(R.id.textView1)
        val lastTime: TextView = chatItem.findViewById(R.id.textView2)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Chat_Room>) {
        chat = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: ChatAdapter.OnItemClickListener) {
        this.listener = listener
    }
}