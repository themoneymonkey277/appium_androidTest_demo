package com.example.angodafake.db

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Hotel(
    var ID: String? = null,
    var ID_Owner: String? = null,
    var name: String? = null,
    var phoneNumber: String? = null,
    var locationDetail: String? = null,
    var city: String? = null,
    var description: String? = null,
    var conveniences: String? = null,
    var highlight: String? = null,
    var star: Int? = 0,
    var point: Double? = 0.0,
    var profit: Double? = 0.0,
    var checkIn: String? = null,
    var checkOut: String? = null,
    var merchantCode: String? = null,
    var longitude : Double? = null,
    var latitude : Double? = null,
    var money: Int? = 0,
    var money_rating: Double? = 0.0,
    var location: Double? = 0.0,
    var clean: Double? =  0.0,
    var service: Double? = 0.0,
    var convenience: Double? = 0.0,
    var total_comments: Int = 0,
    ) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "ID" to ID,
            "ID_Owner" to ID_Owner,
            "name" to name,
            "phoneNumber" to phoneNumber,
            "locationDetail" to locationDetail,
            "city" to city,
            "description" to description,
            "conveniences" to conveniences,
            "highlight" to highlight,
            "star" to star,
            "point" to point,
            "profit" to profit,
            "checkIn" to checkIn,
            "checkOut" to checkOut,
            "money" to money,
            "money_rating" to money_rating,
            "location" to location,
            "clean" to clean,
            "service" to service,
            "convenience" to convenience,
            "total_comments" to total_comments,
        )
    }
}