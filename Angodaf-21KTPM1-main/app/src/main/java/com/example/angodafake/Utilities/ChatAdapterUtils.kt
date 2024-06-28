package com.example.angodafake.Utilities

import android.util.Log
import com.example.angodafake.db.Chat_Room
import com.example.angodafake.db.Purchase
import com.example.angodafake.db.Voucher
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

object ChatAdapterUtils {
    private lateinit var database: DatabaseReference

    init {
        database = Firebase.database.reference
    }

    fun getChatRoomByUserID(userID: String, listener: (MutableList<Chat_Room>) -> Unit) {
        val chatroomsQuery = database.child("chat_rooms")

        chatroomsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val chatroomsList = mutableListOf<Chat_Room>()
                for (chatroomSnapshot in dataSnapshot.children) {
                    val chatroom = chatroomSnapshot.getValue(Chat_Room::class.java)
                    if (chatroom?.ID_User == userID) {
                        chatroom.ID = chatroomSnapshot.key
                        chatroom.let { chatroomsList.add(it) }
                    }
                }
                listener(chatroomsList.asReversed())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getChatRoomByPartnerID(partnerID: String, listener: (MutableList<Chat_Room>) -> Unit) {
        val chatroomsQuery = database.child("chat_rooms")

        chatroomsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val chatroomsList = mutableListOf<Chat_Room>()
                for (chatroomSnapshot in dataSnapshot.children) {
                    val chatroom = chatroomSnapshot.getValue(Chat_Room::class.java)
                    if (chatroom?.ID_Partner == partnerID) {
                        chatroom.ID = chatroomSnapshot.key
                        chatroom.let { chatroomsList.add(it) }
                    }
                }
                listener(chatroomsList.asReversed())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getChatRoomByUserIDAndPartnerID(userID: String, partnerID: String, listener: (Chat_Room?) -> Unit) {
        val chatroomsQuery = database.child("chat_rooms")

        chatroomsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (chatroomSnapshot in dataSnapshot.children) {
                    val chatroom = chatroomSnapshot.getValue(Chat_Room::class.java)
                    if (chatroom?.ID_User == userID && chatroom.ID_Partner == partnerID) {
                        chatroom.ID = chatroomSnapshot.key
                        listener(chatroom)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}