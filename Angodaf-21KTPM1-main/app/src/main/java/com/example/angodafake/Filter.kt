package com.example.angodafake

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.Adapter.HotelAdapter
//import com.example.angodafake.Adapter.HotelAdapter
import com.example.angodafake.Utilities.HotelUtils
import com.example.angodafake.Utilities.RoomUtils
import com.example.angodafake.db.Hotel
import com.example.angodafake.db.Rooms
//import com.example.angodafake.db.HotelDatabase
import com.google.android.material.slider.RangeSlider

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Filter.newInstance] factory method to
 * create an instance of this fragment.
 */
class Filter(private var idUser: String) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var view: View

    private lateinit var hotelAdapter: List<Hotel>
    private lateinit var adapter: HotelAdapter
    private lateinit var listHotels: List<Hotel>
    private var listHotelsOri: List<Hotel> = mutableListOf()
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var popupWindow: PopupWindow
    private var startValue: Int = 20000000
    private var endValue: Int = 80000000
    private lateinit var rangeSlider: RangeSlider
    private var isFitTextViewSelected: Boolean = false
    private var prevPriceLowTextColor: Int = 0
    private var prevPriceHighTextColor: Int = 0
    private var prevCommentTextColor: Int = 0
    private var prevFitTextColor: Int = 0
    private var prevLowestCheckmarkVisibility: Int = View.INVISIBLE
    private var prevHighestCheckmarkVisibility: Int = View.INVISIBLE
    private var prevCommentCheckmarkVisibility: Int = View.INVISIBLE
    private var prevFitCheckmarkVisibility: Int = View.INVISIBLE

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
    ): View {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_home_filter, container, false)

        val searchEditText = view.findViewById<TextView>(R.id.nameHotelSearch)

        // lấy dữ liệu từ trang Home
        val args = arguments
        var hotelIds = args?.getStringArray("hotelIds")
        val saveIds = args?.getStringArray("saveIds")
        val searchText = args?.getString("searchText")
        val checkIn = args?.getString("checkIn")
        val checkOut = args?.getString("checkOut")
        val numberOfRooms = args?.getInt("numberOfRooms", 0)
        val numberOfGuests = args?.getInt("numberOfGuests", 0)
        println(checkIn)
        println(checkOut)
        println(numberOfRooms)
        println(numberOfGuests)

        Log.d("FilterDetailFragment", "Hotel IDs: ${hotelIds?.joinToString(", ")}, Search Text: $searchText")
        if (hotelIds != null) {

            HotelUtils.getHotelList() { hotelList  ->
                listHotels = hotelList.filter { hotel ->
                    hotel.ID in hotelIds!!
                }

                Log.d("FilterDetailFragment", "Hotel IDs: ${listHotels.joinToString(", ")}")
                // Lấy dữ liệu từ FilterDetail
                val bundle = arguments
                if (bundle != null) {
                    val point = bundle.getDouble("point", 0.0)
                    val startValue = bundle.getInt("startValue", -1)
                    val endValue = bundle.getInt("endValue", 0)
                    val selectedCities = bundle.getString("selectedCities", "")

                    if(startValue != -1){
                        listHotels = listHotels.filter { hotel ->
                            hotel.point!! >= point
                        }

                        if(selectedCities != "") {
                            // Lọc ra danh sách các khách sạn có city thuộc selectedCities
                            listHotels = listHotels.filter { hotel ->
                                selectedCities.contains(hotel.city.toString())
                            }
                        }

                        listHotels = listHotels.filter { hotel ->
                            val money = hotel.money ?: Int.MAX_VALUE
                            money in startValue..endValue
                        }

                        val buttonShowPopup = view.findViewById<TextView>(R.id.price)
                        buttonShowPopup.setOnClickListener {
                            showPopupPrice()
                        }
                        listHotelsOri = listHotels

                        val buttonShowSort = view.findViewById<TextView>(R.id.sort)
                        buttonShowSort.setOnClickListener {
                            showPopupSort()
                        }


                        searchEditText.setText(searchText)
                        val hotelsRecyclerView = view.findViewById<RecyclerView>(R.id.contactsRV)
                        hotelAdapter = ArrayList(listHotels)
                        adapter = HotelAdapter(requireContext(), hotelAdapter, idUser)
                        adapter.setOnItemClickListener(object : HotelAdapter.OnItemClickListener {
                            override fun onItemClick(position: Int) {
                                val clickedHotel = hotelAdapter[position]
                                val ids = listHotels.map { it.ID }
                                val arg = Bundle()
                                arg.putStringArray("hotelIds", ids.toTypedArray())
                                arg.putStringArray("saveIds", saveIds)
                                arg.putString("searchText", searchText)
                                arg.putString("hotelPosition", clickedHotel.ID)
                                arg.putString("checkIn", checkIn)
                                arg.putString("checkOut", checkOut)
                                arg.putInt("numberOfRooms", numberOfRooms!!)
                                arg.putInt("numberOfGuests", numberOfGuests!!)


                                val Fragment = Hotel_infor(idUser)
                                Fragment.arguments = arg

                                val fragmentManager = requireActivity().supportFragmentManager
                                fragmentManager.beginTransaction()
                                    .replace(R.id.frameLayout, Fragment)
                                    .addToBackStack(null)  // Để quay lại Fragment Home khi ấn nút Back
                                    .commit()
                            }
                        })
                        hotelsRecyclerView.adapter = adapter
                        layoutManager = LinearLayoutManager(requireContext())
                        hotelsRecyclerView.layoutManager = layoutManager
                        hotelsRecyclerView.setHasFixedSize(true)

                        view.findViewById<Button>(R.id.backToMain).setOnClickListener {
                            val fragmentManager = requireActivity().supportFragmentManager
                            val fragmentTransaction = fragmentManager.beginTransaction()
                            fragmentTransaction.replace(R.id.frameLayout, Home(idUser, ""))
                            fragmentTransaction.commit()
                        }

                        view.findViewById<TextView>(R.id.filter).setOnClickListener {
                            val arg = Bundle()
                            if (saveIds != null) {
                                hotelIds = saveIds
                            }
                            Log.d("FilterDetailFragment", "Hotel ID Filter: ${hotelIds?.joinToString(", ")}, Search Text: $searchText")

                            arg.putStringArray("hotelIds", hotelIds)
                            arg.putStringArray("saveIds", saveIds)
                            arg.putString("searchText", searchText)
                            arg.putString("checkIn", checkIn)
                            arg.putString("checkOut", checkOut)
                            arg.putInt("numberOfRooms", numberOfRooms!!)
                            arg.putInt("numberOfGuests", numberOfGuests!!)

                            // Khởi tạo Fragment Filter và đính kèm Bundle
                            val filterFragment = FilerDetail(idUser)
                            filterFragment.arguments = arg

                            // Thay thế Fragment hiện tại bằng Fragment Filter
                            val fragmentManager = requireActivity().supportFragmentManager
                            fragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, filterFragment)
                                .addToBackStack(null)  // Để quay lại Fragment Home khi ấn nút Back
                                .commit()
                        }

                        view.findViewById<TextView>(R.id.buttonMap).setOnClickListener {
                            val arg = Bundle()
                            val ids = listHotels.map { it.ID }
                            Log.d("FilterDetailFragment", "Hotel ID Map: ${ids.joinToString(", ")}, Search Text: $searchText")

                            arg.putStringArray("hotelIds", ids.toTypedArray())
                            arg.putStringArray("saveIds", saveIds)
                            arg.putString("searchText", searchText)
                            arg.putString("checkIn", checkIn)
                            arg.putString("checkOut", checkOut)
                            arg.putInt("numberOfRooms", numberOfRooms!!)
                            arg.putInt("numberOfGuests", numberOfGuests!!)

                            val mapFragment = MapsFragment(idUser)
                            mapFragment.arguments = arg
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, mapFragment)
                                .addToBackStack(null)
                                .commit()
                        }

                    }
                    else{
                        Log.d("FilterFragment", "Received data - Point: $point, Start Value: $startValue, End Value: $endValue, Selected Cities: $selectedCities")
                        Log.d("FilterDetailFragment", "List of Hotels:")
                        listHotels.forEachIndexed { index, hotel ->
                            Log.d("FilterDetail2", "Hotel ${index + 1}: ${hotel.name}, Point: ${hotel.point}")
                        }

                        val buttonShowPopup = view.findViewById<TextView>(R.id.price)
                        buttonShowPopup.setOnClickListener {
                            showPopupPrice()
                        }
                        listHotelsOri = listHotels

                        val buttonShowSort = view.findViewById<TextView>(R.id.sort)
                        buttonShowSort.setOnClickListener {
                            showPopupSort()
                        }


                        searchEditText.setText(searchText)
                        val hotelsRecyclerView = view.findViewById<RecyclerView>(R.id.contactsRV)
                        hotelAdapter = ArrayList(listHotels)
                        adapter = HotelAdapter(requireContext(), hotelAdapter, idUser)
                        adapter.setOnItemClickListener(object : HotelAdapter.OnItemClickListener {
                            override fun onItemClick(position: Int) {
                                val clickedHotel = hotelAdapter[position]
                                val ids = listHotels.map { it.ID }
                                val arg = Bundle()
                                arg.putStringArray("hotelIds", ids.toTypedArray())
                                arg.putStringArray("saveIds", saveIds)
                                arg.putString("searchText", searchText)
                                arg.putString("hotelPosition", clickedHotel.ID)
                                arg.putString("checkIn", checkIn)
                                arg.putString("checkOut", checkOut)
                                arg.putInt("numberOfRooms", numberOfRooms!!)
                                arg.putInt("numberOfGuests", numberOfGuests!!)

                                // Khởi tạo Fragment Filter và đính kèm Bundle
                                val Fragment = Hotel_infor(idUser)
                                Fragment.arguments = arg

                                val fragmentManager = requireActivity().supportFragmentManager
                                fragmentManager.beginTransaction()
                                    .replace(R.id.frameLayout, Fragment)
                                    .addToBackStack(null)  // Để quay lại Fragment Home khi ấn nút Back
                                    .commit()
                            }
                        })
                        hotelsRecyclerView.adapter = adapter
                        layoutManager = LinearLayoutManager(requireContext())
                        hotelsRecyclerView.layoutManager = layoutManager
                        hotelsRecyclerView.setHasFixedSize(true)

                        view.findViewById<Button>(R.id.backToMain).setOnClickListener {
                            val fragmentManager = requireActivity().supportFragmentManager
                            val fragmentTransaction = fragmentManager.beginTransaction()
                            fragmentTransaction.replace(R.id.frameLayout, Home.newInstance("param1", "param2", idUser, ""))
                            fragmentTransaction.commit()
                        }

                        view.findViewById<TextView>(R.id.filter).setOnClickListener {
                            val arg = Bundle()
                            if (saveIds != null) {
                                hotelIds = saveIds
                            }
                            Log.d("FilterDetailFragment", "Hotel ID Filter: ${hotelIds?.joinToString(", ")}, Search Text: $searchText")

                            arg.putStringArray("hotelIds", hotelIds)
                            arg.putStringArray("saveIds", saveIds)
                            arg.putString("searchText", searchText)
                            arg.putString("checkIn", checkIn)
                            arg.putString("checkOut", checkOut)
                            arg.putInt("numberOfRooms", numberOfRooms!!)
                            arg.putInt("numberOfGuests", numberOfGuests!!)
                            Log.d("number2", "numberOfRooms: ${numberOfRooms}, numberOfGuests: ${numberOfGuests}")

                            // Khởi tạo Fragment Filter và đính kèm Bundle
                            val filterFragment = FilerDetail(idUser)
                            filterFragment.arguments = arg

                            // Thay thế Fragment hiện tại bằng Fragment Filter
                            val fragmentManager = requireActivity().supportFragmentManager
                            fragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, filterFragment)
                                .addToBackStack(null)  // Để quay lại Fragment Home khi ấn nút Back
                                .commit()
                        }

                        view.findViewById<TextView>(R.id.buttonMap).setOnClickListener {
                            val arg = Bundle()
                            if (saveIds != null) {
                                hotelIds = saveIds
                            }
                            Log.d("FilterDetailFragment", "Hotel ID Map: ${hotelIds?.joinToString(", ")}, Search Text: $searchText")

                            arg.putStringArray("hotelIds", hotelIds)
                            arg.putStringArray("saveIds", saveIds)
                            arg.putString("searchText", searchText)
                            arg.putString("checkIn", checkIn)
                            arg.putString("checkOut", checkOut)
                            arg.putInt("numberOfRooms", numberOfRooms!!)
                            arg.putInt("numberOfGuests", numberOfGuests!!)

                            val mapFragment = MapsFragment(idUser)
                            mapFragment.arguments = arg
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, mapFragment)
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                }
            }
        }

        return view
    }

    private fun showPopupPrice() {
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.custom_price, null)

        // Đặt alpha cho layout gốc để làm mờ
        val rootView = requireActivity().window.decorView.findViewById<View>(android.R.id.content)
        rootView.alpha = 0.5f
        startValue = 20000000
        endValue = 80000000
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
        val closeButton = popupView.findViewById<Button>(R.id.backToFilter)
        closeButton.setOnClickListener {
            rootView.alpha = 1.0f
            popupWindow.dismiss() // Đóng PopupWindow khi nhấn nút đóng

        }
        rangeSlider = popupView.findViewById(R.id.continuousSlider)
        rangeSlider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {
                // Lấy giá trị bắt đầu khi bắt đầu chạm vào slider
                startValue = slider.values[0].toInt()
                endValue = slider.values[1].toInt()
            }

            override fun onStopTrackingTouch(slider: RangeSlider) {
                // Lấy giá trị khi kết thúc chạm vào slider
                startValue = slider.values[0].toInt()
                endValue = slider.values[1].toInt()
            }
        })

        rangeSlider.addOnChangeListener { rangeSlider, value, fromUser ->
            // Lấy giá trị khi slider thay đổi
            startValue = rangeSlider.values[0].toInt()
            endValue = rangeSlider.values[1].toInt()
        }

        val buttonReset: Button = popupView.findViewById(R.id.buttonReset)
        buttonReset.setOnClickListener {
            startValue = 20000000
            endValue = 80000000
            rangeSlider.values = mutableListOf(startValue.toFloat(), endValue.toFloat())
        }

        val buttonOK: Button = popupView.findViewById(R.id.buttonOK)
        buttonOK.setOnClickListener {
            listHotels = listHotelsOri
            Log.d("FilterDetailFragment", "List of Hotels:")
            listHotels.forEachIndexed { index, hotel ->
                Log.d("FilterDetailFragment", "Hotel ${index + 1}: ${hotel.name}, Point: ${hotel.point}")
            }

            Log.d("FilterDetailFragment", "List of Hotels:")
            listHotelsOri.forEachIndexed { index, hotel ->
                Log.d("FilterDetailFragment", "Hotel ${index + 1}: ${hotel.name}, Point: ${hotel.point}")
            }


            listHotels = listHotels.filter { hotel ->
                val money = hotel.money ?: Int.MAX_VALUE
                money in startValue..endValue
            }

            Log.d("FilterDetailFragment", "List of Hotels:")
            listHotels.forEachIndexed { index, hotel ->
                Log.d("FilterDetailFragment", "Hotel ${index + 1}: ${hotel.name}, Point: ${hotel.point}")
            }
            rootView.alpha = 1.0f
            adapter.updateDataGradually(listHotels)
            popupWindow.dismiss()

        }

        popupWindow.setOnDismissListener {
            // Xử lý khi PopupWindow bị đóng
            rootView.alpha = 1.0f
        }
    }

    private fun showPopupSort() {
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.custom_sort, null)

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
        val closeButton = popupView.findViewById<Button>(R.id.backToFilter)
        closeButton.setOnClickListener {
            rootView.alpha = 1.0f
            popupWindow.dismiss() // Đóng PopupWindow khi nhấn nút đóng

        }

        val priceLowTextView = popupView.findViewById<TextView>(R.id.Price_low)
        val checkmarkLowestImageView = popupView.findViewById<ImageView>(R.id.checkmark_lowest)
        val priceHighTextView = popupView.findViewById<TextView>(R.id.Price_high)
        val checkmarkHighestImageView = popupView.findViewById<ImageView>(R.id.checkmark_highest)
        val commentTextView = popupView.findViewById<TextView>(R.id.comment)
        val checkmarkCommentImageView = popupView.findViewById<ImageView>(R.id.checkmark_cmt)
        val fitTextView = popupView.findViewById<TextView>(R.id.fit)
        val checkmarkFitImageView = popupView.findViewById<ImageView>(R.id.checkmark_fit)


        if (isFitTextViewSelected) {
            if(prevPriceLowTextColor == 1){
                priceLowTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            }
            else{
                priceLowTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.Blacki))
            }
            checkmarkLowestImageView.visibility = prevLowestCheckmarkVisibility

            if(prevPriceHighTextColor == 1){
                priceHighTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            }
            else{
                priceHighTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.Blacki))
            }
            checkmarkHighestImageView.visibility = prevHighestCheckmarkVisibility

            if(prevCommentTextColor == 1){
                commentTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            }
            else{
                commentTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.Blacki))
            }
            checkmarkCommentImageView.visibility = prevCommentCheckmarkVisibility

            if(prevFitTextColor == 1){
                fitTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            }
            else{
                fitTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.Blacki))
            }
            checkmarkFitImageView.visibility = prevFitCheckmarkVisibility
        }




        priceLowTextView.setOnClickListener {

            // Lưu trạng thái hiện tại của các thành phần
            isFitTextViewSelected = true
            prevPriceLowTextColor = 1
            prevPriceHighTextColor = 0
            prevCommentTextColor = 0
            prevFitTextColor = 0
            prevLowestCheckmarkVisibility = View.VISIBLE
            prevHighestCheckmarkVisibility = View.INVISIBLE
            prevCommentCheckmarkVisibility = View.INVISIBLE
            prevFitCheckmarkVisibility = View.INVISIBLE



            listHotels.forEachIndexed { index, hotel ->
                Log.d("Sort", "Hotel ${index + 1}: ${hotel.name}, Point: ${hotel.point}")
            }
            listHotels = listHotels.sortedBy { it.money }
            Log.d("Sort", "List of HotelsSort:")
            listHotels.forEachIndexed { index, hotel ->
                Log.d("Sort", "Hotel ${index + 1}: ${hotel.name}, Point: ${hotel.point}")
            }
            rootView.alpha = 1.0f
            adapter.updateDataGradually(listHotels)
            popupWindow.dismiss()

        }

        priceHighTextView.setOnClickListener {

            // Lưu trạng thái hiện tại của các thành phần
            isFitTextViewSelected = true
            prevPriceLowTextColor = 0
            prevPriceHighTextColor = 1
            prevCommentTextColor = 0
            prevFitTextColor = 0
            prevLowestCheckmarkVisibility = View.INVISIBLE
            prevHighestCheckmarkVisibility = View.VISIBLE
            prevCommentCheckmarkVisibility = View.INVISIBLE
            prevFitCheckmarkVisibility = View.INVISIBLE

            listHotels.forEachIndexed { index, hotel ->
                Log.d("Sort", "Hotel ${index + 1}: ${hotel.name}, Point: ${hotel.point}")
            }
            listHotels = listHotels.sortedByDescending { it.money }
            Log.d("Sort", "List of HotelsSort:")
            listHotels.forEachIndexed { index, hotel ->
                Log.d("Sort", "Hotel ${index + 1}: ${hotel.name}, Point: ${hotel.point}")
            }
            rootView.alpha = 1.0f
            adapter.updateDataGradually(listHotels)
            popupWindow.dismiss()
        }

        commentTextView.setOnClickListener {
            // Lưu trạng thái hiện tại của các thành phần
            isFitTextViewSelected = true
            prevPriceLowTextColor = 0
            prevPriceHighTextColor = 0
            prevCommentTextColor = 1
            prevFitTextColor = 0
            prevLowestCheckmarkVisibility = View.INVISIBLE
            prevHighestCheckmarkVisibility = View.INVISIBLE
            prevCommentCheckmarkVisibility = View.VISIBLE
            prevFitCheckmarkVisibility = View.INVISIBLE

            listHotels = listHotels.sortedByDescending { hotel -> hotel.point }
            rootView.alpha = 1.0f
            adapter.updateDataGradually(listHotels)
            popupWindow.dismiss()
        }

        fitTextView.setOnClickListener {
            // Lưu trạng thái hiện tại của các thành phần
            isFitTextViewSelected = true
            prevPriceLowTextColor = 0
            prevPriceHighTextColor = 0
            prevCommentTextColor = 0
            prevFitTextColor = 1
            prevLowestCheckmarkVisibility = View.INVISIBLE
            prevHighestCheckmarkVisibility = View.INVISIBLE
            prevCommentCheckmarkVisibility = View.INVISIBLE
            prevFitCheckmarkVisibility = View.VISIBLE

            listHotels = listHotelsOri
            rootView.alpha = 1.0f
            adapter.updateDataGradually(listHotels)
            popupWindow.dismiss()
        }

        popupWindow.setOnDismissListener {
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
         * @return A new instance of fragment MyRoom.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, idUser: String) =
            Filter(idUser).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}