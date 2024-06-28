package com.example.angodafake.Adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.CancelPurchase
import com.example.angodafake.Home
import com.example.angodafake.Hotel_infor
import com.example.angodafake.MainActivity
import com.example.angodafake.MyRoom
import com.example.angodafake.PurchaseExtra
import com.example.angodafake.R
import com.example.angodafake.db.Purchase
import com.squareup.picasso.Picasso
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ActivePurchaseAdapter(private val fragment: Fragment, private var activePurchase: MutableList<PurchaseExtra>) : RecyclerView.Adapter<ActivePurchaseAdapter.MyViewHolder>() {
    var onItemClick: ((PurchaseExtra) -> Unit)? = null
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(purchase: Purchase)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val activePurchaseItem = LayoutInflater.from(parent.context).inflate(R.layout.custom_active_purchase_adapter, parent, false)
        return MyViewHolder(activePurchaseItem)
    }

    override fun getItemCount(): Int {
        return activePurchase.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = activePurchase[position]

        holder.namehotel.text = currentItem.nameHotel
        Picasso.get().load(currentItem.imageHotel).into(holder.imagehotel)
        holder.timebooking.text = currentItem.Purchase?.time_booking
        holder.idorder.text = currentItem.Purchase?.ID.toString()

        if (currentItem.Purchase?.status_purchase == "DA_THANH_TOAN") {
            holder.statusperchase1.text = "Đã thanh toán"
        } else {
            holder.statusperchase2.text = "Chưa thanh toán"
        }

        holder.checkin.text = format(currentItem.Purchase?.date_come.toString())
        holder.checkout.text = format(currentItem.Purchase?.date_go.toString())

        holder.cancelbtn.setOnClickListener {
            val intent = Intent(fragment.context, CancelPurchase::class.java)
            intent.putExtra("id_user", currentItem.Purchase?.ID_Owner)
            intent.putExtra("id_purchase", currentItem.Purchase?.ID)
            intent.putExtra("date_come", currentItem.Purchase?.date_come)
            intent.putExtra("status_purchase", currentItem.Purchase?.status_purchase)
            fragment.context?.startActivity(intent)
        }

        holder.reorderbtn.setOnClickListener {
            val mainActivity = fragment.requireActivity() as MainActivity
            mainActivity.replaceFragment(Home(currentItem.Purchase?.ID_Owner!!, currentItem.nameHotel))
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentItem)
        }
    }
    class MyViewHolder(activePurchaseItem: View) : RecyclerView.ViewHolder(activePurchaseItem) {
        val timebooking: TextView = activePurchaseItem.findViewById(R.id.time_booking)
        val idorder: TextView = activePurchaseItem.findViewById(R.id.id_order)
        val statusperchase1: TextView = activePurchaseItem.findViewById(R.id.status1)
        val statusperchase2: TextView = activePurchaseItem.findViewById(R.id.status2)
        val imagehotel: ImageView = activePurchaseItem.findViewById(R.id.image_hotel)
        val namehotel: TextView = activePurchaseItem.findViewById(R.id.name_hotel)
        val checkin: TextView = activePurchaseItem.findViewById(R.id.check_in)
        val checkout: TextView = activePurchaseItem.findViewById(R.id.check_out)
        val cancelbtn: Button = activePurchaseItem.findViewById(R.id.btn_removeorder)
        val reorderbtn: Button = activePurchaseItem.findViewById(R.id.btn_reorder1)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun format (temp: String) : String {
        val inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")
        val outputFormatter = DateTimeFormatter.ofPattern("d 'thg' M, yyyy", Locale("vi"))

        val date = LocalDate.parse(temp, inputFormatter)
        val formattedDate = date.format(outputFormatter)

        return formattedDate
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<PurchaseExtra>) {
        activePurchase = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: ActivePurchaseAdapter.OnItemClickListener) {
        this.listener = listener
    }
}