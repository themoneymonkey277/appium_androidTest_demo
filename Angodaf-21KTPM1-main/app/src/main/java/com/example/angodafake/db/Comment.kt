package com.example.angodafake.db

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Comment(
    var ID: String? = null,
    var ID_Owner: String? = null,
    var ID_Hotel: String? = null,
    var ID_Purchase: String? = null,
    var time: String? = null,
    var point: Double? = 0.0,
    var content: String? = null,
    var money: Int? = 0,
    var location: Int? = 0,
    var clean: Int? =  0,
    var service: Int? = 0,
    var convenience: Int? = 0,
    var type_customer: String? = null,
    var title: String? = null,
    ) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "ID" to ID,
            "ID_Owner" to ID_Owner,
            "ID_Hotel" to ID_Hotel,
            "ID_Purchase" to ID_Purchase,
            "time" to time,
            "point" to point,
            "content" to content,
            "money" to money,
            "location" to location,
            "clean" to clean,
            "service" to service,
            "convenience" to convenience,
            "type_customer" to type_customer,
            "title" to title,
        )
    }
}