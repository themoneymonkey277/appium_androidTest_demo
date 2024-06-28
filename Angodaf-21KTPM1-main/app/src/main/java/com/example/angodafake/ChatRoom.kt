package com.example.angodafake

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.example.angodafake.Adapter.ChatMessageAdapter
import com.example.angodafake.Utilities.ChatUtils
import com.example.angodafake.Utilities.HotelUtils
import com.example.angodafake.Utilities.UserUtils
import com.example.angodafake.db.Chat_Message
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.Query


class ChatRoom : AppCompatActivity() {
    private lateinit var ID_ChatRoom: String
    private lateinit var ID_User: String
    private lateinit var ID_Partner: String
    private lateinit var Type_User: String

    private lateinit var btnBack: ImageButton
    private lateinit var namePartner: TextView
    private lateinit var chatField: RecyclerView
    private lateinit var chatInput: EditText
    private lateinit var btnSend: ImageButton

    private lateinit var adapter: ChatMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_chat_room)

        namePartner = findViewById(R.id.textView)
        chatField = findViewById(R.id.chatField)
        chatInput = findViewById(R.id.chatInput)

        btnBack = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        btnSend = findViewById(R.id.btn_send)
        btnSend.setOnClickListener {
            val message: String = chatInput.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessageToPartner(message)
            }
        }
        
        ID_User = intent.getStringExtra("ID_User").toString()
        ID_Partner = intent.getStringExtra("ID_Partner").toString()
        Type_User = intent.getStringExtra("Type_User").toString()
        namePartner.text = intent.getStringExtra("Name_User")

        ID_ChatRoom = getID_ChatRoom(ID_User, ID_Partner)

        getOrCreateChatRoom()
        setupChatRecyclerView()
    }

    private fun getID_ChatRoom(userId1: String, userId2: String): String {
        return if (userId1.hashCode() < userId2.hashCode()) {
            userId1 + "_" + userId2
        } else {
            userId2 + "_" + userId1
        }
    }

    private fun setupChatRecyclerView() {
        val query: Query = ChatUtils.getChatroomMessageReference(ID_ChatRoom).orderByChild("time")
        val options: FirebaseRecyclerOptions<Chat_Message> = FirebaseRecyclerOptions.Builder<Chat_Message>()
            .setQuery(query, Chat_Message::class.java).build()

        adapter = ChatMessageAdapter(options, this, ID_User)
        val manager = LinearLayoutManager(this)
        manager.reverseLayout = true
        chatField.layoutManager = manager
        chatField.adapter = adapter
        adapter.startListening()
        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                chatField.smoothScrollToPosition(0)
            }
        })
    }

    private fun sendMessageToPartner(message: String) {
        ChatUtils.sendChatMessage(ID_ChatRoom, ID_User, message) { result ->
            chatInput.setText("");
        }
    }

    private fun getOrCreateChatRoom() {
        if (Type_User == "User") {
            UserUtils.getUserByID(ID_User) {user ->
                HotelUtils.getHotelByID(ID_Partner) {hotel ->
                    ChatUtils.getOrCreateChatRoom(ID_ChatRoom, ID_User, ID_Partner, user!!.name!!, hotel.name!!) {id_chatroom ->
                        ID_ChatRoom = id_chatroom
                    }
                }
            }
        } else {
            UserUtils.getUserByID(ID_Partner) {user ->
                HotelUtils.getHotelByID(ID_User) {hotel ->
                    ChatUtils.getOrCreateChatRoom(ID_ChatRoom, ID_User, ID_Partner, hotel.name!!, user!!.name!!) {id_chatroom ->
                        ID_ChatRoom = id_chatroom
                    }
                }
            }
        }
    }
}