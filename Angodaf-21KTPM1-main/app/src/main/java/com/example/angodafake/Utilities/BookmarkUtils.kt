package com.example.angodafake.Utilities

import android.util.Log
import com.example.angodafake.db.Bookmark
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

object BookmarkUtils {
    private lateinit var database: DatabaseReference

    init {
        database = Firebase.database.reference
    }

    fun getAllBookmarks(userID: String, listener: (List<Bookmark>) -> Unit){
        val bookmarksQuery = database.child("bookmarks")
        bookmarksQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Xử lý khi dữ liệu thay đổi
                val bookmarksList = mutableListOf<Bookmark>()
                for (bookmarkSnapshot in dataSnapshot.children) {
                    val bookmark = bookmarkSnapshot.getValue(Bookmark::class.java)
                    if  (bookmark?.ID_Owner == userID){
                        bookmark.ID = bookmarkSnapshot.key
                        bookmark.let { bookmarksList.add(it) }
                    }
                }
                listener(bookmarksList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        })
    }

    fun addBookmark(bookmark: Bookmark, callback: (Boolean) -> Unit) {
        val bookmarksRef = database.child("bookmarks")
        val newBookmarkRef = bookmarksRef.push()

        // Ép kiểu giá trị của ID_Hotel và ID_Owner thành Any để đảm bảo không có giá trị null
        val bookmarkMap = mapOf<String, Any>(
            "ID_Hotel" to (bookmark.ID_Hotel ?: ""),
            "ID_Owner" to (bookmark.ID_Owner ?: "")
        )

        newBookmarkRef.setValue(bookmarkMap)
            .addOnSuccessListener {
                // Xử lý khi thêm thành công
                callback(true)
            }
            .addOnFailureListener { error ->
                // Xử lý khi có lỗi xảy ra
                Log.e("BookmarkUtils", "Failed to add bookmark: ${error.message}")
                callback(false)
            }
    }

    fun deleteBookmark(bookmark: Bookmark){
        val bookmarksRef = database.child("bookmarks").child(bookmark.ID!!)
        bookmarksRef.removeValue()
            .addOnSuccessListener {
                // Xử lý khi xóa thành công
            }
            .addOnFailureListener { error ->
                // Xử lý khi có lỗi xảy ra
            }
    }

    fun deleteBookmarkWithID(idUser: String, id_Hotel: String){
        // Thực hiện truy vấn vào cơ sở dữ liệu Firebase để tìm bookmark cần xóa
        val bookmarksRef = Firebase.database.reference.child("bookmarks")

        // Thêm một lắng nghe sự kiện để lấy dữ liệu từ Firebase
        bookmarksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Duyệt qua tất cả các bookmark trong dataSnapshot
                for (bookmarkSnapshot in dataSnapshot.children) {
                    // Lấy giá trị của ID_Owner và ID_Hotel từ bookmark
                    val idOwner = bookmarkSnapshot.child("ID_Owner").getValue(String::class.java)
                    val idHotel = bookmarkSnapshot.child("ID_Hotel").getValue(String::class.java)

                    // Kiểm tra xem ID_Owner và ID_Hotel có khớp với tham số đầu vào không
                    if (idOwner == idUser && idHotel == id_Hotel) {
                        // Nếu khớp, xóa bookmark
                        bookmarkSnapshot.ref.removeValue()
                            .addOnSuccessListener {
                                // Xử lý khi xóa thành công
                                println("Bookmark deleted successfully.")
                            }
                            .addOnFailureListener { error ->
                                // Xử lý khi xảy ra lỗi trong quá trình xóa
                                println("Failed to delete bookmark: ${error.message}")
                            }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình truy vấn cơ sở dữ liệu Firebase
                println("Failed to query bookmarks: ${databaseError.message}")
            }
        })
    }
}