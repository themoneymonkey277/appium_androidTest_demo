package com.example.angodafake.Utilities

import android.content.ContentValues.TAG
import android.util.Log
import com.example.angodafake.db.Voucher
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import javax.security.auth.callback.Callback

object VoucherUtils {
    private lateinit var database: DatabaseReference

    init {
        database = Firebase.database.reference
    }

    fun getAllVouchers(ownerID: String, listener: (MutableList<Voucher>) -> Unit) {
        val vouchersQuery = database.child("vouchers")

        vouchersQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val vouchersList = mutableListOf<Voucher>()
                for (voucherSnapshot in dataSnapshot.children) {
                    val voucher = voucherSnapshot.getValue(Voucher::class.java)
                    if (voucher?.ID_Hotel == ownerID) {
                        voucher.ID = voucherSnapshot.key
                        voucher.let { vouchersList.add(it) }
                    }
                }
                listener(vouchersList.asReversed())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun minusVoucher(voucherID: String, quantity: Int, callback: (String) -> Unit) {
        if (quantity > 0) {
            val voucherUpdate = hashMapOf<String, Any>(
                "/vouchers/$voucherID/quantity" to (quantity - 1)
            )

            database.updateChildren(voucherUpdate)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback.invoke("success")
                    } else {
                        callback.invoke("failure")
                    }
                }
        } else {
            callback.invoke("failure")
        }
    }

    fun addVoucher(ID_Hotel: String, max_discount: Double, limit_price: Double, percentage: Int, money_discount: Double, quantity: Int, callback: (String) -> Unit) {
        val key = database.child("vouchers").push().key
        if (key == null) {
            Log.w(TAG, "Couldn't get push key for vouchers")
            return
        }

        val newVoucher = hashMapOf<String, Any>(
            "/vouchers/$key/ID_Hotel" to ID_Hotel,
            "/vouchers/$key/max_discount" to max_discount,
            "/vouchers/$key/limit_price" to limit_price,
            "/vouchers/$key/percentage" to percentage,
            "/vouchers/$key/money_discount" to money_discount,
            "/vouchers/$key/quantity" to quantity,
        )

        database.updateChildren(newVoucher)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback.invoke("success")
                } else {
                    callback.invoke("failure")
                }
            }
    }

    fun editVoucher(ID: String, ID_Hotel: String, max_discount: Double, limit_price: Double, percentage: Int, money_discount: Double, quantity: Int, callback: (String) -> Unit) {
        val editVoucher = hashMapOf<String, Any>(
            "/vouchers/$ID/ID_Hotel" to ID_Hotel,
            "/vouchers/$ID/max_discount" to max_discount,
            "/vouchers/$ID/limit_price" to limit_price,
            "/vouchers/$ID/percentage" to percentage,
            "/vouchers/$ID/money_discount" to money_discount,
            "/vouchers/$ID/quantity" to quantity,
        )

        database.updateChildren(editVoucher)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback.invoke("success")
                } else {
                    callback.invoke("failure")
                }
            }
    }
}