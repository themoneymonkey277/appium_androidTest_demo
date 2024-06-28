package com.example.angodafake.db

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Chat_Message (
    var ID_Sender: String? = null,
    var Message: String? = null,
    var Time: Long? = 0,
    var Time_String: String? = null,
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "ID_Sender" to ID_Sender,
            "Message" to Message,
            "Time" to Time,
            "Time_String" to Time_String,
        )
    }
}