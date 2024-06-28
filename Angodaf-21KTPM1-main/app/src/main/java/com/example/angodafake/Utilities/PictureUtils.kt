package com.example.angodafake.Utilities

import android.util.Log
import android.widget.Toast
import com.example.angodafake.db.Picture_Hotel
import com.example.angodafake.db.Picture_Room
import com.example.angodafake.db.Rooms
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.storage

object PictureUtils {
    private lateinit var database: DatabaseReference

    init {
        database = Firebase.database.reference
    }

    fun addHotelPictures(ID_Hotel: String, pics: ArrayList<String>){
        val pictureQuery = database.child("hotel_pictures").child(ID_Hotel)
        for (pic in pics){
            val key = pictureQuery.push().key
            if (key != null){
                val picture = Picture_Hotel(null, null, pic)
                pictureQuery.child(key).setValue(picture)
            } else{
                Log.e("firebase", "Counldn't get push key for hotel_pictures")
            }

        }
    }

    fun updateHotelPictures(ID_Hotel: String, pics: ArrayList<String>){
        val pictureQuery = database.child("hotel_pictures").child(ID_Hotel)
        pictureQuery.setValue(null)
        for (pic in pics){
            val key = pictureQuery.push().key
            if (key != null){
                val picture = Picture_Hotel(null, null, pic)
                pictureQuery.child(key).setValue(picture)
            } else{
                Log.e("firebase", "Counldn't get push key for hotel_pictures")
            }
        }
    }

    fun updateRoomPictures(ID_Hotel: String, ID_Room: String, pics: ArrayList<String>){
        val pictureQuery = database.child("room_pictures").child(ID_Hotel).child(ID_Room)
        pictureQuery.setValue(null)
        for (pic in pics){
            val key = pictureQuery.push().key
            if (key != null){
                val picture = Picture_Room(null, null, null, pic)
                pictureQuery.child(key).setValue(picture)
            } else{
                Log.e("firebase", "Counldn't get push key for room_pictures")
            }
        }
    }

    fun addRoomPictures(ID_Hotel: String, ID_Room: String, pics: ArrayList<String>){
        val pictureQuery = database.child("room_pictures").child(ID_Hotel).child(ID_Room)
        for (pic in pics){
            val key = pictureQuery.push().key
            if (key != null){
                val room_pic = Picture_Room(null, null, null, pic)
                pictureQuery.child(key).setValue(room_pic)
            } else{
                Log.e("firebase", "Counldn't get push key for room_pictures")
            }

        }
    }

