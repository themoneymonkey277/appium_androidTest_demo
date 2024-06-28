package com.example.angodafake.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.R
import com.example.angodafake.Utilities.VoucherUtils
import com.example.angodafake.VoucherAddForm
import com.example.angodafake.db.Hotel

class VoucherOfHotelAdapter(private val context: Context, private var hotel: MutableList<Hotel>) : RecyclerView.Adapter<VoucherOfHotelAdapter.MyViewHolder>() {
    var onItemClick: ((Hotel) -> Unit)? = null
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(hotel: Hotel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val voucherItem = LayoutInflater.from(parent.context).inflate(R.layout.custom_voucher_of_hotel_adapter, parent, false)
        return MyViewHolder(voucherItem)
    }

    override fun getItemCount(): Int {
        return hotel.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = hotel[position]

        holder.nameHotel.text = currentItem.name

        VoucherUtils.getAllVouchers(currentItem.ID!!) { vouchers ->
            holder.totalVoucher.text = vouchers.size.toString()
            var useful = 0
            var useless = 0

            for (voucher in vouchers) {
                if (voucher.quantity!! > 0) {
                    useful++
                } else {
                    useless++
                }
            }

            holder.usefulVoucher.text = useful.toString()
            holder.uselessVoucher.text = useless.toString()
        }

        holder.btn_add.setOnClickListener {
            val intent = Intent(context, VoucherAddForm::class.java)
            intent.putExtra("idHotel", currentItem.ID)
            context.startActivity(intent)
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentItem)
        }
    }

    class MyViewHolder(hotelItem: View) : RecyclerView.ViewHolder(hotelItem) {
        val nameHotel: TextView = hotelItem.findViewById(R.id.hotelName)
        val usefulVoucher: TextView = hotelItem.findViewById(R.id.usefulVoucher)
        val uselessVoucher: TextView = hotelItem.findViewById(R.id.uselessVoucher)
        val totalVoucher: TextView = hotelItem.findViewById(R.id.totalVoucher)
        val btn_add: ImageButton = hotelItem.findViewById(R.id.btn_add)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Hotel>) {
        hotel = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: VoucherOfHotelAdapter.OnItemClickListener) {
        this.listener = listener
    }
}