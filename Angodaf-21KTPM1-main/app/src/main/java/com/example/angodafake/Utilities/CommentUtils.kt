package com.example.angodafake.Utilities

import android.content.ContentValues
import android.health.connect.datatypes.ExerciseRoute.Location
import android.util.Log
import com.example.angodafake.db.Comment
import com.example.angodafake.db.Purchase
import com.example.angodafake.db.User
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

object CommentUtils {
    private lateinit var database: DatabaseReference
     init {
         database = Firebase.database.reference
     }

    fun getCommentsByIDHotel(ownerID: String, listenser: (MutableList<Comment>) -> Unit) {
        val commentsQuery = database.child("comments")

        commentsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val commentsList = mutableListOf<Comment>()
                for (commentSnapshot in dataSnapshot.children) {
                    val comment = commentSnapshot.getValue(Comment::class.java)
                    if (comment?.ID_Hotel == ownerID) {
                        comment.ID = commentSnapshot.key
                        comment.let { commentsList.add(it) }
                    }
                }
                listenser(commentsList.asReversed())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getCommentsByIDOwner(ownerID: String, listenser: (MutableList<Comment>) -> Unit) {
        val commentsQuery = database.child("comments")

        commentsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val commentsList = mutableListOf<Comment>()
                for (commentSnapshot in dataSnapshot.children) {
                    val comment = commentSnapshot.getValue(Comment::class.java)
                    if (comment?.ID_Owner == ownerID) {
                        comment.ID = commentSnapshot.key
                        comment.let { commentsList.add(it) }
                    }
                }
                listenser(commentsList.asReversed())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    fun getCommentByIDPurchase(ownerID: String, listenser: (MutableList<Comment>) -> Unit) {
        val commentsQuery = database.child("comments")

        commentsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val commentsList = mutableListOf<Comment>()
                for (commentSnapshot in dataSnapshot.children) {
                    val comment = commentSnapshot.getValue(Comment::class.java)
                    if (comment?.ID_Purchase == ownerID) {
                        comment.ID = commentSnapshot.key
                        comment.let { commentsList.add(it) }
                    }
                }
                listenser(commentsList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun addComment(ID_Owner: String, ID_Hotel: String, ID_Purchase: String, time: String, point: Double, content: String, money: Int, location: Int, clean: Int, service: Int, convenience: Int, type_customer: String, title: String, callback: (String) -> Unit) {
        val key = database.child("comments").push().key
        if (key == null) {
            Log.w(ContentValues.TAG, "Couldn't get push key for comments")
            return
        }

        val newComment = hashMapOf<String, Any>(
            "/comments/$key/ID_Owner" to ID_Owner,
            "/comments/$key/ID_Hotel" to ID_Hotel,
            "/comments/$key/ID_Purchase" to ID_Purchase,
            "/comments/$key/time" to time,
            "/comments/$key/point" to point,
            "/comments/$key/content" to content,
            "/comments/$key/money" to money,
            "/comments/$key/location" to location,
            "/comments/$key/clean" to clean,
            "/comments/$key/service" to service,
            "/comments/$key/convenience" to convenience,
            "/comments/$key/type_customer" to type_customer,
            "/comments/$key/title" to title,
        )

        HotelUtils.getHotelByID(ID_Hotel) {hotel ->
            val hotelMoneyRating = (hotel.money_rating!! + money) / 2
            val hotelLocation = (hotel.location!! + location) / 2
            val hotelClean = (hotel.clean!! + clean) / 2
            val hotelService = (hotel.service!! + service) / 2
            val hotelConvenience = (hotel.convenience!! + convenience) / 2
            val hotelPoint = (((hotelMoneyRating + hotelLocation + hotelClean + hotelService + hotelConvenience) / 5) + point) / 2

            HotelUtils.updateRating(ID_Hotel, hotelPoint, hotelMoneyRating, hotelLocation, hotelClean, hotelService, hotelConvenience) {result ->

            }
        }

        database.updateChildren(newComment)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback.invoke("success")
                } else {
                    callback.invoke("failure")
                }
            }
    }

    fun deleteCommentByIDHotel(ID_Hotel: String){
        val commentsQuery = database.child("comments")
        commentsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (commentSnapshot in dataSnapshot.children) {
                    val comment = commentSnapshot.getValue(Comment::class.java)
                    if (comment?.ID_Hotel == ID_Hotel) {
                        database.child("comments").child(commentSnapshot.key!!).setValue(null)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}