    fun getPictureByHotelID(ID: String, listener: (Picture_Hotel) -> Unit){
        val pictureQuery = database.child("hotel_pictures").child(ID)
        pictureQuery.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val pictureHotelList = mutableListOf<Picture_Hotel>()
                for (pictureSnapshot in snapshot.children) {
                    val pictureHotel = pictureSnapshot.getValue(Picture_Hotel::class.java)
                    pictureHotel?.ID = pictureSnapshot.key
                    pictureHotel?.ID_Hotel = ID
                    pictureHotel?.let { pictureHotelList.add(it) }
                }
                if (!pictureHotelList.isEmpty()){
                    listener(pictureHotelList[0])
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        })
    }

    fun getPictureRoom(ID_Hotel: String, roomID: String, listener: (Picture_Room) -> Unit) {
        val roomPicture = mutableListOf<Picture_Room>()
        database.child("room_pictures").child(ID_Hotel).child(roomID).get().addOnSuccessListener { dataSnapshot ->
            for (roomSnapshot in dataSnapshot.children) {
                val picture = roomSnapshot.getValue(Picture_Room::class.java)
                picture?.let {
                    roomPicture.add(it)
                }
            }
            if (roomPicture.isNotEmpty()) {
                listener(roomPicture[0])
            }
        }.addOnFailureListener { exception ->
            Log.e("firebase", "Error getting picture by ID", exception)
        }
    }

    fun getRoomPictureByID(ID_Hotel: String, roomID: String, listener: (Picture_Room?) -> Unit) {
        database.child("room_pictures").child(ID_Hotel).child(roomID)
            .get()
            .addOnSuccessListener { dataSnapshot ->
                val roomPicture = dataSnapshot.children.firstOrNull { roomSnapshot ->
                    val picture = roomSnapshot.getValue(Picture_Room::class.java)
                    picture?.let {
                        it.ID = roomSnapshot.key
                        it.ID_Hotel = ID_Hotel
                        it.ID_Room = roomID
                        true
                    } ?: false
                }?.getValue(Picture_Room::class.java)

                listener(roomPicture)
            }
            .addOnFailureListener { exception ->
                Log.e("firebase", "Error getting picture by ID", exception)
                listener(null)
            }
    }


    fun getPictureList(listener: (List<Picture_Hotel>) -> Unit) {
        val pictureList = mutableListOf<Picture_Hotel>()
        PictureUtils.database.child("pictures").get().addOnSuccessListener { dataSnapshot ->
            for (pictureSnapshot in dataSnapshot.children) {
                val picture = pictureSnapshot.getValue(Picture_Hotel::class.java)
                picture?.let {
                    pictureList.add(it)
                }
            }
            listener(pictureList)
        }.addOnFailureListener { exception ->
            Log.e("firebase", "Error getting hotel list", exception)
            listener(emptyList()) // Trả về danh sách rỗng nếu có lỗi xảy ra
        }
    }

    fun getPicturesByHotelID(ID: String, listener: (List<Picture_Hotel>) -> Unit){
        val hotelPicture = mutableListOf<Picture_Hotel>()
        database.child("hotel_pictures").child(ID).get().
        addOnSuccessListener { dataSnapshot ->
            for (hotelPictureSnapshot in dataSnapshot.children) {
                val hotelPic = hotelPictureSnapshot.getValue(Picture_Hotel::class.java)
                hotelPic?.let {
                    it.ID = hotelPictureSnapshot.key
                    it.ID_Hotel = ID
                    hotelPicture.add(it)
                }
            }
            listener(hotelPicture)
        }.addOnFailureListener { exception ->
            Log.e("firebase", "Error getting picture list", exception)
            listener(emptyList()) // Trả về danh sách rỗng nếu có lỗi xảy ra
        }
    }

    fun getPicturesRoomByID(ID_Hotel: String, roomID: String, listener: (List<Picture_Room>) -> Unit) {
        val roomPicture = mutableListOf<Picture_Room>()
        database.child("room_pictures").child(ID_Hotel).child(roomID).get().addOnSuccessListener { dataSnapshot ->
            for (roomSnapshot in dataSnapshot.children) {
                val picture = roomSnapshot.getValue(Picture_Room::class.java)
                picture?.let {
                    it.ID = roomSnapshot.key
                    it.ID_Hotel = ID_Hotel
                    it.ID_Room = roomID
                    roomPicture.add(it)
                }
            }
            listener(roomPicture)
        }.addOnFailureListener { exception ->
            Log.e("firebase", "Error getting picture by ID", exception)
            listener(emptyList())
        }
    }

    fun deleteRoomPictues(ID_Hotel: String, roomID: String){
        //xóa ảnh trên firebase storage
        database.child("room_pictures").child(ID_Hotel).child(roomID).get().addOnSuccessListener { dataSnapshot ->
            for (roomSnapshot in dataSnapshot.children) {
                val picture = roomSnapshot.getValue(Picture_Room::class.java)
                picture?.let {
                    Firebase.storage.reference.child(picture.url!!.substring(picture.url.lastIndexOf('/') + 1, picture.url.indexOf('?'))).delete()
                }
            }

        }.addOnFailureListener { exception ->
            Log.e("firebase", "Error getting picture by ID", exception)
        }
        //xóa trên firebase realtime
        database.child("room_pictures").child(ID_Hotel).child(roomID).setValue(null)
    }
}