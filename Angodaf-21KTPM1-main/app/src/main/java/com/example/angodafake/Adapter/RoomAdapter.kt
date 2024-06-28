package com.example.angodafake.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.R
import com.example.angodafake.Utilities.PictureUtils
import com.example.angodafake.Utilities.PurchaseUtils
import com.example.angodafake.db.Picture_Hotel
import com.example.angodafake.db.Picture_Room
import com.example.angodafake.db.Purchase
import com.example.angodafake.db.Rooms
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class RoomAdapter(private val context: Context, private var rooms: List<Rooms>, private var intArray: IntArray, private var checkIn: String, private var checkOut: String, private var idHotel: String) : RecyclerView.Adapter<RoomAdapter.ViewHolder>() {
    //private lateinit var Picture: Picture
    private var listener: OnItemClickListener? = null
    // Interface cho sự kiện click
    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onPlusClick(position: Int)
        fun onMinusClick(position: Int)
        fun onBookRoomClick(position: Int)
    }

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val roomName = listItemView.findViewById<TextView>(R.id.roomName)
        val count = listItemView.findViewById<TextView>(R.id.count)
        val countBedSingle = listItemView.findViewById<TextView>(R.id.countBedSingle)
        val countBedDouble = listItemView.findViewById<TextView>(R.id.countBedDouble)
        val convenience: TextView = listItemView.findViewById(R.id.convenience)
        val countRoom: TextView = listItemView.findViewById(R.id.countRoom)
        val price: TextView = listItemView.findViewById(R.id.price)
        val count_Room: TextView = listItemView.findViewById(R.id.count_Room)
        val firstRectangle: TextView = listItemView.findViewById(R.id.firstRectangle)
        val direction: TextView = listItemView.findViewById(R.id.direction)
        val minus: ImageView = listItemView.findViewById(R.id.minus)
        val plus: ImageView = listItemView.findViewById(R.id.plus)
        val bookRoomBtn: TextView = listItemView.findViewById(R.id.buttonSet)
        val imageRV: RecyclerView = listItemView.findViewById(R.id.imageView)



        init {
            // Thêm sự kiện click cho itemView
            itemView.setOnClickListener {
                listener?.onItemClick(adapterPosition)
            }

            // Thêm sự kiện click cho countRoom
            plus.setOnClickListener {
                listener?.onPlusClick(adapterPosition)
            }
            minus.setOnClickListener {
                listener?.onMinusClick(adapterPosition)
            }

            bookRoomBtn.setOnClickListener {
                listener?.onBookRoomClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val roomsView = inflater.inflate(R.layout.custom_room, parent, false)
        // Return a new holder instance
        return ViewHolder(roomsView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room : Rooms = rooms[position]
        holder.roomName.text = room.type
        holder.count.text = "Tối đa " + room.capacity.toString() + " người - " + room.acreage.toString() + " m\u00B2"
        holder.countBedSingle.text = room.single_bed.toString() + " giường đơn   -"
        holder.countBedDouble.text = room.double_bed.toString() + " giường đôi"
        val conveniences = room.benefit?.split("\\")
        val formattedconveniences = conveniences?.map { "❇\uFE0F    $it" }
        val formattedconvenience = formattedconveniences?.joinToString("\n")
        holder.convenience.text = formattedconvenience
        holder.direction.text = room.direction.toString()
        holder.price.text = room.price.toString() + " đ"

        Log.d("ID hotel", "room.ID_Hotel: ${idHotel}")
        Log.d("ID hotel", "room.ID: ${room.ID!!}")
        PictureUtils.getPicturesRoomByID(idHotel, room.ID!!) { pictureList ->
            Log.d("lít pic", "Size: ${pictureList.size}")

            if (pictureList.isNotEmpty()) {
                val imageAdapter = ImageAdapterRoom()
                holder.imageRV.adapter = imageAdapter
                imageAdapter.submitList(pictureList)
            }

            if(checkIn == "") {
                var rest = room.quantity!! - room.available!!

                if(rest <= 0){
                    holder.count_Room.text = "Hết phòng rồi ní ơi!"
                    holder.firstRectangle.text = "HẾT PHÒNG"
                }
                else {
                    holder.count_Room.text = rest.toString() + " phòng cuối cùng!"
                    holder.firstRectangle.text = "CÒN PHÒNG"
                }

                if(intArray[position] <= rest) {
                    holder.countRoom.text = "Số phòng: " + intArray[position].toString()
                }
                else{
                    holder.countRoom.text = "Số phòng: " + rest.toString()
                    intArray[position] = rest
                }
            }
            else{
                PurchaseUtils.getAllPurchasesByHotelID(idHotel) { purchaseList ->
                    var purchases: List<Purchase> = emptyList()
                    purchases = purchaseList
                    var count_room_purchase = 0
                    var rest = 0
                    if (purchases.isNotEmpty()) {
                        for (purchase in purchases) {
                            if (purchase.detail != "DA_HUY") {

                                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                val dateCome = dateFormat.parse(purchase.date_come)
                                val dateGo = dateFormat.parse(purchase.date_go)
                                val dateCheckIn = dateFormat.parse(checkIn)
                                val dateCheckOut = dateFormat.parse(checkOut)

                                Log.d("DateCome", "Date come: ${dateFormat.format(dateCome)}")
                                Log.d("DateGo", "Date go: ${dateFormat.format(dateGo)}")

                                if ((dateCheckIn.before(dateCome) || dateCheckIn.compareTo(dateCome) == 0) && dateCheckOut.after(dateCome)) {
                                    count_room_purchase++
                                }
                                else if (dateCheckIn.before(dateGo) && ((dateCheckOut.after(dateGo) || dateCheckOut.compareTo(dateGo) == 0))) {
                                    count_room_purchase++
                                }
                                else if (dateCheckIn.compareTo(dateCome) == 0 && dateCheckOut.compareTo(dateGo) == 0) {
                                    count_room_purchase++
                                }
                                else if(dateCheckIn.after(dateCome) && dateCheckOut.before(dateGo)){
                                    count_room_purchase++
                                }
                            }
                        }
                        rest = room.quantity!! - count_room_purchase
                    }
                    else {
                        rest = room.quantity!! - room.available!!
                    }

                    if(rest <= 0){
                        holder.count_Room.text = "Hết phòng rồi ní ơi!"
                        holder.firstRectangle.text = "HẾT PHÒNG"
                    }
                    else {
                        holder.count_Room.text = rest.toString() + " phòng cuối cùng!"
                        holder.firstRectangle.text = "CÒN PHÒNG"
                    }

                    if(intArray[position] <= rest) {
                        holder.countRoom.text = "Số phòng: " + intArray[position].toString()
                    }
                    else{
                        holder.countRoom.text = "Số phòng: " + rest.toString()
                        intArray[position] = rest
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return rooms.size
    }

    fun updateDataGradually(newData: List<Rooms>) {
        rooms = newData
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

}
