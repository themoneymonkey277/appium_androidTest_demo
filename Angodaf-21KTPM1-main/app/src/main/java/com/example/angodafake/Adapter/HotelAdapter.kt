package com.example.angodafake.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.R
import com.example.angodafake.Utilities.BookmarkUtils
import com.example.angodafake.Utilities.CommentUtils
import com.example.angodafake.Utilities.PictureUtils
import com.example.angodafake.Utilities.PurchaseUtils
import com.example.angodafake.Utilities.RoomUtils
import com.example.angodafake.Utilities.VoucherUtils
import com.example.angodafake.db.Bookmark
import com.example.angodafake.db.Comment
import com.example.angodafake.db.Hotel
import com.example.angodafake.db.Picture_Hotel
import com.example.angodafake.db.Rooms
import com.example.angodafake.db.Voucher
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HotelAdapter(private val context: Context, private var hotels: List<Hotel>, private var idUser: String) : RecyclerView.Adapter<HotelAdapter.ViewHolder>() {
    private lateinit var Picture_Hotel: Picture_Hotel
    private var listener: OnItemClickListener? = null
    private lateinit var database: DatabaseReference

    // Interface cho sự kiện click
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val hotelName = listItemView.findViewById<TextView>(R.id.hotelName)
        val City = listItemView.findViewById<TextView>(R.id.City)
        val ratingBar: RatingBar = listItemView.findViewById(R.id.ratingBar)
        val pointView = listItemView.findViewById<TextView>(R.id.point)
        val img: ImageView = listItemView.findViewById(R.id.imageView)
        val rateStatus: TextView = listItemView.findViewById(R.id.rateStatus)
        val count_cmt: TextView = listItemView.findViewById(R.id.cmt)
        val buttonFav: ImageView = listItemView.findViewById(R.id.fav)
        val buttonShare: ImageView = listItemView.findViewById(R.id.shareBtn)
        val price: TextView = listItemView.findViewById(R.id.price)
        val price_new: TextView = listItemView.findViewById(R.id.priceNew)
        val firstRectangle: TextView = listItemView.findViewById(R.id.firstRectangle)


        init {
            // Thêm sự kiện click cho itemView
            itemView.setOnClickListener {
                listener?.onItemClick(adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val hotelsView = inflater.inflate(R.layout.custom_hotel, parent, false)
        // Return a new holder instance
        return ViewHolder(hotelsView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hotel : Hotel = hotels[position]

        database = Firebase.database.reference
        val hotelsRef = database.child("hotels")
        val roomsRef = database.child("rooms")

        holder.hotelName.text = hotel.name
        holder.ratingBar.rating = hotel.star!!.toFloat()
        holder.City.text = hotel.city
        holder.count_cmt.text = hotel.total_comments.toString() + " nhận xét"
        holder.rateStatus.text = when (hotel.point?.toInt()){
            in 0 until 3 -> { "Rất tệ" }
            in 3 until 5 -> { "Tệ" }
            in 5 until 6 -> { "Trung bình" }
            in 6 until 7 -> { "Hài lòng" }
            in 7 until 8 -> { "Rất tốt" }
            in 8 until 9 -> { "Tuyệt vời" }
            else -> { "Trên cả tuyệt vời" }
        }

        RoomUtils.getRoomByHotelID(hotel.ID!!){ fetchedRoomList   ->
            var roomList: List<Rooms> = emptyList()
            roomList = fetchedRoomList
            val lowestPrice = roomList.minOfOrNull { it.price ?: Int.MAX_VALUE } ?: Int.MAX_VALUE
//            holder.price.text = lowestPrice.toString() + " đ"
            Log.d("Adapter", "Room: ${lowestPrice}")

            hotelsRef.child(hotel.ID!!).child("money").setValue(lowestPrice)


            CommentUtils.getCommentsByIDHotel(hotel.ID!!) { commentList ->
                if (commentList.size != 0){
                    var totalPoint = 0.0
                    for (comment in commentList) {
                        totalPoint += comment.point!!
                    }

                    hotelsRef.child(hotel.ID!!).child("total_comments").setValue(commentList.size)
                    holder.count_cmt.text = hotel.total_comments.toString() + " nhận xét"
                    val averagePoint = totalPoint / commentList.size
                    val roundedNumber = String.format(Locale.US, "%.1f", averagePoint).toDouble()
                    hotelsRef.child(hotel.ID!!).child("point").setValue(roundedNumber)
                    holder.pointView.text = hotel.point.toString()
                }
                else{
                    hotelsRef.child(hotel.ID!!).child("total_comments").setValue(0)
                    holder.count_cmt.text = hotel.total_comments.toString() + " nhận xét"
                    hotelsRef.child(hotel.ID!!).child("point").setValue(0.0)
                    holder.pointView.text = hotel.point.toString()
                }



                BookmarkUtils.getAllBookmarks(idUser) { favList ->
                    favList.forEach { bookmark ->
                        if (bookmark.ID_Hotel == hotel.ID) {
                            Log.d("id_hotel", "$bookmark.ID_Hotel, ${hotel.toString()}")
                            holder.buttonFav.setColorFilter(Color.RED)
                            return@forEach
                        }
                    }

                    PurchaseUtils.getAllPurchasesByHotelID(hotel.ID!!) { purchaseList ->
                        var count_room_purchase = 0
                        if (purchaseList.isNotEmpty()) {
                            var id_room = ""
                            for (purchase in purchaseList) {
                                if (purchase.detail != "DA_HUY") {
                                    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                                        Date()
                                    )
                                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    val dateCome = dateFormat.parse(purchase.date_come!!)
                                    val dateGo = dateFormat.parse(purchase.date_go!!)
                                    val currentDateObject = dateFormat.parse(currentDate)
                                    Log.d("DateCome", "Date come: ${dateFormat.format(dateCome!!)}")
                                    Log.d("DateGo", "Date go: ${dateFormat.format(dateGo!!)}")
                                    Log.d("currentDate", "currentDate: ${dateFormat.format(currentDateObject!!)}")


                                    // Kiểm tra xem thời gian hiện tại có nằm trong khoảng thời gian giữa purchase.date_come và purchase.date_go không
                                    if (currentDateObject.after(dateCome) && currentDateObject.before(dateGo)) {
                                        count_room_purchase++
                                    }
                                    if (currentDateObject.compareTo(dateGo) == 0) {
                                        // Nếu hôm nay trùng với dateGo, thì count_room_purchase cũng được tăng lên
                                        count_room_purchase++
                                    }
                                }
                                id_room = purchase.ID_Room!!
                                Log.d("PurchaseList", "Purchase ID: ${purchase.ID}, Detail: ${purchase.detail}")
                            }
                            roomsRef.child(hotel.ID!!).child(id_room).child("available").setValue(count_room_purchase)

                        }else {
                            Log.d("PurchaseList", "Purchase list is null")
                        }

                        PictureUtils.getPicturesByHotelID(hotel.ID!!) { picture ->
                            if (picture.isNotEmpty()) {
                                Picasso.get().load(picture[0].url).into(holder.img)
                            }
                            else{
                                val idPicture = context.resources.getIdentifier("quang_ba_khach_san", "drawable", context.packageName)

                                holder.img.setImageResource(idPicture)
                            }

                            VoucherUtils.getAllVouchers(hotel.ID!!) { vouchers ->
                                println(hotel.ID!!)

                                val listVoucher = mutableListOf<Voucher>()

                                for (voucher in vouchers) {
                                    if (voucher.quantity!! > 0) {
                                        listVoucher.add(voucher)
                                    }
                                }
                                if (listVoucher.isNotEmpty()) {
                                    holder.firstRectangle.text = "Đã áp dụng đ " + listVoucher[0].money_discount.toString()
                                    holder.price.visibility = View.VISIBLE

                                    val priceText = lowestPrice.toString() + " đ"
                                    val spannableString = SpannableString(priceText)
                                    spannableString.setSpan(
                                        StrikethroughSpan(),
                                        0,
                                        priceText.length,
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                    holder.price.text = spannableString
                                    println(listVoucher[0].money_discount!!)
                                    holder.price_new.text = (lowestPrice.minus(listVoucher[0].money_discount!!)).toString() + " đ"

                                } else {
                                    holder.price.visibility = View.GONE
                                    holder.price_new.text = lowestPrice.toString() + " đ"
                                    holder.firstRectangle.text = "GIÁ RẺ NHẤT"

                                }
                            }
                        }
                    }


                    holder.buttonFav.setOnClickListener {
                        if (holder.buttonFav.colorFilter == null){
                            holder.buttonFav.setColorFilter(Color.RED)
                            val newBookmark = Bookmark(ID_Hotel = hotel.ID, ID_Owner = "tYw0x3oVS7gAd9wOdOszzvJMOEM2")

                            BookmarkUtils.addBookmark(newBookmark) { success ->
                                if (success) {
                                    // Bookmark đã được thêm thành công
                                    Log.d("Bookmark", "Bookmark added successfully.")
                                } else {
                                    // Có lỗi xảy ra khi thêm bookmark
                                    Log.e("Bookmark", "Failed to add bookmark.")
                                }
                            }
                        } else {
                            holder.buttonFav.setColorFilter(null)
                            BookmarkUtils.deleteBookmarkWithID("tYw0x3oVS7gAd9wOdOszzvJMOEM2", hotel.ID!!)
                        }
                    }

                    holder.buttonShare.tag = position
                    holder.buttonShare.setOnClickListener{
                        onShareButtonClick(it)
                    }
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return hotels.size
    }

    fun updateDataGradually(newData: List<Hotel>) {
        hotels = newData
        notifyDataSetChanged()
    }

    fun onShareButtonClick(view: View) {
        val position = view.tag as Int
        val hotel = hotels[position]

        // Tạo một Intent để chia sẻ thông tin về bookmark
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT, "Check out this hotel: ${hotel.name}\n" +
                "Location: ${hotel.locationDetail}\n" +
                "Description: ${hotel.description}\n" +
                "Rate: ${hotel.point}")

        // Mở activity chia sẻ với Intent đã tạo
        context.startActivity(Intent.createChooser(shareIntent, "Share bookmark via"))
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}
