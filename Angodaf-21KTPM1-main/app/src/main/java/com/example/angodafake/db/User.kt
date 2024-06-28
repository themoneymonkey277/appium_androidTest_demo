package com.example.angodafake.db

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var ID: String? = null,
    val name: String? = null,
    val dob: String? = null,
    val gender: String? = null,
    val phoneN: String? = null,
    val email: String? = null,
    val country: String? = null,
    val cardNumber: String? = null,
    val cardName: String? = null,
    val point: Int? = 0,
    val password: String? = null,
    val picture_onwer: String? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}


