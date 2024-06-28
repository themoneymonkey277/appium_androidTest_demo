package com.example.angodafake

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.angodafake.Utilities.CommentUtils
import com.example.angodafake.Utilities.HotelUtils
import com.example.angodafake.Utilities.PictureUtils
import com.example.angodafake.Utilities.RoomUtils
import com.example.angodafake.Utilities.UserUtils
import com.example.angodafake.db.Hotel
import com.example.angodafake.db.Rooms
import com.example.angodafake.db.User
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class PastCancelPurchaseDetail : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var btnCall: ImageButton
    private lateinit var btnLocal: ImageButton
    private lateinit var btnChat: Button
    private lateinit var btnOrder: Button
    private lateinit var btnComment: Button

    private lateinit var hotel: Hotel
    private lateinit var room: Rooms
    private lateinit var owner: User

    private lateinit var hotelImage: ImageView
    private lateinit var hotelName: TextView
    private lateinit var hotelStar: RatingBar
    private lateinit var hotelPhone: TextView
    private lateinit var hotelAddress: TextView
    private lateinit var dateCome: TextView
    private lateinit var dateGo: TextView

    private lateinit var roomImage: ImageView
    private lateinit var roomType: TextView
    private lateinit var roomQuantity: TextView
    private lateinit var roomDes: TextView
    private lateinit var roomBenefit: TextView

    private lateinit var clientName: TextView
    private lateinit var clientQuantity: TextView

    private lateinit var roomTime: TextView
    private lateinit var roomCost: TextView
    private lateinit var roomTax: TextView
    private lateinit var total: TextView
    private lateinit var roomMethod: TextView

    private lateinit var reasoN: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    fun format (temp: String) : String {
        val inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")
        val outputFormatter = DateTimeFormatter.ofPattern("d 'tháng' M, yyyy", Locale("vi"))

        val date = LocalDate.parse(temp, inputFormatter)
        val formattedDate = date.format(outputFormatter)

        return formattedDate
    }

    fun format1 (temp: String) : String {
        return temp.replace("\\", ", ")
    }

    fun format2(temp1: String, temp2: String, single: String, Double: String): Spanned? {
        val roomDescription: String = if (single == "0") {
            "$temp1 | $temp2 &#8575;&#178; | $Double giường đôi"
        } else {
            "$temp1 | $temp2 &#8575;&#178; | $single giường đơn"
        }

        return Html.fromHtml(roomDescription, Html.FROM_HTML_MODE_COMPACT)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun format3 (come: String, go: String, room: String) : String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateA = LocalDate.parse(come, formatter)
        val dateB = LocalDate.parse(go, formatter)

        val daysBetween = ChronoUnit.DAYS.between(dateA, dateB)

        return "$room phòng x $daysBetween đêm"
    }

    fun format4(number: Double): String {
        val formatSymbols = DecimalFormatSymbols()
        formatSymbols.groupingSeparator = '.'

        val decimalFormat = DecimalFormat("#,##0", formatSymbols)
        return decimalFormat.format(number)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_past_cancel_purchase_detail)

        hotelName = findViewById(R.id.hotel_name)
        hotelStar = findViewById(R.id.ratingBar)
        hotelPhone = findViewById(R.id.hotel_phone)
        hotelAddress = findViewById(R.id.hotel_address)
        dateCome = findViewById(R.id.nhanphong)
        dateGo = findViewById(R.id.traphong)

        roomType = findViewById(R.id.type_room)
        roomQuantity = findViewById(R.id.quan_room)
        roomDes = findViewById(R.id.des_room)
        roomBenefit = findViewById(R.id.benefit)

        clientName = findViewById(R.id.name_client)
        clientQuantity = findViewById(R.id.quan_client)

        roomTime = findViewById(R.id.room_time)
        roomCost = findViewById(R.id.cost)
        roomTax = findViewById(R.id.tax)
        total = findViewById(R.id.total)
        roomMethod = findViewById(R.id.payment_methods)

        reasoN = findViewById(R.id.reason)

        btnComment = findViewById(R.id.btn_comment)

        val purchaseID = intent.getStringExtra("id_purchase")
        val hotelPic = intent.getStringExtra("pic_hotel")
        val hotelID = intent.getStringExtra("id_hotel")
        val ownerID = intent.getStringExtra("id_owner")
        val roomID = intent.getStringExtra("id_room")
        val roomquantity = intent.getStringExtra("quantity").toString()
        val come = intent.getStringExtra("date_come").toString()
        val go = intent.getStringExtra("date_go").toString()

        hotelImage = findViewById(R.id.hotel_image)
        Picasso.get().load(hotelPic).into(hotelImage)

        roomImage = findViewById(R.id.imageRoom)
        PictureUtils.getPictureRoom(hotelID!!, roomID!!) { pictureRooms ->
            Picasso.get().load(pictureRooms.url).into(roomImage)
        }

        btnComment.visibility = View.GONE
        CommentUtils.getCommentByIDPurchase(purchaseID!!) { comment ->
            if (intent.getStringExtra("detail") == "HOAN_TAT" && comment.isEmpty()) {
                btnComment.visibility = View.VISIBLE
            }
        }

        if (hotelID != null && ownerID != null && roomID != null) {
            HotelUtils.getHotelByID(hotelID) { h ->
                hotel = h

                hotelName.text = hotel.name
                hotelStar.rating = hotel.star!!.toFloat()
                hotelPhone.text = hotel.phoneNumber
                hotelAddress.text = hotel.locationDetail
            }

            UserUtils.getUserByID(ownerID) { o ->
                owner = o!!

                clientName.text = owner.name
            }

            RoomUtils.getRoomByID(hotelID, roomID) { r ->
                room = r

                roomType.text = room.type
                roomTime.text = format3(come, go, roomquantity)
                roomCost.text = format4(room.price!!.toDouble())
                roomTax.text = format4((room.price?.times(0.1))!!.toDouble())

                roomDes.text = format2(room.direction.toString(), room.acreage.toString(), room.single_bed.toString(), room.double_bed.toString())

                roomBenefit.text = format1(room.benefit.toString())

                clientQuantity.text = (roomquantity.toInt().let { room.capacity?.times(it) }).toString() + " người"
            }
        }

        dateCome.text = format(come)
        dateGo.text = format(go)
        roomQuantity.text = roomquantity
        total.text = format4(intent.getStringExtra("total_purchase")!!.toDouble())

        val temp1: TextView = findViewById(R.id.line7)
        val temp2: TextView = findViewById(R.id.textView16)
        val reason = intent.getStringExtra("reason")
        if (reason != "") {
            reasoN.text = reason
        } else {
            temp1.visibility = View.GONE
            temp2.visibility = View.GONE
            reasoN.visibility = View.GONE
        }

        val method = intent.getStringExtra("method")
        if (method == "CASH") {
            roomMethod.text = "Thanh toán bằng tiền mặt"
        } else if (method == "CARD_PAYMENT") {
            roomMethod.text = "Thanh toán bằng thẻ ngân hàng"
        } else {
            roomMethod.text = "Thanh toán bằng ví điện tử"
        }

        val status1: TextView = findViewById(R.id.status1)
        val status2: TextView = findViewById(R.id.status2)
        val status3: TextView = findViewById(R.id.status3)
        val status4: TextView = findViewById(R.id.status4)
        val statusPurchase = intent.getStringExtra("status_purchase")
        println(statusPurchase)
        if (statusPurchase == "DA_HOAN_TIEN") {
            status2.visibility = View.GONE
            status3.visibility = View.GONE
            status4.visibility = View.GONE
        } else if (statusPurchase == "CHUA_HOAN_TIEN") {
            status1.visibility = View.GONE
            status3.visibility = View.GONE
            status4.visibility = View.GONE
        } else if (statusPurchase == "DA_THANH_TOAN"){
            status1.visibility = View.GONE
            status2.visibility = View.GONE
            status3.visibility = View.GONE
        } else {
            status1.visibility = View.GONE
            status2.visibility = View.GONE
            status4.visibility = View.GONE
        }

        btnBack = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        btnCall = findViewById(R.id.btn_call)
        btnCall.setOnClickListener {
            val phone_number = hotelPhone.text
            val phone_uri = Uri.parse("tel:$phone_number")
            val phone_intent = Intent(Intent.ACTION_DIAL, phone_uri)
            startActivity(phone_intent)
        }

        btnLocal = findViewById(R.id.btn_local)
        btnLocal.setOnClickListener {
            println("btnLocal is pressed")
        }

        btnChat = findViewById(R.id.btn_chat)
        btnChat.setOnClickListener {
//            val phone_number = hotelPhone.text
//            val phone_uri = Uri.parse("sms:$phone_number")
//            val sms_intent = Intent(Intent.ACTION_SENDTO, phone_uri)
//            startActivity(sms_intent)
            val intent = Intent(this, ChatRoom::class.java)
            intent.putExtra("ID_User", ownerID)
            intent.putExtra("ID_Partner", hotelID)
            intent.putExtra("Name_User", clientName.text)
            intent.putExtra("Type_User", "User")
            startActivity(intent)
        }

        btnOrder = findViewById(R.id.btn_reorder)
        btnOrder.setOnClickListener {
//            println("btnOrder is pressed")
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Xóa các activity trước đó và chỉ hiển thị MainActivity
            intent.putExtra("replaceChannel", "Hotel_infor")
            intent.putExtra("idUser", ownerID)
            intent.putExtra("nameHotel", hotelName.text)
            setResult(Activity.RESULT_OK, intent)
            startActivity(intent)
            finish()
        }

        btnComment.setOnClickListener {
            val intent = Intent(this, MyCommentForm::class.java)
            intent.putExtra("hotelName", hotelName.text)
            intent.putExtra("ID_Owner", ownerID)
            intent.putExtra("ID_Hotel", hotelID)
            intent.putExtra("ID_Purchase", purchaseID)
            startActivity(intent)
        }
    }
}