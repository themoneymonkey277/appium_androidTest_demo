package com.example.angodafake.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.Bookmark
import com.example.angodafake.R
import com.example.angodafake.Utilities.BookmarkUtils
import com.example.angodafake.Utilities.HotelUtils
import com.example.angodafake.Utilities.PictureUtils
import com.example.angodafake.db.Hotel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.core.utilities.Utilities
import com.squareup.picasso.Picasso

class BookmarkAdapter(private val rootView: View, private val context: Context, private var bookmarks: List<com.example.angodafake.db.Bookmark>) : RecyclerView.Adapter<BookmarkAdapter.MyViewHolder>() {
    private var listener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(bookmark: Bookmark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val bookmarkItem = LayoutInflater.from(parent.context).inflate(R.layout.custom_bookmark, parent, false)
        return MyViewHolder(bookmarkItem)
    }

    override fun getItemCount(): Int {
        return bookmarks.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = bookmarks[position]

        HotelUtils.getHotelByID(currentItem.ID_Hotel!!){ HotelMarked ->
            PictureUtils.getPictureByHotelID(HotelMarked.ID!!){
                Picasso.get().load(it.url).into(holder.img)
            }
            holder.hotelName.text = HotelMarked.name
            holder.buttonFav.setOnClickListener {
                val bookmarks_tmp = bookmarks.toMutableList()
                //xoá khỏi firebase
                BookmarkUtils.deleteBookmark(bookmarks_tmp[position])
                bookmarks_tmp.removeAt(position)
                updateList(bookmarks_tmp)
                showSuccessSnackBar("Đã xoá khách sạn")
            }
            holder.location.text = HotelMarked.city
            holder.rateStatus.text = when (HotelMarked.point!!.toInt()){
                in 0 until 3 -> { "Cực tệ" }
                in 3 until 5 -> { "Tệ" }
                in 5 until 6 -> { "Trung bình" }
                in 6 until 8 -> { "Tốt" }
                in 8 until 9 -> { "Rất tốt" }
                else -> { "Tuyệt vời" }
            }
            holder.Point.text = HotelMarked.point.toString()
        }
    }

    class MyViewHolder(bookmarkItem: View) : RecyclerView.ViewHolder(bookmarkItem) {
        val img: ImageView = bookmarkItem.findViewById(R.id.imageView)
        val hotelName: TextView = bookmarkItem.findViewById(R.id.hotelName)
        val buttonFav: Button = bookmarkItem.findViewById(R.id.fav)
        val location: TextView = bookmarkItem.findViewById(R.id.Location)
        val rateStatus: TextView = bookmarkItem.findViewById(R.id.rateStatus)
        val Point: TextView = bookmarkItem.findViewById(R.id.point)
        val quaCM: TextView = bookmarkItem.findViewById(R.id.quaCM)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<com.example.angodafake.db.Bookmark>) {
        bookmarks = newList
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    private fun showSuccessSnackBar(msg: String) {
        val snackbar = Snackbar.make(rootView, msg, Snackbar.LENGTH_LONG)
        snackbar.view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3193FF"))
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }
}