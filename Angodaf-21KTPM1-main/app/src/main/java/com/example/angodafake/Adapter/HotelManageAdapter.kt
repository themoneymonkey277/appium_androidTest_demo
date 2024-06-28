package com.example.angodafake.Adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.AddHotelFragment
import com.example.angodafake.BillFragment
import com.example.angodafake.MainActivity
import com.example.angodafake.R
import com.example.angodafake.Utilities.PictureUtils
import com.example.angodafake.Utilities.PurchaseUtils
import com.example.angodafake.Utilities.RoomUtils
import com.example.angodafake.db.Hotel
import com.example.angodafake.db.Rooms
import com.squareup.picasso.Picasso

interface OnHotelDeleteListener {
    fun onHotelDeleted(hotel: Hotel)
}
class HotelManageAdapter (private val context: Context, private var hotel_list: ArrayList<Hotel>, var date: String, var dateType: Int, var searchStr: String) : RecyclerView.Adapter<HotelManageAdapter.ViewHolder>() {
    private var onDeleteListener: OnHotelDeleteListener? = null

    fun setOnDeleteListener(listener: OnHotelDeleteListener) {
        onDeleteListener = listener
    }

    var onItemClick: ((Hotel) -> Unit)? = null
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val btn_edit = listItemView.findViewById<ImageButton>(R.id.btn_edit)
        val btn_delete = listItemView.findViewById<ImageButton>(R.id.btn_delete)
        val tv_hotelName = listItemView.findViewById<TextView>(R.id.tv_hotelName)
        val tv_city = listItemView.findViewById<TextView>(R.id.tv_city)
        val layout_bookedRoomsQty = listItemView.findViewById<RelativeLayout>(R.id.layout_bookedRoomsQty)
        val tv_bookedRoomsQty = listItemView.findViewById<TextView>(R.id.tv_bookedRoomsQty)
        val imageView = listItemView.findViewById<ImageView>(R.id.imageView)
        init {
            listItemView.setOnClickListener { onItemClick?.invoke(hotel_list[adapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.custom_manage_hotel, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    override fun getItemCount(): Int {
        return hotel_list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hotel = hotel_list[position]
        holder.btn_edit.setOnClickListener {
            //chuyen sang fragment edit
            val arg = Bundle()
            arg.putString("from", "edit")
            arg.putString("idHotel", hotel.ID)
            arg.putString("date", date)
            arg.putString("dateType", dateType.toString())
            arg.putString("searchStr", searchStr)
            arg.putString("hotelName", hotel.name)
            arg.putString("city", hotel.city)
            arg.putString("locationDetail", hotel.locationDetail)
            arg.putString("longitude", hotel.longitude.toString())
            arg.putString("latitude", hotel.latitude.toString())
            arg.putInt("star", hotel.star!!)
            arg.putString("phoneN", hotel.phoneNumber)
            arg.putString("description", hotel.description)
            arg.putString("convenient", hotel.conveniences)
            arg.putString("highlight", hotel.highlight)
            arg.putString("checkin", hotel.checkIn)
            arg.putString("checkout", hotel.checkOut)
            arg.putString("merchantCode", hotel.merchantCode)

            PictureUtils.getPicturesByHotelID(hotel.ID!!){
                val pics = ArrayList<String>()
                for (pic in it){
                    pics.add(pic.url!!)
                }
                Log.d("pics", pics.toString())
                arg.putStringArrayList("pics", pics)
            }

            val editHotelFrag = AddHotelFragment(hotel.ID_Owner!!)
            editHotelFrag.arguments = arg

            val mainActivity = context as MainActivity
            mainActivity.replaceFragment(editHotelFrag)
        }

        holder.btn_delete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Xác nhận")
            builder.setMessage("Bạn có chắc chắn muốn xóa khách sạn này không?")

            builder.setPositiveButton("Xóa") { dialog, _ ->
                onDeleteListener?.onHotelDeleted(hotel)
                dialog.dismiss()
            }

            builder.setNegativeButton("Hủy bỏ") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        holder.tv_hotelName.text = hotel.name
        holder.tv_city.text = hotel.city
        PictureUtils.getPictureByHotelID(hotel.ID!!){
            Picasso.get().load(it.url)
                .into(holder.imageView)
        }

        if (dateType == 0){
            hotel.ID?.let {
                PurchaseUtils.getBookedRoomBillsByHotelIDAndBookedDate(it, date) { bookedRoomBills, bookedRoomQty ->
                    RoomUtils.getRoomQtyByHotelID(it) { roomQty ->
                        holder.tv_bookedRoomsQty.text = "$bookedRoomQty/$roomQty"
                    }
                    val list = ArrayList<String>()
                    for (bill in bookedRoomBills!!){
                        list.add(bill.ID!!)
                    }
                    holder.layout_bookedRoomsQty.setOnClickListener {
                        val arg = Bundle()
                        arg.putString("from", "edit")
                        arg.putString("date", date)
                        arg.putString("dateType", dateType.toString())
                        arg.putString("searchStr", searchStr)

                        arg.putStringArrayList("bills", list)

                        val billFrag = BillFragment(hotel.ID_Owner!!)
                        billFrag.arguments = arg

                        val mainActivity = context as MainActivity
                        mainActivity.replaceFragment(billFrag)
                    }
                }
            }
        } else if (dateType == 1){
            hotel.ID?.let {
                PurchaseUtils.getBookedRoomBillsByHotelID(it, date) { bookedRoomBills, bookedRoomQty ->
                    RoomUtils.getRoomQtyByHotelID(it) { roomQty ->
                        holder.tv_bookedRoomsQty.text = "$bookedRoomQty/$roomQty"
                    }
                    val list = ArrayList<String>()
                    for (bill in bookedRoomBills!!){
                        list.add(bill.ID!!)
                    }
                    holder.layout_bookedRoomsQty.setOnClickListener {
                        val arg = Bundle()
                        arg.putString("from", "edit")
                        arg.putString("date", date)
                        arg.putString("dateType", dateType.toString())
                        arg.putString("searchStr", searchStr)
                        arg.putStringArrayList("bills", list)

                        val billFrag = BillFragment(hotel.ID_Owner!!)
                        billFrag.arguments = arg

                        val mainActivity = context as MainActivity
                        mainActivity.replaceFragment(billFrag)
                    }
                }
            }
        }



    }


}