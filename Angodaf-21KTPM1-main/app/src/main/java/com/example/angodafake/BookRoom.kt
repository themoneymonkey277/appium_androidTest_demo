package com.example.angodafake

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.angodafake.Adapter.VoucherHotelAdapter
import com.example.angodafake.Utilities.HotelUtils
import com.example.angodafake.Utilities.PictureUtils
import com.example.angodafake.Utilities.RoomUtils
import com.example.angodafake.Utilities.UserUtils
import com.example.angodafake.Utilities.VoucherUtils
import com.example.angodafake.db.Purchase
import com.example.angodafake.db.Voucher
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import vn.momo.momo_partner.AppMoMoLib
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale
class BookRoom : AppCompatActivity() {
    private lateinit var countdownTimer: CountDownTimer
    private lateinit var countdownTextView: TextView
    private lateinit var paymentMethodLayout: ConstraintLayout
    private lateinit var radioGroupPaymentMethod: RadioGroup
    private lateinit var bookRoomBtn: Button
    private lateinit var backBtn: ImageView
    private lateinit var hotelPicture: ImageView
    private lateinit var idUser: String

    private var firstPrice: Int = 0
    private var discountValue: Int = 0
    private val databaseReference = FirebaseDatabase.getInstance().reference.child("purchases")
    private lateinit var dialog: Dialog
    private lateinit var anim: LottieAnimationView
    private lateinit var hotelID: String
    private lateinit var RoomID: String
    private lateinit var purchaseId: String
    private lateinit var purchase: Purchase

    private lateinit var seenVoucherBtn: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var linearAdapter: VoucherHotelAdapter
    private var listVoucher: MutableList<Voucher> = mutableListOf()

    private lateinit var hotelName: String
    private lateinit var typeRoom: String
    private lateinit var notic: TextView

