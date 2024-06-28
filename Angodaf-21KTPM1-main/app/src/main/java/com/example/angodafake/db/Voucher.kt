package com.example.angodafake.db

import kotlin.math.max

data class Voucher(
    var ID: String? = null,
    var ID_Hotel: String? = null,
    var max_discount: Double? = 0.0,
    var limit_price: Double? = 0.0,
    var percentage: Int? = 0,
    var money_discount: Double? = 0.0,
    var quantity: Int? = 0,
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "ID" to ID,
            "ID_Hotel" to ID_Hotel,
            "max_discount" to max_discount,
            "limit_price" to limit_price,
            "percentage" to percentage,
            "money_discount" to money_discount,
            "quantity" to quantity,
        )
    }
}
