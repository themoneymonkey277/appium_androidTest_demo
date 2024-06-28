package com.example.angodafake.Adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.ListRoom
import com.example.angodafake.R
import com.example.angodafake.VoucherEditForm
import com.example.angodafake.db.Voucher
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class InactiveVoucherAdapter(private val context: Context, private var voucher: MutableList<Voucher>) : RecyclerView.Adapter<InactiveVoucherAdapter.MyViewHolder>() {
    var onItemClick: ((Voucher) -> Unit)? = null
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(voucher: Voucher)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val voucherItem = LayoutInflater.from(parent.context).inflate(R.layout.custom_inactive_voucher_adapter, parent, false)
        return MyViewHolder(voucherItem)
    }

    override fun getItemCount(): Int {
        return voucher.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = voucher[position]

        holder.voucher_discount.text = format(currentItem.percentage!!, currentItem.money_discount!!)
        holder.voucher_min.text = format2(currentItem.limit_price!!)

        if (currentItem.percentage != 0) {
            holder.voucher_max.text = format3(currentItem.max_discount!!)
        } else {
            holder.voucher_max.visibility = View.GONE
        }

        holder.voucher_quantity.text = currentItem.quantity.toString()
        holder.rewrite.setOnClickListener {
            val intent = Intent(context, VoucherEditForm::class.java)
            intent.putExtra("id", currentItem.ID)
            intent.putExtra("idHotel", currentItem.ID_Hotel)
            intent.putExtra("min", currentItem.limit_price.toString())
            intent.putExtra("max", currentItem.max_discount.toString())
            intent.putExtra("percent", currentItem.percentage.toString())
            intent.putExtra("money", currentItem.money_discount.toString())
            intent.putExtra("quantity", currentItem.quantity.toString())
            context.startActivity(intent)
        }
    }

    fun format(percentage: Int, money: Double): Spanned? {
        val temp: String
        if (percentage != 0) {
            temp = "Chính sách ưu đãi: Giảm $percentage%"
        } else {
            temp = "Chính sách ưu đãi: Giảm &#8363;${format1(money)}"
        }

        return Html.fromHtml(temp, Html.FROM_HTML_MODE_COMPACT)
    }

    fun format1(money: Double): String {
        val formatSymbols = DecimalFormatSymbols()
        formatSymbols.groupingSeparator = ','

        val decimalFormat = DecimalFormat("#,##0", formatSymbols)
        return decimalFormat.format(money)
    }

    fun format2(voucher_min: Double): Spanned? {
        val temp = "Đơn tối thiểu &#8363;${format1(voucher_min)}"
        return Html.fromHtml(temp, Html.FROM_HTML_MODE_COMPACT)
    }

    fun format3(voucher_max: Double): Spanned? {
        val temp = "Giảm tối đa &#8363;${format1(voucher_max)}"
        return Html.fromHtml(temp, Html.FROM_HTML_MODE_COMPACT)
    }

    class MyViewHolder(voucherItem: View) : RecyclerView.ViewHolder(voucherItem) {
        val voucher_discount: TextView = voucherItem.findViewById(R.id.voucher_discount)
        val voucher_min: TextView = voucherItem.findViewById(R.id.voucher_min)
        val voucher_max: TextView = voucherItem.findViewById(R.id.voucher_max)
        val voucher_quantity: TextView = voucherItem.findViewById(R.id.voucher_quantity)
        val rewrite: Button = voucherItem.findViewById(R.id.rewrite)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Voucher>) {
        voucher = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: InactiveVoucherAdapter.OnItemClickListener) {
        this.listener = listener
    }
}