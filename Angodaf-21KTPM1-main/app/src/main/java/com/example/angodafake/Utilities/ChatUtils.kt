package com.example.angodafake.Utilities

import com.example.angodafake.db.Chat_Message
import com.example.angodafake.db.Chat_Room
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar

object ChatUtils {
    private lateinit var database: DatabaseReference
    init {
        database = FirebaseDatabase.getInstance().reference
    }

    fun getChatroomReference(ID: String): DatabaseReference {
        return database.child("chat_rooms").child(ID)
    }

    fun getChatroomMessageReference(chatroom_ID: String): DatabaseReference {
        return getChatroomReference(chatroom_ID).child("chats")
    }

    fun getOrCreateChatRoom(chatroom_ID: String, user_ID: String, partner_ID: String, user_Name: String, partner_Name: String, callback: (String) -> Unit) {
        getChatroomReference(chatroom_ID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val chatRoom = dataSnapshot.getValue(Chat_Room::class.java)
                if (chatRoom == null) {
                    // First time chat
                    val currentDateTime = Calendar.getInstance().time
                    val formatter = SimpleDateFormat("HH:mm dd/MM")
                    val timeNow = formatter.format(currentDateTime)

                    val newChatRoom = Chat_Room(chatroom_ID, user_ID, partner_ID, user_Name, partner_Name, partner_ID, timeNow, "", System.currentTimeMillis())
                    getChatroomReference(chatroom_ID).setValue(newChatRoom)
                    sendChatMessage(chatroom_ID, partner_ID, "Cảm ơn bạn đã liên hệ với khách sạn chúng tôi! Vui lòng chia sẻ yêu cầu hoặc câu hỏi của bạn, chúng tôi sẽ phản hồi ngay sau khi có thể.") {result ->
                        callback(chatroom_ID)
                    }
                } else {
                    callback(chatroom_ID)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled event if needed
            }
        })
    }

    fun sendChatMessage(chatroom_ID: String, sender_ID: String, message: String, callback: (String) -> Unit) {
        getChatroomReference(chatroom_ID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val chatRoom = dataSnapshot.getValue(Chat_Room::class.java)

                val currentDateTime = Calendar.getInstance().time
                val formatter = SimpleDateFormat("HH:mm dd/MM")
                val timeNow = formatter.format(currentDateTime)

                val updateChatRoom = hashMapOf<String, Any>()
                updateChatRoom["last_Message_Sender"] = sender_ID
                updateChatRoom["last_Message_Time"] = timeNow
                updateChatRoom["last_Message"] = message

                val priority = chatRoom?.Time_Milestone?.minus(System.currentTimeMillis())

                getChatroomReference(chatroom_ID).updateChildren(updateChatRoom)
                val chatMessage = Chat_Message(sender_ID, message, priority, timeNow)
                val newMessageRef = getChatroomMessageReference(chatroom_ID).push()
                newMessageRef.setValue(chatMessage)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            callback.invoke("success")
                        } else {
                            callback.invoke("failure")
                        }
                    }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled event if needed
            }
        })

    }
}