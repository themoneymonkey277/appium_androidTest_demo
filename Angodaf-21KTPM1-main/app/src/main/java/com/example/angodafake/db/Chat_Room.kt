package com.example.angodafake.db

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Chat_Room (
    var ID: String? = null,
    var ID_User: String? = null,
    var ID_Partner: String? = null,
    var Name_User: String? = null,
    var Name_Partner: String? = null,
    var Last_Message_Sender: String? = null,
    var Last_Message_Time: String? = null,
    var Last_Message: String? = null,
    var Time_Milestone: Long? = 0,
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "ID" to ID,
            "ID_User" to ID_User,
            "ID_Partner" to ID_Partner,
            "Name_User" to Name_User,
            "Name_Partner" to Name_Partner,
            "Last_Message_Sender" to Last_Message_Sender,
            "Last_Message_Time" to Last_Message_Time,
            "Last_Message" to Last_Message,
            "Time_Milestone" to Time_Milestone,
        )
    }
}