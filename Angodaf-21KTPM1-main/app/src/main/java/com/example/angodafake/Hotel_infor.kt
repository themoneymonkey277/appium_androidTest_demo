package com.example.angodafake

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.Adapter.ImageAdapterHotel
import com.example.angodafake.Adapter.VoucherAdapter
import com.example.angodafake.Utilities.HotelUtils
import com.example.angodafake.Utilities.PictureUtils
import com.example.angodafake.Utilities.UserUtils
import com.example.angodafake.Utilities.VoucherUtils
import com.example.angodafake.db.Hotel
import com.example.angodafake.db.Picture_Hotel
import com.example.angodafake.db.User
import com.example.angodafake.db.Voucher
import java.util.UUID


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Hotel_infor.newInstance] factory method to
 * create an instance of this fragment.
 */
class Hotel_infor(private var idUser: String) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    //private lateinit var Picture_Hotel: Picture_Hotel
    private lateinit var User: User
    private lateinit var hotel: Hotel
    private lateinit var popupWindow: PopupWindow
    private lateinit var voucherfield: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var linearAdapter: VoucherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.custom_hotel_detail, container, false)
        val args = arguments
        val itemPosition = args?.getString("hotelPosition")
        val hotelIds = args?.getStringArray("hotelIds")
        val saveIds = args?.getStringArray("saveIds")
        val searchText = args?.getString("searchText")
        val checkInfind = args?.getString("checkIn")
        val checkOutfind = args?.getString("checkOut")
        val numberOfRooms = args?.getInt("numberOfRooms")
        val numberOfGuests = args?.getInt("numberOfGuests")
        val flow = args?.getString("Flow_1")
        println(checkInfind)
        println(checkOutfind)
        println(numberOfRooms)
        println(numberOfGuests)

        val nameTextView = view.findViewById<TextView>(R.id.hotel_name)
        val locationTextView = view.findViewById<TextView>(R.id.address_hotel)
