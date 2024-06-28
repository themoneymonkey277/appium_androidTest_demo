package com.example.angodafake.db

data class Picture_Room(
    var ID: String? = null,
    var ID_Hotel: String? = null,
    var ID_Room: String? = null,
    val url : String? = null,) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}
