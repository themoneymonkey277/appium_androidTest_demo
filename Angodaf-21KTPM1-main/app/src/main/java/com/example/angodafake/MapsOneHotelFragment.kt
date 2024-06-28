package com.example.angodafake

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.example.angodafake.Utilities.HotelUtils
import com.example.angodafake.Utilities.PictureUtils
import com.example.angodafake.Utilities.VoucherUtils
import com.example.angodafake.db.Hotel
import com.example.angodafake.db.Voucher

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso

class MapsOneHotelFragment(private var idUser: String) : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->

        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
    private lateinit var listHotels: List<Hotel>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map_one_hotel, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { googleMap ->

            val args = arguments
            val itemPosition = args?.getString("hotelPosition")
            val searchText = args?.getString("searchText")
            val hotelIds = args?.getStringArray("hotelIds")
            val saveIds = args?.getStringArray("saveIds")
            val checkIn = args?.getString("checkIn")
            val checkOut = args?.getString("checkOut")
            val numberOfRooms = args?.getInt("numberOfRooms")
            val numberOfGuests = args?.getInt("numberOfGuests")
            val flow = args?.getString("Flow_1")

            println(checkIn)
            println(checkOut)
            println(numberOfRooms)
            println(numberOfGuests)

            val price_room: TextView = view.findViewById(R.id.price)
            val price_room_new: TextView = view.findViewById(R.id.priceNew)
            val firstRectangle: TextView = view.findViewById(R.id.firstRectangle)
            val img: ImageView = view.findViewById(R.id.imageView)
            val hotelName = view.findViewById<TextView>(R.id.hotelName)
            val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
            val pointView = view.findViewById<TextView>(R.id.point)
            val rateStatus: TextView = view.findViewById(R.id.rateStatus)
            val count_cmt: TextView = view.findViewById(R.id.cmt)


            Log.d("FilterDetailFragment", "Hotel IDs: ${hotelIds?.joinToString(", ")}, Search Text: $searchText")
            if (itemPosition != null) {

                HotelUtils.getHotelList() { hotelList ->
                    val hotel = hotelList.find { hotel ->
                        hotel.ID == itemPosition
                    }

                    hotelName.text = hotel?.name
                    ratingBar.rating = hotel?.star!!.toFloat()
                    count_cmt.text = hotel.total_comments.toString() + " nhận xét"
                    pointView.text = hotel.point.toString()
                    rateStatus.text = when (hotel.point?.toInt()){
                        in 0 until 3 -> { "Rất tệ" }
                        in 3 until 5 -> { "Tệ" }
                        in 5 until 6 -> { "Trung bình" }
                        in 6 until 7 -> { "Hài lòng" }
                        in 7 until 8 -> { "Rất tốt" }
                        in 8 until 9 -> { "Tuyệt vời" }
                        else -> { "Trên cả tuyệt vời" }
                    }

                    val location = LatLng(hotel?.latitude ?: 0.0, hotel?.longitude ?: 0.0)
                    val title = "${hotel?.name}"
                    val snippet = "Giá: ${hotel?.money} đ"
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(location)
                            .title(title)
                            .snippet(snippet)
                    )

                    // Phương thức moveCamera di chuyển tầm nhìn của bản đồ đến vị trí đầu tiên trong danh sách khách sạn
                    if (hotel != null) {
                        val defaultZoomLevel = 10f

                        val firstHotelLocation = LatLng(hotel.latitude ?: 0.0, hotel.longitude ?: 0.0)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstHotelLocation, defaultZoomLevel))
                    }

                    view.findViewById<Button>(R.id.watchRoom).setOnClickListener {
                        val arg = Bundle()
                        arg.putString("hotelPosition", itemPosition)
                        arg.putString("searchText", searchText)
                        arg.putStringArray("hotelIds", hotelIds)
                        arg.putStringArray("saveIds", saveIds)
                        arg.putString("hotelName", hotel?.name)
                        arg.putString("checkIn", checkIn)
                        arg.putString("checkOut", checkOut)
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

                    VoucherUtils.getAllVouchers(itemPosition!!) { vouchers ->
                        val listVoucher = mutableListOf<Voucher>()

                        for (voucher in vouchers) {
                            if (voucher.quantity!! > 0) {
                                listVoucher.add(voucher)
                            }
                        }
                        if (listVoucher.isNotEmpty()) {
                            firstRectangle.text =
                                "Đã áp dụng đ " + listVoucher[0].money_discount.toString()
                            price_room.visibility = View.VISIBLE
                            val priceText = hotel?.money.toString() + " đ"
                            val spannableString = SpannableString(priceText)
                            spannableString.setSpan(
                                StrikethroughSpan(),
                                0,
                                priceText.length,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            price_room.text = spannableString
                            println(listVoucher[0].money_discount!!)
                            if (hotel != null) {
                                price_room_new.text =
                                    (hotel.money?.minus(listVoucher[0].money_discount!!)).toString() + " đ"
                            }
                        } else {
                            price_room.visibility = View.GONE
                            price_room_new.text = hotel?.money.toString() + " đ"
                        }

                        PictureUtils.getPicturesByHotelID(hotel.ID!!) { picture ->
                            if (picture.isNotEmpty()) {
                                Picasso.get().load(picture[0].url).into(img)
                            } else {
                                val idPicture = context?.resources?.getIdentifier(
                                    "quang_ba_khach_san",
                                    "drawable",
                                    context?.packageName
                                )

                                img.setImageResource(idPicture!!)
                            }
                        }
                    }
                }
            }

            view.findViewById<ImageView>(R.id.back).setOnClickListener {
                val arg = Bundle()

                arg.putString("hotelPosition", itemPosition)
                arg.putString("searchText", searchText)
                arg.putStringArray("hotelIds", hotelIds)
                arg.putStringArray("saveIds", saveIds)
                arg.putString("checkIn", checkIn)
                arg.putString("checkOut", checkOut)
                arg.putInt("numberOfRooms", numberOfRooms!!)
                arg.putInt("numberOfGuests", numberOfGuests!!)

                val filterFragment = Hotel_infor(idUser)
                filterFragment.arguments = arg

                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, filterFragment)
                    .addToBackStack(null)
                    .commit()
            }


        }
    }
}