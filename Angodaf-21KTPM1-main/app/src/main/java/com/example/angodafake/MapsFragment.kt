package com.example.angodafake

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.example.angodafake.Utilities.HotelUtils
import com.example.angodafake.db.Hotel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment(private var idUser: String) : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_maps, container, false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { googleMap ->

            val args = arguments
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

            Log.d("FilterDetailFragment", "Hotel IDs: ${hotelIds?.joinToString(", ")}, Search Text: $searchText")
            if (hotelIds != null) {

                HotelUtils.getHotelList() { hotelList ->
                    listHotels = hotelList.filter { hotel ->
                        hotel.ID in hotelIds!!
                    }

                    // Duyệt qua danh sách các khách sạn và hiển thị các đánh dấu trên bản đồ
                    listHotels.forEach { hotel ->
                        val location = LatLng(hotel.latitude ?: 0.0, hotel.longitude ?: 0.0)
                        val title = "${hotel.name}"
                        val snippet = "Giá: ${hotel.money} đ"
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(location)
                                .title(title)
                                .snippet(snippet)
                        )
                    }

                    // Phương thức moveCamera di chuyển tầm nhìn của bản đồ đến vị trí đầu tiên trong danh sách khách sạn
                    if (listHotels.isNotEmpty()) {
                        val defaultZoomLevel = 10f
                        println(listHotels[0].latitude)
                        println(listHotels[0].longitude)

                        val firstHotelLocation = LatLng(listHotels[0].latitude ?: 0.0, listHotels[0].longitude ?: 0.0)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstHotelLocation, defaultZoomLevel))
                    }
                }
            }

            view.findViewById<ImageView>(R.id.back).setOnClickListener {
                val arg = Bundle()

                arg.putString("searchText", searchText)
                arg.putStringArray("hotelIds", hotelIds)
                arg.putStringArray("saveIds", saveIds)
                arg.putString("checkIn", checkInfind)
                arg.putString("checkOut", checkOutfind)
                arg.putInt("numberOfRooms", numberOfRooms!!)
                arg.putInt("numberOfGuests", numberOfGuests!!)

                val filterFragment = Filter(idUser)
                filterFragment.arguments = arg

                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, filterFragment)
                    .addToBackStack(null)  // Để quay lại Fragment Home khi ấn nút Back
                    .commit()
            }
        }
    }
}