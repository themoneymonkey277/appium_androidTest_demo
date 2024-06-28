package com.example.angodafake.Adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
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
import com.example.angodafake.AddRoomFragment
import com.example.angodafake.BillFragment
import com.example.angodafake.MainActivity
import com.example.angodafake.R
import com.example.angodafake.Utilities.PictureUtils
import com.example.angodafake.Utilities.PurchaseUtils
import com.example.angodafake.Utilities.RoomUtils
import com.example.angodafake.db.Rooms
import com.squareup.picasso.Picasso


interface OnRoomDeleteListener {
    fun onRoomDeleted(room: Rooms)
}

class RoomManageAdapter(private val context: Context, private var room_list: ArrayList<Rooms>, var date: String, var idUser: String, var dateType: Int, var searchRoomStr: String, var searchStr: String) : RecyclerView.Adapter<RoomManageAdapter.ViewHolder>()  {
    private var onDeleteListener: OnRoomDeleteListener? = null

    fun setOnDeleteListener(listener: OnRoomDeleteListener) {
        onDeleteListener = listener
    }
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val btn_edit = listItemView.findViewById<ImageButton>(R.id.btn_edit)
        val btn_delete = listItemView.findViewById<ImageButton>(R.id.btn_delete)
        val tv_roomType = listItemView.findViewById<TextView>(R.id.tv_roomType)
        val layout_bookedRoomsQty = listItemView.findViewById<RelativeLayout>(R.id.layout_bookedRoomsQty)
        val tv_bookedRoomsQty = listItemView.findViewById<TextView>(R.id.tv_bookedRoomsQty)
        val imageView = listItemView.findViewById<ImageView>(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.custom_manage_room, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    override fun getItemCount(): Int {
        return room_list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room = room_list[position]
        holder.btn_edit.setOnClickListener {
            //chuyen sang fragment edit
            val arg = Bundle()
            arg.putString("from", "edit")
            arg.putString("date", date)
            arg.putString("date", date)
            arg.putString("dateType", dateType.toString())
            arg.putString("searchStr", searchStr)
            arg.putString("searchRoomStr", searchRoomStr)
            arg.putString("dateType", dateType.toString())
            arg.putString("searchStr", searchStr)
            arg.putString("idHotel", room.ID_Hotel)
            arg.putString("idRoom", room.ID)
            arg.putString("roomType", room.type)
            arg.putString("acreage", room.acreage.toString())
            arg.putString("direction", room.direction)
            arg.putString("benefit", room.benefit)
            arg.putString("singleBed", room.single_bed.toString())
            arg.putString("doubleBed", room.double_bed.toString())
            arg.putString("price", room.price.toString())
            arg.putString("quantity", room.quantity.toString())

            PictureUtils.getPicturesRoomByID(room.ID_Hotel!!, room.ID!!){
                val pics = ArrayList<String>()
                for (pic in it){
                    pics.add(pic.url!!)
                }
                Log.d("pics", pics.toString())
                arg.putStringArrayList("pics", pics)
            }

            val editRoomFrag = AddRoomFragment(room.ID_Hotel!!, idUser)
            editRoomFrag.arguments = arg

            val mainActivity = context as MainActivity
            mainActivity.replaceFragment(editRoomFrag)
        }

        holder.btn_delete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Xác nhận")
            builder.setMessage("Bạn có chắc chắn muốn xóa loại phòng này không?")

            builder.setPositiveButton("Xóa") { dialog, _ ->
                onDeleteListener?.onRoomDeleted(room)
                dialog.dismiss()
            }

            builder.setNegativeButton("Hủy bỏ") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        holder.tv_roomType.text = room.type
        PictureUtils.getRoomPictureByID(room.ID_Hotel!!, room.ID!!){
            Picasso.get().load(it?.url)
                .into(holder.imageView)
        }
        if (dateType == 0){
            PurchaseUtils.getBookedRoomBillsByRoomIDAndBookedDate(room.ID_Hotel!!, room.ID!!, date) { bookedRoomBills, bookedRoomQty ->
                holder.tv_bookedRoomsQty.text = "$bookedRoomQty/${room.quantity}"
                val list = ArrayList<String>()
                for (bill in bookedRoomBills!!){
                    list.add(bill.ID!!)
                }
                holder.layout_bookedRoomsQty.setOnClickListener {
                    val arg = Bundle()
                    arg.putString("from", "edit_room")
                    arg.putString("date", date)
                    arg.putString("idHotel", room.ID_Hotel)
                    arg.putString("dateType", dateType.toString())
                    arg.putString("searchStr", searchStr)
                    arg.putString("searchRoomStr", searchRoomStr)
                    arg.putStringArrayList("bills", list)

                    val billFrag = BillFragment(idUser)
                    billFrag.arguments = arg

                    val mainActivity = context as MainActivity
                    mainActivity.replaceFragment(billFrag)
                }
            }
        } else{
            PurchaseUtils.getBookedRoomBillsByRoomID(room.ID_Hotel!!, room.ID!!, date) { bookedRoomBills, bookedRoomQty ->
                holder.tv_bookedRoomsQty.text = "$bookedRoomQty/${room.quantity}"
                val list = ArrayList<String>()
                for (bill in bookedRoomBills!!){
                    list.add(bill.ID!!)
                }
                holder.layout_bookedRoomsQty.setOnClickListener {
                    val arg = Bundle()
                    arg.putString("from", "edit_room")
                    arg.putString("date", date)
                    arg.putString("idHotel", room.ID_Hotel)
                    arg.putString("dateType", dateType.toString())
                    arg.putString("searchStr", searchStr)
                    arg.putString("searchRoomStr", searchRoomStr)
                    arg.putStringArrayList("bills", list)

                    val billFrag = BillFragment(idUser)
                    billFrag.arguments = arg

                    val mainActivity = context as MainActivity
                    mainActivity.replaceFragment(billFrag)
                }
            }
        }
    }
}