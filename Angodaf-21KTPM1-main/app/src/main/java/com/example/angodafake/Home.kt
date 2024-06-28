package com.example.angodafake

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.Adapter.HotelAdapter
import com.example.angodafake.Utilities.RoomUtils
import com.example.angodafake.db.Hotel
import com.example.angodafake.db.Rooms
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Date

import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home(private var idUser: String,  private val nameHotel: String?) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var homeLayout: View

    private lateinit var listHotels: MutableList<Hotel>
    private lateinit var listHotelsFilter: MutableList<Hotel>
    private lateinit var database: DatabaseReference
    private lateinit var checkIn : TextInputLayout
    private lateinit var checkOut : TextInputLayout
    private lateinit var checkIn_Text : TextInputEditText
    private lateinit var checkOut_Text : TextInputEditText
    private lateinit var popupWindow: PopupWindow
    var inforCount: String? = null
    private lateinit var infor: TextView
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database.reference
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeLayout = inflater.inflate(R.layout.fragment_home, container, false)

        setupViews(homeLayout)
        return homeLayout
    }

    private fun setupViews(view: View) {
        val searchEditText = view.findViewById<EditText>(R.id.nameHotelSearch)
        if (nameHotel != "") {
            val editableText = Editable.Factory.getInstance().newEditable(nameHotel)
            searchEditText.text = editableText
        }
        val findButton = view.findViewById<Button>(R.id.findButton)
        infor = view.findViewById<TextView>(R.id.infor)

        checkIn = view.findViewById(R.id.checkIn)
        checkIn_Text = checkIn.editText as TextInputEditText
        checkOut = view.findViewById(R.id.checkOut)
        checkOut_Text = checkOut.editText as TextInputEditText

        // Lấy ngày hiện tại
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDateString = dateFormat.format(currentDate)

        // Lấy ngày hôm sau
        val nextDateCalendar = Calendar.getInstance()
        nextDateCalendar.add(Calendar.DAY_OF_MONTH, 1) // Thêm 1 ngày vào ngày hiện tại
        val nextDate = nextDateCalendar.time
        val nextDateString = dateFormat.format(nextDate)

        checkIn_Text.setText(currentDateString)
        checkOut_Text.setText(nextDateString)

        if (!::listHotels.isInitialized) {
            listHotels = mutableListOf()
        }

        val hotelsRef = database.child("hotels")

        hotelsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listHotels.clear()
                for (hotelSnapshot in dataSnapshot.children) {
                    //Log.d("hotelSnapshot", hotelSnapshot.toString())
                    var hotel = hotelSnapshot.getValue(Hotel::class.java)
                    hotel?.ID = hotelSnapshot.key
                    //Log.d("hotel", hotel.toString())
                    hotel?.let { listHotels.add(it) }
                }

                //checkIn
                checkIn_Text.setOnClickListener{
                    val year: Int
                    val month: Int
                    val day: Int
                    if (checkIn_Text.text.toString() == ""){
                        // Lấy ngày hiện tại
                        val calendar = Calendar.getInstance()
                        year = calendar.get(Calendar.YEAR)
                        month = calendar.get(Calendar.MONTH)
                        day = calendar.get(Calendar.DAY_OF_MONTH)
                    }
                    else{
                        val date = checkIn_Text.text.toString().split("/")
                        year = date[2].toInt()
                        month = date[1].toInt() - 1
                        day = date[0].toInt()
                    }
                    val datePickerDialog = DatePickerDialog(
                        requireContext(),
                        { view: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                            // Xử lý khi người dùng chọn ngày
                            val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                            val checkOutText = checkOut_Text.text.toString()

                            val checkInDate: Date? = dateFormat.parse(selectedDate)

                            if (checkOutText.isNotEmpty()) {
                                val checkOutDate: Date? = dateFormat.parse(checkOutText)
                                // Kiểm tra xem checkOutDate có phải là tương lai của checkInDate không
                                if (checkOutDate != null && checkInDate != null) {
                                    val isFuture = checkOutDate.after(checkInDate)
                                    if (isFuture) {
                                        checkIn_Text.setText(selectedDate)
                                        checkIn_Text.error = null
                                    } else {
                                        checkIn_Text.error = "Vui lòng chọn lại ngày hợp lí"
                                        warning("Warning", "Vui lòng chọn lại ngày hợp lí")
                                    }
                                }
                            } else {
                                checkIn_Text.setText(selectedDate)
                            }
                        },
                        year, month, day
                    )
                    datePickerDialog.show()
                }

                //checkOut
                checkOut_Text.setOnClickListener{
                    val year: Int
                    val month: Int
                    val day: Int
                    if (checkOut_Text.text.toString() == ""){
                        // Lấy ngày hiện tại
                        val calendar = Calendar.getInstance()
                        year = calendar.get(Calendar.YEAR)
                        month = calendar.get(Calendar.MONTH)
                        day = calendar.get(Calendar.DAY_OF_MONTH)
                    }
                    else{
                        val date = checkOut_Text.text.toString().split("/")
                        year = date[2].toInt()
                        month = date[1].toInt() - 1
                        day = date[0].toInt()
                    }
                    val datePickerDialog = DatePickerDialog(
                        requireContext(),
                        { view: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                            // Xử lý khi người dùng chọn ngày
                            val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                            val checkInText = checkIn_Text.text.toString()


                            val checkOutDate: Date? = dateFormat.parse(selectedDate)

                            if (checkInText.isNotEmpty()) {
                                val checkInDate: Date? = dateFormat.parse(checkInText)
                                // Kiểm tra xem checkOutDate có phải là tương lai của checkInDate không
                                if (checkOutDate != null && checkInDate != null) {
                                    val isFuture = checkOutDate.after(checkInDate)
                                    if (isFuture) {
                                        checkOut_Text.setText(selectedDate)
                                        checkOut_Text.error = null
                                    } else {
                                        checkOut_Text.error = "Vui lòng chọn lại ngày hợp lí"
                                        warning("Warning", "Vui lòng chọn lại ngày hợp lí")
                                    }
                                }
                            } else {
                                checkOut_Text.setText(selectedDate)
                            }
                        },
                        year, month, day
                    )
                    datePickerDialog.show()
                }

                // count
                infor.setOnClickListener {
                    showPopup()
                }

                // Tìm kiếm
                findButton.setOnClickListener {
                    val searchText = searchEditText.text.toString()
                    var list = filterHotels(searchText)
                    val inputString = infor.text.toString()
                    val parts = inputString.split(" ")

                    val numberOfRooms = parts[0].toIntOrNull() ?: 0
                    val numberOfGuests = parts[3].toIntOrNull() ?: 0

                    val checkInText = checkIn_Text.text.toString()
                    val checkOutText = checkOut_Text.text.toString()

                    RoomUtils.getRoomsFromDatabase() { fetchedRoomList ->
                        list = filterHotelsCount(fetchedRoomList, list, numberOfGuests, numberOfRooms)

                        val args = Bundle()
                        val hotelIds = list.map { it.ID }
                        println("Hotel IDs: ${hotelIds.joinToString()}")
                        args.putStringArray("hotelIds", hotelIds.toTypedArray())
                        args.putStringArray("saveIds", hotelIds.toTypedArray())
                        args.putString("searchText", searchText)
                        args.putString("checkIn", checkInText)
                        args.putString("checkOut", checkOutText)
                        args.putInt("numberOfRooms", numberOfRooms)
                        args.putInt("numberOfGuests", numberOfGuests)

                        // Khởi tạo Fragment Filter và đính kèm Bundle
                        val filterFragment = Filter(idUser)

                        filterFragment.arguments = args

                        // Thay thế Fragment hiện tại bằng Fragment Filter
                        val fragmentManager = requireActivity().supportFragmentManager
                        fragmentManager.beginTransaction()
                            .replace(R.id.frameLayout, filterFragment)
                            .addToBackStack(null)  // Để quay lại Fragment Home khi ấn nút Back
                            .commit()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
                println("Failed to read value: ${databaseError.toException()}")
                //listHotels = emptyList() // Trả về danh sách rỗng khi có lỗi
            }
        })
    }

    private fun showPopup() {
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_find, null)

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

        val plus_room = popupView.findViewById<ImageView>(R.id.plus_room)
        val minus_room = popupView.findViewById<ImageView>(R.id.minus_room)
        val minus_person = popupView.findViewById<ImageView>(R.id.minus_person)
        val plus_person = popupView.findViewById<ImageView>(R.id.plus_person)
        val count_room = popupView.findViewById<TextView>(R.id.count_room)
        val count_person = popupView.findViewById<TextView>(R.id.count_person)


        plus_room.setOnClickListener {
            val currentCount = count_room.text.toString().toIntOrNull() ?: 0
            count_room.text = (currentCount + 1).toString()
        }

        minus_room.setOnClickListener {
            val currentCount = count_room.text.toString().toIntOrNull() ?: 0
            val newCount = currentCount - 1
            val finalCount = if (newCount < 1) 1 else newCount
            count_room.text = finalCount.toString()
        }

        plus_person.setOnClickListener {
            val currentCount = count_person.text.toString().toIntOrNull() ?: 0
            count_person.text = (currentCount + 1).toString()
        }

        minus_person.setOnClickListener {
            val currentCount = count_person.text.toString().toIntOrNull() ?: 0
            val newCount = currentCount - 1
            val finalCount = if (newCount < 1) 1 else newCount
            count_person.text = finalCount.toString()
        }


        val buttonOK: Button = popupView.findViewById(R.id.findButton)
        buttonOK.setOnClickListener {
            inforCount = count_room.text.toString() + " phòng - " + count_person.text.toString() + " người"
            rootView.alpha = 1.0f
            popupWindow.dismiss()

        }

        popupWindow.setOnDismissListener {
            // Xử lý khi PopupWindow bị đóng
            if(inforCount != null){
                infor.text = inforCount.toString()

            }
            rootView.alpha = 1.0f
        }
    }

    private fun warning(title: String, message: String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setPositiveButton("EXIT") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, idUser: String, nameHotel: String?) =
            Home(idUser, nameHotel).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun filterHotels(query: String): List<Hotel> {
        val filteredList = mutableListOf<Hotel>()
        for (hotel in listHotels) {
            val hotelNameLower = hotel.name?.toLowerCase(Locale.getDefault())
            val locationDetailLower = hotel.locationDetail?.toLowerCase(Locale.getDefault())

            // Kiểm tra xem query có tồn tại trong tên khách sạn hoặc chi tiết địa điểm không
            if (locationDetailLower != null) {
                if (hotelNameLower != null) {
                    if (hotelNameLower.contains(query.toLowerCase(Locale.getDefault())) ||
                        locationDetailLower.contains(query.toLowerCase(Locale.getDefault()))
                    ) {
                        filteredList.add(hotel)
                    }
                }
            }
        }
        return filteredList
    }

    fun filterHotelsCount(rooms: List<Rooms>, hotels: List<Hotel>, numberOfGuests: Int, numberOfRooms: Int): List<Hotel> {
        val filteredHotels = mutableListOf<Hotel>()

        for (hotel in hotels) {
            var suitableRoomFound = false

            for (room in rooms.filter { it.ID_Hotel == hotel.ID }) {
                if (room.capacity!! >= numberOfGuests && (room.quantity!! - room.available!!) >= numberOfRooms) {
                    suitableRoomFound = true
                    break
                }
            }

            if (suitableRoomFound) {
                filteredHotels.add(hotel)
            }
        }

        return filteredHotels
    }

}