    private var idVoucher: String = ""
    private val merchantCode = "MOMOC2IC20220510"
    private var quantity: Int = 0
    private var checkMethod: Boolean = true
    private var i: Int = -1

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createData(hotel_ID: String) {
        listVoucher.clear()

        VoucherUtils.getAllVouchers(hotel_ID) {vouchers ->
            for (voucher in vouchers) {
                if (voucher.quantity!! > 0) {
                    listVoucher.add(voucher)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_room)

        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION

        dialog = Dialog(this)
        dialog.setContentView(R.layout.purchased)
        anim = dialog.findViewById(R.id.checkPurchase)

        paymentMethodLayout = findViewById(R.id.paymentMethod)
        countdownTextView = findViewById(R.id.timer)
        bookRoomBtn = findViewById(R.id.bookRoomBtn)
        backBtn = findViewById(R.id.back)
        hotelPicture = findViewById(R.id.imageView8)

        val checkInTime = intent.getStringExtra("checkInTime") ?: ""
        val checkOutTime = intent.getStringExtra("checkOutTime") ?: ""
        idUser = intent.getStringExtra("idUser") ?: ""
        findViewById<TextView>(R.id.checkIn).text = convertDateTimeToString(checkInTime, 1)
        findViewById<TextView>(R.id.checkOut).text = convertDateTimeToString(checkOutTime, 1)
        val dayInHotel = calculateDaysBetweenDates(checkInTime, checkOutTime).toString()
        findViewById<TextView>(R.id.day).text = dayInHotel

        findViewById<TextView>(R.id.hotelName).text = intent.getStringExtra("hotelName") ?: ""
        findViewById<TextView>(R.id.textView35).text = "Đặt phòng không có rủi ro! Quý khách có thể huỷ bỏ đến ${convertDateTimeToString(checkInTime, 2)} và không phải trả gì!"
        findViewById<TextView>(R.id.calender).text = "Đặt phòng hôm nay và thanh toán vào ${convertDateTimeToString(checkInTime, 2)}"

        hotelID = intent.getStringExtra("hotelID").toString()
        PictureUtils.getPictureByHotelID(hotelID){
            Picasso.get().load(it.url).into(hotelPicture)
        }
        val roomID = intent.getStringExtra("roomID")
        HotelUtils.getHotelByID(hotelID){ hotel ->
            findViewById<TextView>(R.id.locationDetail).text = hotel.locationDetail
            findViewById<TextView>(R.id.rating).text = when (hotel.point?.toInt()){
                in 0 until 3 -> { "${hotel.point} Cực tệ" }
                in 3 until 5 -> { "${hotel.point} Tệ" }
                in 5 until 6 -> { "${hotel.point} Trung bình" }
                in 6 until 8 -> { "${hotel.point} Tốt" }
                in 8 until 9 -> { "${hotel.point} Rất tốt" }
                else -> { "${hotel.point} Tuyệt vời" }
            }
            hotelName = hotel.name.toString()
            findViewById<TextView>(R.id.textView43).text = "Hotline: ${hotel.phoneNumber}"
            dialog.findViewById<TextView>(R.id.hotelName).text = hotel.name
            dialog.findViewById<TextView>(R.id.timeCheckIn).text = "${hotel.checkIn} ${convertDateTimeToString(checkInTime, 3)}"
            dialog.findViewById<TextView>(R.id.timeCheckOut).text = "${hotel.checkOut} ${convertDateTimeToString(checkOutTime, 3)}"
        }

        val roomQuantity = intent.getStringExtra("roomQuantity").toString()
        RoomUtils.getRoomByID(hotelID, roomID!!){ room ->
            RoomID = room.ID.toString()
            findViewById<TextView>(R.id.type_room).text = "${roomQuantity} x ${room.type}"
            findViewById<TextView>(R.id.textView37).text = "Tối đa: ${room.capacity} người lớn"
            findViewById<TextView>(R.id.bedQ).text = "${room.single_bed} giường đơn - ${room.double_bed} giường đôi"
            firstPrice = (room.price?.times(roomQuantity.toInt()) ?: 1) * dayInHotel.toInt()
            findViewById<TextView>(R.id.Price).text = "${formatMoney(firstPrice)} đ"
            findViewById<TextView>(R.id.PriceAfterPromotion).text = "${formatMoney(firstPrice - discountValue)} đ"
            dialog.findViewById<TextView>(R.id.roomID).text = "${room.type}"
            typeRoom = room.type.toString()
        }

        UserUtils.getUserByID(idUser){user ->
            findViewById<TextView>(R.id.customerName).text = user?.name
            dialog.findViewById<TextView>(R.id.customerName).text = user?.name
            findViewById<TextView>(R.id.mailConfirm).text = "Chúng tôi sẽ gửi xác nhận đặt phòng của bạn đến ${user?.email}. Vui lòng kiểm tra và xác nhận đặt phòng."
        }

        findViewById<TextView>(R.id.cost).text = "Giá gốc (${roomQuantity} phòng x ${dayInHotel} đêm)"
        findViewById<TextView>(R.id.Promotion).text = "-${formatMoney(discountValue)} đ"

        findViewById<RadioButton>(R.id.cashChoice).text = "Thanh toán vào ${convertDateTimeToString(checkInTime, 2)}"
        findViewById<TextView>(R.id.textView47).text = "Đặt phòng hôm nay và thanh toán vào ${convertDateTimeToString(checkInTime, 2)}"
        findViewById<TextView>(R.id.textView48).text = "Huỷ đặt phòng trước 00:00, ${convertDateTimeToString(checkInTime, 2)}"

        // Ẩn các layout khi khởi động Activity
        paymentMethodLayout.visibility = View.GONE

        // Khởi tạo đồng hồ đếm ngược
        startCountdownTimer()

        // Xử lý sự kiện khi RadioButton trong RadioGroup Payment Method được chọn
        radioGroupPaymentMethod = findViewById(R.id.pickMethod)
        radioGroupPaymentMethod.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.cashChoice -> {
                    paymentMethodLayout.visibility = View.GONE

                    notic.visibility = View.GONE
                    recyclerView.visibility = View.GONE

                    idVoucher = ""
                    checkMethod = false
                    discountValue = 0
                    i = -1
                    findViewById<TextView>(R.id.Promotion).text = "-${formatMoney(discountValue)} đ"
                    findViewById<TextView>(R.id.PriceAfterPromotion).text = "${formatMoney(firstPrice - discountValue)} đ"
                }
                R.id.onlineChoice -> {
                    paymentMethodLayout.visibility = View.VISIBLE

                    checkMethod = true
                }
            }
        }

        backBtn.setOnClickListener {
            onBackPressed()
        }

        bookRoomBtn.setOnClickListener {
            purchaseId = getPurchaseID()

            bookRoom(hotelID, idUser, roomID,
                roomQuantity, getTimePurchase(), (firstPrice - discountValue).toDouble(),
                checkInTime, checkOutTime, dialog, anim)

            dialog.setOnDismissListener(DialogInterface.OnDismissListener {
                backBtn.callOnClick()
            })

            if(idVoucher != ""){
                VoucherUtils.minusVoucher(idVoucher,quantity) {result ->
                    println(result)
                }
            }
        }

        createData(intent.getStringExtra("hotelID")!!)

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        seenVoucherBtn = findViewById(R.id.seen_voucher)
        notic = findViewById(R.id.notic)
        recyclerView = findViewById(R.id.fieldvoucher)

        notic.visibility = View.GONE

        seenVoucherBtn.setOnClickListener {
            println("giảm giá ${discountValue}")
            if (checkMethod) {
                showBottomSheet()
            } else {
                val noticDialog = AlertDialog.Builder(this)
                    .setTitle("Lưu ý!")
                    .setMessage("Phiếu Voucher không được áp dụng cho phương thức thanh toán trực tiếp!")
                    .setCancelable(true)
                    .setNegativeButton("Đóng") {_,_ ->}
                    .show()
            }
        }
    }

