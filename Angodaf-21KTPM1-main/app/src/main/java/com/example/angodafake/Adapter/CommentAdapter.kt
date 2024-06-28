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
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.R
import com.example.angodafake.Utilities.PurchaseUtils
import com.example.angodafake.Utilities.UserUtils
import com.example.angodafake.VoucherEditForm
import com.example.angodafake.db.Comment
import com.example.angodafake.db.Voucher
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class CommentAdapter(private val context: Context, private var comment: MutableList<Comment>) : RecyclerView.Adapter<CommentAdapter.MyViewHolder>() {
    var onItemClick: ((Comment) -> Unit)? = null
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(comment: Comment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val commentItem = LayoutInflater.from(parent.context).inflate(R.layout.custom_rating, parent, false)
        return MyViewHolder(commentItem)
    }

    override fun getItemCount(): Int {
        return comment.size
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = comment[position]

        holder.title.text = currentItem.title

        if (currentItem.point!! < 3) {
            holder.status_point.text = "Rất tệ ${currentItem.point}"
        } else if (currentItem.point!! >= 3 && currentItem.point!! < 5) {
            holder.status_point.text = "Tệ ${currentItem.point}"
        } else if (currentItem.point!! >= 5 && currentItem.point!! < 6) {
            holder.status_point.text = "Trung bình ${currentItem.point}"
        } else if (currentItem.point!! >= 6 && currentItem.point!! < 7) {
            holder.status_point.text = "Hài lòng ${currentItem.point}"
        } else if (currentItem.point!! >= 7 && currentItem.point!! < 8) {
            holder.status_point.text = "Rất tốt ${currentItem.point}"
        } else if (currentItem.point!! >= 8 && currentItem.point!! < 9) {
            holder.status_point.text = "Tuyệt vời ${currentItem.point}"
        } else {
            holder.status_point.text = "Trên cả tuyệt vời ${currentItem.point}"
        }

        holder.date.text = format(currentItem.time!!)

        holder.content.text = currentItem.content

        UserUtils.getUserByID(currentItem.ID_Owner!!) {customer ->
            holder.customer.text = "${customer!!.name} | ${customer.country} | ${currentItem.type_customer}"
        }

        PurchaseUtils.getPurchaseByID(currentItem.ID_Purchase!!) {purchase ->
            holder.trip.text = "Đã ở ${time(purchase.date_come!!, purchase.date_go!!)} đêm vào ${format1(purchase.date_come!!)}"
        }
    }
    class MyViewHolder(commentItem: View) : RecyclerView.ViewHolder(commentItem) {
        val title: TextView = commentItem.findViewById(R.id.textView)
        val status_point: TextView = commentItem.findViewById(R.id.textView1)
        val date: TextView = commentItem.findViewById(R.id.textView2)
        val content: TextView = commentItem.findViewById(R.id.textView4)
        val customer: TextView = commentItem.findViewById(R.id.textView5)
        val trip: TextView = commentItem.findViewById(R.id.textView6)
    }

    private fun format(inputDate: String): String {
        val parts = inputDate.split("/") // Tách chuỗi thành các phần tử dựa trên dấu "/"
        val day = parts[0]
        val month = parts[1]
        val year = parts[2]

        val monthNames = arrayOf(
            "", "tháng 1", "tháng 2", "tháng 3", "tháng 4",
            "tháng 5", "tháng 6", "tháng 7", "tháng 8",
            "tháng 9", "tháng 10", "tháng 11", "tháng 12"
        )

        return "${monthNames[month.toInt()]} $day, $year"
    }

    private fun format1(inputDate: String): String {
        val parts = inputDate.split("/") // Tách chuỗi thành các phần tử dựa trên dấu "/"
        val day = parts[0]
        val month = parts[1]
        val year = parts[2]

        val monthNames = arrayOf(
            "", "tháng 1", "tháng 2", "tháng 3", "tháng 4",
            "tháng 5", "tháng 6", "tháng 7", "tháng 8",
            "tháng 9", "tháng 10", "tháng 11", "tháng 12"
        )

        return "${monthNames[month.toInt()]} $year"
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

    fun setOnItemClickListener(listener: CommentAdapter.OnItemClickListener) {
        this.listener = listener
    }
}