//        val img: ImageView = view.findViewById(R.id.hotel_image)
        val description: TextView = view.findViewById(R.id.description)
        val convenience: TextView = view.findViewById(R.id.convenience)
        val highlight: TextView = view.findViewById(R.id.highlight)
        val price_room: TextView = view.findViewById(R.id.price)
        val price_room_new: TextView = view.findViewById(R.id.priceNew)
        val nameOwner: TextView = view.findViewById(R.id.nameOwner)
        val hotel_phone: TextView = view.findViewById(R.id.hotel_phone)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        val checkIn: TextView = view.findViewById(R.id.time_In)
        val checkOut: TextView = view.findViewById(R.id.time_Out)
        val imgAvt: ImageView = view.findViewById(R.id.avt)
        val showDetail: TextView = view.findViewById(R.id.showDetail)
        val firstRectangle: TextView = view.findViewById(R.id.firstRectangle)
        val imageRV: RecyclerView = view.findViewById(R.id.hotel_image)
        val inforVoucher: TextView = view.findViewById(R.id.inforVoucher)

        val ratingField: RelativeLayout = view.findViewById(R.id.ratingField)
        val pointView = view.findViewById<TextView>(R.id.point)
        val rateStatus: TextView = view.findViewById(R.id.rateStatus)
        val count_cmt: TextView = view.findViewById(R.id.cmt)
        val btnChat: ImageView = view.findViewById(R.id.chat)
        val btnCall: ImageButton = view.findViewById(R.id.btn_call)
        val hotelPhone: TextView = view.findViewById(R.id.hotel_phone)

        var nameUser: String = ""

        inforVoucher.setOnClickListener {
            showPopup1()
        }

        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        if (itemPosition != null) {
            HotelUtils.getHotelByID(itemPosition) { ho ->
                hotel = ho

                btnChat.setOnClickListener {
                    val intent = Intent(context, ChatRoom::class.java)
                    intent.putExtra("ID_User", idUser)
                    intent.putExtra("ID_Partner", ho.ID)
                    intent.putExtra("Name_User", ho.name)
                    intent.putExtra("Type_User", "User")
                    startActivity(intent)
                }

                PictureUtils.getPicturesByHotelID(hotel.ID!!) { pictureList ->
                    val imageAdapter = ImageAdapterHotel()
                    imageRV.adapter = imageAdapter
                    imageAdapter.submitList(pictureList)

                    UserUtils.getUserByID(hotel.ID_Owner!!){user ->
                        User = user!!

                        Log.d("FilterFragment", "Received data - user: $User")

                        nameTextView.text = hotel.name
                        locationTextView.text = hotel.locationDetail
                        pointView.text = hotel.point.toString()
                        //description.text = hotel.description
                        //convenience.text = hotel.conveniences
                        checkIn.text = hotel.checkIn
                        checkOut.text = hotel.checkOut
                        nameOwner.text = User.name
                        ratingBar.rating = hotel.star!!.toFloat()
                        hotel_phone.text = hotel.phoneNumber
                        count_cmt.text = hotel.total_comments.toString() + " nhận xét"

                        val conveniences = hotel.highlight?.split("\\")
                        val formattedconveniences = conveniences?.map { "✅    $it" }
                        val formattedconvenience = formattedconveniences?.joinToString("\n")
                        convenience.text = formattedconvenience

                        val highlights = hotel.highlight?.split("\\")
                        val formattedHighlights = highlights?.map { "\uD83D\uDCA0    $it" }
                        val formattedHighlight = formattedHighlights?.joinToString("\n")
                        highlight.text = formattedHighlight

                        rateStatus.text = when (hotel.point!!.toInt()) {
                            in 0 until 3 -> {
                                "Rất tệ"
                            }

                            in 3 until 5 -> {
                                "Tệ"
                            }

                            in 5 until 6 -> {
                                "Trung bình"
                            }

                            in 6 until 7 -> {
                                "Hài lòng"
                            }

                            in 7 until 8 -> {
                                "Rất tốt"
                            }

                            in 8 until 9 -> {
                                "Tuyệt vời"
                            }

                            else -> {
                                "Trên cả tuyệt vời"
                            }
                        }

                        pointView.text = hotel.point.toString()

                        count_cmt.text = hotel.total_comments.toString() + " nhận xét"

                        val fullDescription = hotel.description
                        val initialLinesToShow = 3
                        val lines = fullDescription?.split("\\n")
                        val initialDescription = lines?.take(initialLinesToShow)?.joinToString("\n")
                        description.text = initialDescription

                        ratingField.setOnClickListener {
                            val intent = Intent(requireContext(), Hotel_comment::class.java)
                            intent.putExtra("idHotel", itemPosition)
                            intent.putExtra("point", pointView.text)
                            intent.putExtra("rateStatus", rateStatus.text)
                            intent.putExtra("cmt", count_cmt.text)
                            intent.putExtra("clean", hotel.clean.toString())
                            intent.putExtra("convenience", hotel.convenience.toString())
                            intent.putExtra("location", hotel.location.toString())
                            intent.putExtra("service", hotel.service.toString())
                            intent.putExtra("money", hotel.money_rating.toString())
                            startActivity(intent)
                        }

                        view.findViewById<ImageView>(R.id.imageView4).setOnClickListener {
                            if (flow == null) {
                                val arg = Bundle()
                                arg.putStringArray("hotelIds", hotelIds)
                                arg.putStringArray("saveIds", saveIds)
                                arg.putString("searchText", searchText)
                                arg.putString("checkIn", checkInfind)
                                arg.putString("checkOut", checkOutfind)
                                arg.putInt("numberOfRooms", numberOfRooms!!)
                                arg.putInt("numberOfGuests", numberOfGuests!!)

                                // Khởi tạo Fragment Filter và đính kèm Bundle
                                val filterFragment = Filter(idUser)
                                filterFragment.arguments = arg

                                // Thay thế Fragment hiện tại bằng Fragment Filter
                                val fragmentManager = requireActivity().supportFragmentManager
                                fragmentManager.beginTransaction()
                                    .replace(R.id.frameLayout, filterFragment)
                                    .addToBackStack(null)  // Để quay lại Fragment Home khi ấn nút Back
                                    .commit()
                            } else if (flow == "reorder") {
                                replaceFragmentToRoom(idUser)
                            }
                        }

                        view.findViewById<Button>(R.id.watchRoom).setOnClickListener {
                            val arg = Bundle()
                            arg.putString("hotelPosition", itemPosition)
                            arg.putString("searchText", searchText)
                            arg.putStringArray("hotelIds", hotelIds)
                            arg.putStringArray("saveIds", saveIds)
                            arg.putString("hotelName", hotel.name)
                            arg.putString("checkIn", checkInfind)
                            arg.putString("checkOut", checkOutfind)
                            arg.putInt("numberOfRooms", numberOfRooms!!)
                            arg.putInt("numberOfGuests", numberOfGuests!!)

                            val listRoom = ListRoom(idUser)
                            listRoom.arguments = arg

                            val fragmentManager = requireActivity().supportFragmentManager
                            fragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, listRoom)
                                .addToBackStack(null)
                                .commit()
                        }

                        btnCall.setOnClickListener {
                            val phone_number = hotelPhone.text
                            val phone_uri = Uri.parse("tel:$phone_number")
                            val phone_intent = Intent(Intent.ACTION_DIAL, phone_uri)
                            startActivity(phone_intent)
                        }

                        view.findViewById<ImageButton>(R.id.btn_local).setOnClickListener {
                            val arg = Bundle()

                            arg.putString("hotelPosition", itemPosition)
                            arg.putString("searchText", searchText)
                            arg.putStringArray("hotelIds", hotelIds)
                            arg.putStringArray("saveIds", saveIds)
                            arg.putString("searchText", searchText)
                            arg.putString("checkIn", checkInfind)
                            arg.putString("checkOut", checkOutfind)
                            arg.putInt("numberOfRooms", numberOfRooms!!)
                            arg.putInt("numberOfGuests", numberOfGuests!!)

                            val mapFragment = MapsOneHotelFragment(idUser)
                            mapFragment.arguments = arg
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, mapFragment)
                                .addToBackStack(null)
                                .commit()
                        }
                        showDetail.setOnClickListener {
                            showPopup()
                        }
                    }
                }

                VoucherUtils.getAllVouchers(itemPosition) {vouchers ->
                        println(itemPosition)
//                        println(listVoucher)
                    val listVoucher = mutableListOf<Voucher>()

                    for (voucher in vouchers) {
                        if (voucher.quantity!! > 0) {
                            listVoucher.add(voucher)
                        }
                    }
                    if(listVoucher.isNotEmpty()){
                        firstRectangle.text = "Đã áp dụng đ " + listVoucher[0].money_discount.toString()
                        price_room.visibility = View.VISIBLE
                        val priceText = hotel.money.toString() + " đ"
                        val spannableString = SpannableString(priceText)
                        spannableString.setSpan(StrikethroughSpan(), 0, priceText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        price_room.text = spannableString
                        println(listVoucher[0].money_discount!!)
                        price_room_new.text = (hotel.money?.minus(listVoucher[0].money_discount!!)).toString() + " đ"
                    }
                    else{
                        price_room.visibility = View.GONE
                        price_room_new.text = hotel.money.toString() + " đ"
                    }

                    voucherfield = view.findViewById(R.id.voucher)
                    voucherfield.layoutManager = layoutManager
                    voucherfield.setHasFixedSize(true)

                    linearAdapter = VoucherAdapter(requireActivity(), listVoucher, idUser, hotel.name!!)
                    voucherfield.adapter = linearAdapter
                }
            }
        }
        return view
    }

    private fun replaceFragmentToRoom(idUser: String) {
        println(idUser)
        val mainActivity = activity as MainActivity
        mainActivity.replaceFragment(MyRoom(idUser))
    }

    private fun showPopup1() {
        val dialog = Dialog(requireActivity())
        val inflater = LayoutInflater.from(requireActivity())
        val dialogView = inflater.inflate(R.layout.popup_1, null)

        val btn_close: ImageButton = dialogView.findViewById(R.id.btn_close)
        btn_close.setOnClickListener {
            dialog.dismiss()
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT // Kích thước ngang theo match_parent
        layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT // Kích thước chiều cao tự động
        window?.attributes = layoutParams

        dialog.show()
    }

    private fun showPopup() {
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_description, null)

        // Đặt alpha cho layout gốc để làm mờ
        val rootView = requireActivity().window.decorView.findViewById<View>(android.R.id.content)
        rootView.alpha = 0.5f

        // Khởi tạo PopupWindow
        popupWindow = PopupWindow(
            popupView,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )

        // Thiết lập animation và hiển thị PopupWindow từ dưới lên
        popupWindow.animationStyle = R.style.PopupAnimation
        popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 0)

        // Xử lý sự kiện khi nhấn vào nút hoặc các thành phần trong PopupWindow
        val closeButton = popupView.findViewById<ImageButton>(R.id.btn_close)
        closeButton.setOnClickListener {
            rootView.alpha = 1.0f
            popupWindow.dismiss() // Đóng PopupWindow khi nhấn nút đóng

        }

        val name = popupView.findViewById<TextView>(R.id.name)
        val detail = popupView.findViewById<TextView>(R.id.detail)
        name.text = hotel.name
        detail.text = hotel.description


        popupWindow.setOnDismissListener {
            // Xử lý khi PopupWindow bị đóng
            rootView.alpha = 1.0f
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyHotel.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, idUser: String) =
            Hotel_infor(idUser).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}