    private fun showDialog(dialog: Dialog, anim: LottieAnimationView){
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed(Runnable{
            anim.visibility = View.VISIBLE
            anim.playAnimation()
        }, 300)
    }

    private fun getPurchaseID(): String{
        val currentTime = Calendar.getInstance()
        val year = currentTime.get(Calendar.YEAR)
        val month = currentTime.get(Calendar.MONTH) + 1
        val day = currentTime.get(Calendar.DAY_OF_MONTH)
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)
        val second = currentTime.get(Calendar.SECOND)
        return  "$day$month$year$hour$minute$second"
    }

    private fun getTimePurchase(): String {
        val currentTime = Calendar.getInstance()
        // Định dạng thời gian theo đúng yêu cầu
        val dateFormat = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())

        return dateFormat.format(currentTime.time)
    }
    private fun bookRoom(hotelID: String, customerID: String, RoomID: String,
                         quantity: String, timeBooking: String, total_purchase: Double,
                         checkInTime: String, checkOutTime: String, dialog: Dialog, anim: LottieAnimationView){

        if(findViewById<RadioButton>(R.id.cashChoice).isChecked){
            // Trả tiền mặt -> lưu thông tin ngay vào DB
             purchase = Purchase(ID_Owner = customerID, ID_Hotel = hotelID, ID_Room = RoomID, quantity = quantity.toInt(),
                method = "CASH", time_booking = timeBooking, time_purchase = "", time_cancel = "", reason = "", total_purchase = total_purchase,
                status_purchase = "CHUA_THANH_TOAN", detail = "SAP_TOI", date_come = checkInTime, date_go = checkOutTime)
             databaseReference.child(purchaseId).setValue(purchase)
             showDialog(dialog, anim)
        } else if (findViewById<RadioButton>(R.id.onlineChoice).isChecked){
            // Kiểm tra vnpay hay momo
            if(findViewById<RadioButton>(R.id.cardChoice).isChecked){
                // CARD
//                val purchase = Purchase(ID_Owner = customerID, ID_Hotel = hotelID, ID_Room = RoomID, quantity = quantity.toInt(),
//                    method = "CARD_PAYMENT", time_booking = timeBooking, time_purchase = "$hour:$minute:$second $day/$month/$year", time_cancel = "", reason = "", total_purchase = total_purchase,
//                    status_purchase = "DA_THANH_TOAN", detail = "SAP_TOI", date_come = checkInTime, date_go = checkOutTime)
//                databaseReference.child(purchaseId).setValue(purchase)

            } else if(findViewById<RadioButton>(R.id.momoChoice).isChecked){
                // MoMo
                 purchase = Purchase(ID_Owner = customerID, ID_Hotel = hotelID, ID_Room = RoomID, quantity = quantity.toInt(),
                    method = "MOMO", time_booking = timeBooking, time_purchase = getTimePurchase(), time_cancel = "", reason = "", total_purchase = total_purchase,
                    status_purchase = "DA_THANH_TOAN", detail = "SAP_TOI", date_come = checkInTime, date_go = checkOutTime)
                 requestPayment(purchaseId, total_purchase.toInt())
            }
        }
    }

    private fun showBottomSheet() {
        recyclerView.visibility = View.VISIBLE

        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        linearAdapter = VoucherHotelAdapter(this, listVoucher, i, firstPrice)
        recyclerView.adapter = linearAdapter

        linearAdapter.onItemClick = { contact, position ->
            i = position

            idVoucher = contact.ID.toString()
            quantity = contact.quantity!!
            if (contact.money_discount == 0.0) {
                calculatePrice2(firstPrice.toDouble(), contact.limit_price!!, contact.max_discount!!, contact.percentage!!)
            } else {
                calculatePrice1(firstPrice.toDouble(), contact.limit_price!!, contact.money_discount!!)
            }
            if (discountValue != 0) {
                notic.visibility = View.VISIBLE
            } else {
                notic.visibility = View.GONE
                i = -1
                val noticDialog = AlertDialog.Builder(this)
                    .setTitle("Lưu ý!")
                    .setMessage("Bạn chưa đủ điều kiện sử dụng phiếu Voucher này!")
                    .setCancelable(true)
                    .setNegativeButton("Đóng") {_,_ ->}
                    .show()
            }

            findViewById<TextView>(R.id.Promotion).text = "-${formatMoney(discountValue)} đ"
            findViewById<TextView>(R.id.PriceAfterPromotion).text = "${formatMoney(firstPrice - discountValue)} đ"
            recyclerView.visibility = View.GONE
        }
    }

    private fun calculatePrice1(olePrice: Double, limit_price: Double, money_discount: Double) {
        discountValue = if (olePrice >= limit_price) {
            money_discount.toInt()
        } else {
            0
        }
    }

    private fun calculatePrice2(olePrice: Double, limit_price: Double, max_discount: Double, percentage: Int){
        discountValue = if (olePrice >= limit_price) {
            val money_discount = olePrice * percentage * 0.01
            if (money_discount > max_discount) {
                max_discount.toInt()
            } else {
                money_discount.toInt()
            }
        } else {
            0
        }
    }

    private fun startCountdownTimer() {
        val countdownTimeInMillis: Long = 20 * 60 * 1000
        countdownTimer = object : CountDownTimer(countdownTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val formattedTime = formatTime(millisUntilFinished)
                countdownTextView.text = formattedTime
            }

            override fun onFinish() {
                countdownTextView.text = "00:00:00"
            }
        }
        countdownTimer.start()
    }

    private fun formatTime(timeInMillis: Long): String {
        val seconds = timeInMillis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60

        val formattedTime = String.format("%02d:%02d:%02d",
            hours % 24,
            minutes % 60,
            seconds % 60
        )
        return formattedTime
    }

    private fun convertDateTimeToString(dateTimeString: String, mode: Int): String {
        val dateTime = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateTimeString)
        val calendar = Calendar.getInstance()
        if (dateTime != null) {
            calendar.time = dateTime
        }

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val dayOfWeekString = when (dayOfWeek) {
            Calendar.MONDAY -> "T2"
            Calendar.TUESDAY -> "T3"
            Calendar.WEDNESDAY -> "T4"
            Calendar.THURSDAY -> "T5"
            Calendar.FRIDAY -> "T6"
            Calendar.SATURDAY -> "T7"
            Calendar.SUNDAY -> "CN"
            else -> ""
        }

        val monthString = when (month) {
            Calendar.JANUARY -> "tháng 1"
            Calendar.FEBRUARY -> "tháng 2"
            Calendar.MARCH -> "tháng 3"
            Calendar.APRIL -> "tháng 4"
            Calendar.MAY -> "tháng 5"
            Calendar.JUNE -> "tháng 6"
            Calendar.JULY -> "tháng 7"
            Calendar.AUGUST -> "tháng 8"
            Calendar.SEPTEMBER -> "tháng 9"
            Calendar.OCTOBER -> "tháng 10"
            Calendar.NOVEMBER -> "tháng 11"
            Calendar.DECEMBER -> "tháng 12"
            else -> ""
        }
        if (mode == 1) {
            return "$dayOfWeekString, $dayOfMonth $monthString"
        }
        else if (mode == 2) {
            return "ngày $dayOfMonth $monthString, $year"
        }
        else if (mode == 3){
            return "$dayOfWeekString, $dayOfMonth $monthString, $year"
        }
        return ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateDaysBetweenDates(startDate: String, endDate: String): Long {
        val dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")
        val startDateObj = LocalDate.parse(startDate, dateFormatter)
        val endDateObj = LocalDate.parse(endDate, dateFormatter)

        // Tính số ngày giữa hai ngày
        val daysBetween = ChronoUnit.DAYS.between(startDateObj, endDateObj)

        return daysBetween
    }

    fun formatMoney(amount: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale.getDefault()) as DecimalFormat
        formatter.applyPattern("#,###")
        return formatter.format(amount.toLong())
    }

    //Get token through MoMo app
    private fun requestPayment(IDPayment: String, amount: Int) {
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT)
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN)

        val eventValue: MutableMap<String, Any> = HashMap()
        //client Required
        eventValue["merchantname"] = hotelName //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue["merchantcode"] = merchantCode //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue["amount"] = amount //Kiểu integer
        eventValue["orderId"] = IDPayment //uniqueue id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
        eventValue["orderLabel"] = "Payment ID: $IDPayment" //gán nhãn

        //client Optional - bill info
        eventValue["merchantnamelabel"] = "Đặt phòng khách sạn" //gán nhãn
        eventValue["fee"] = 0 //Kiểu integer
        eventValue["description"] = typeRoom //mô tả đơn hàng - short description

        //client extra data
        eventValue["requestId"] = merchantCode + "merchant_billId_" + System.currentTimeMillis()
        eventValue["partnerCode"] = merchantCode
        //Example extra data
        val objExtraData = JSONObject()
        try {
            objExtraData.put("ID_Hotel", hotelID)
            objExtraData.put("ID_Room", RoomID)
            objExtraData.put("quantity", quantity)
            objExtraData.put("totalPayment", amount)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        eventValue["extraData"] = objExtraData.toString()
        eventValue["extra"] = ""
        AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue)
    }

    //Get token callback from MoMo app an submit to server side
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if (data != null) {
                if (data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE
                    println("Thanh cong")
                    println("Get token " + data.getStringExtra("message"))
                    val token = data.getStringExtra("data") //Token response
                    val phoneNumber = data.getStringExtra("phonenumber")
                    var env = data.getStringExtra("env")
                    if (env == null) {
                        env = "app"
                    }
                    if (token != null && token != "") {
                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                        // IF Momo topup success, continue to process your order
                        showDialog(dialog, anim)
                        databaseReference.child(purchaseId).setValue(purchase)
                    } else {
                        requestPayment(purchaseId, (firstPrice - discountValue))
                    }
                } else if (data.getIntExtra("status", -1) == 1) {
                    //TOKEN FAIL
                    val message =
                        if (data.getStringExtra("message") != null) data.getStringExtra("message") else "Thất bại"
                    println(message)
                } else if (data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
                    requestPayment(purchaseId, (firstPrice - discountValue))
                } else {
                    //TOKEN FAIL
                    requestPayment(purchaseId, (firstPrice - discountValue))
                }
            } else {
                requestPayment(purchaseId, (firstPrice - discountValue))
            }
        } else {
            requestPayment(purchaseId, (firstPrice - discountValue))
        }
    }
}