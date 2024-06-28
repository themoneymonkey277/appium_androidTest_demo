package com.example.angodafake.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.R
import com.example.angodafake.db.Chat_Message
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class ChatMessageAdapter(options: FirebaseRecyclerOptions<Chat_Message>, private val context: Context, private val user_ID: String) : FirebaseRecyclerAdapter<Chat_Message, ChatMessageAdapter.ChatModelViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatModelViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.custom_chat_message_adapter, parent, false)
        return ChatModelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatModelViewHolder, position: Int, model: Chat_Message) {
        if (model.ID_Sender == user_ID) {
            holder.leftChatLayout.visibility = View.GONE
            holder.rightChatLayout.visibility = View.VISIBLE

            holder.rightChatTextview.text = model.Message
            holder.rightChatTime.text = model.Time_String
        } else {
            holder.rightChatLayout.visibility = View.GONE
            holder.leftChatLayout.visibility = View.VISIBLE

            holder.leftChatTextview.text = model.Message
            holder.leftChatTime.text = model.Time_String
        }
    }

    inner class ChatModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var leftChatLayout: LinearLayout = itemView.findViewById(R.id.left_chat_layout)
        var rightChatLayout: LinearLayout = itemView.findViewById(R.id.right_chat_layout)

        var leftChatTextview: TextView = itemView.findViewById(R.id.left_chat_textview)
        var rightChatTextview: TextView = itemView.findViewById(R.id.right_chat_textview)

        var leftChatTime: TextView = itemView.findViewById(R.id.left_chat_time)
        var rightChatTime: TextView = itemView.findViewById(R.id.right_chat_time)
    }
}