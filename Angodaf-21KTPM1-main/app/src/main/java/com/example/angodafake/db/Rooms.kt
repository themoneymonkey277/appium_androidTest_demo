package com.example.angodafake.db

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Rooms(
    var ID: String? = null,
    var ID_Hotel: String? = null,
    var quantity: Int? = 0,//
    var available: Int? = 0, //số lượng phòng đã đặt
    var type: String? = null,//
    var acreage: Double? = 0.0,//
    var direction: String? = null,//
    var benefit: String? = null,//
    var price: Int? = 0,//
    var single_bed: Int? = 0,//
    var double_bed: Int? = 0,//
    var capacity: Int? = 0, //so nguoi
    ) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}

//@Query("Select * from room_db")
//fun getRoomList() : List<Rooms>
//@Query("SELECT * FROM room_db WHERE ID_Hotel = :hotel_id")
//fun getRoomsByHotelID(hotel_id: Int): List<Rooms>

