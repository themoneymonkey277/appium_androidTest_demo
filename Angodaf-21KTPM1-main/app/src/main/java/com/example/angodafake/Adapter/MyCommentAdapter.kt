package com.example.angodafake.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.R
import com.example.angodafake.Utilities.HotelUtils
import com.example.angodafake.Utilities.PictureUtils
import com.example.angodafake.Utilities.PurchaseUtils
import com.example.angodafake.Utilities.RoomUtils
import com.example.angodafake.Utilities.UserUtils
import com.example.angodafake.VoucherEditForm
import com.example.angodafake.db.Comment
import com.example.angodafake.db.Voucher
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.concurrent.TimeUnit

class MyCommentAdapter(private val context: Context, private var comment: MutableList<Comment>) : RecyclerView.Adapter<MyCommentAdapter.MyViewHolder>() {
    var onItemClick: ((Comment) -> Unit)? = null
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(comment: Comment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val commentItem = LayoutInflater.from(parent.context).inflate(R.layout.custom_comment_detail, parent, false)
        return MyViewHolder(commentItem)
    }

    override fun getItemCount(): Int {
        return comment.size
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = comment[position]

        HotelUtils.getHotelByID(currentItem.ID_Hotel!!) {hotel ->
            holder.hotelName.text = hotel.name
            holder.hotelLocal.text = format1(hotel.locationDetail!!)

            PurchaseUtils.getPurchaseByID(currentItem.ID_Purchase!!) {purchase ->
                RoomUtils.getRoomByID(currentItem.ID_Hotel!!, purchase.ID_Room!!) {rooms ->
                    holder.roomType.text = rooms.type
                }
                holder.dateCome.text = format(purchase.date_come!!)
                holder.dateGo.text = format(purchase.date_go!!)
                holder.night.text = time(purchase.date_come!!, purchase.date_go!!).toString()
                holder.room.text = purchase.quantity.toString()
            }
        }

        PictureUtils.getPictureByHotelID(currentItem.ID_Hotel!!) {pictureHotel ->
            Picasso.get().load(pictureHotel.url).into(holder.hotelImage)
        }

        holder.point.text = currentItem.point.toString()
        holder.title.text = currentItem.title
        holder.content.text = currentItem.content
        holder.dateComment.text = format(currentItem.time!!)
    }
    class MyViewHolder(commentItem: View) : RecyclerView.ViewHolder(commentItem) {
        val hotelImage: ImageView = commentItem.findViewById(R.id.imageRoom)
        val hotelName: TextView = commentItem.findViewById(R.id.textView)
        val hotelLocal: TextView = commentItem.findViewById(R.id.textView1)
        val roomType: TextView = commentItem.findViewById(R.id.textView2)
        val dateCome: TextView = commentItem.findViewById(R.id.textView4)
        val dateGo: TextView = commentItem.findViewById(R.id.textView6)
        val night: TextView = commentItem.findViewById(R.id.textView8)
        val room: TextView = commentItem.findViewById(R.id.textView10)
        val point: TextView = commentItem.findViewById(R.id.point)
        val title: TextView = commentItem.findViewById(R.id.title)
        val content: TextView = commentItem.findViewById(R.id.content)
        val dateComment: TextView = commentItem.findViewById(R.id.dateComment)
    }

    private fun format(inputDateStr: String): String {
        val inputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("dd 'thg' MM yyyy", Locale.getDefault())

        val inputDate = inputDateFormat.parse(inputDateStr)
        return outputDateFormat.format(inputDate!!)
    }

    private fun format1(address: String): String {
        val parts = address.split(", ")
        val country = parts.last()
        val city = parts[parts.size - 2]

        return "$city, $country"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun time(come: String, go: String): Long {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateA = LocalDate.parse(come, formatter)
        val dateB = LocalDate.parse(go, formatter)

        return ChronoUnit.DAYS.between(dateA, dateB)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Comment>) {
        comment = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: MyCommentAdapter.OnItemClickListener) {
        this.listener = listener
    }
}