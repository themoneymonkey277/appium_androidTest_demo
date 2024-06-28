package com.example.angodafake.db

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Purchase(
    var ID: String? = null,
    var ID_Owner: String? = null,
    var ID_Hotel: String? = null,
    var ID_Room: String? = null,
    var quantity: Int? = 0,
    var method: String? = null,
    var time_booking: String? = null,
    var time_purchase: String? = null,
    var time_cancel: String? = null,
    var reason: String? = null,
    var total_purchase: Double? = 0.0,
    var status_purchase: String? = null,
    var detail: String? = null,
    var date_come: String? = null,
    var date_go: String? = null
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "ID" to ID,
            "ID_Owner" to ID_Owner,
            "ID_Hotel" to ID_Hotel,
            "ID_Room" to ID_Room,
            "quantity" to quantity,
            "method" to method,
            "time_booking" to time_booking,
            "time_purchase" to time_purchase,
            "time_cancel" to time_cancel,
            "reason" to reason,
            "total_purchase" to total_purchase,
            "status_purchase" to status_purchase,
            "detail" to detail,
            "date_come" to date_come,
            "date_go" to date_go,
        )
    }
}

