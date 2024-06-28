package com.example.angodafake

import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.Adapter.HotelManageAdapter
import com.example.angodafake.Adapter.OnRoomDeleteListener
import com.example.angodafake.Adapter.RoomManageAdapter
import com.example.angodafake.Utilities.HotelUtils
import com.example.angodafake.Utilities.PictureUtils
import com.example.angodafake.Utilities.PurchaseUtils
import com.example.angodafake.Utilities.RoomUtils
import com.example.angodafake.db.Hotel
import com.example.angodafake.db.Rooms
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [ManageRoomsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ManageRoomsFragment(private var idUser: String) : Fragment(), OnRoomDeleteListener {
    // TODO: Rename and change types of parameters
    private lateinit var btn_back : ImageButton
    private lateinit var addLL : View
    private lateinit var addFab : FloatingActionButton
    private lateinit var billLL : View
    private lateinit var billFab : FloatingActionButton
    private lateinit var menuFab : FloatingActionButton
    private var rotate = false

    private lateinit var filterSpinner: Spinner
    private lateinit var lDate : TextInputLayout
    private lateinit var et_date : TextInputEditText
    private lateinit var recyclerView : RecyclerView
    private lateinit var room_list : ArrayList<Rooms>
    private lateinit var date: String
    private var searchStr= ""
    private var searchRoomStr= ""
    private var dateType = 0
    private lateinit var idHotel: String
    private lateinit var adapter: RoomManageAdapter

    private lateinit var lRoomType: TextInputLayout
    private lateinit var et_roomType: TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_manage_rooms, container, false)
        initUI(view)
        initShowout(addLL)
        initShowout(billLL)

        room_list = ArrayList()
        if (et_date.text.toString() == ""){
            // Lấy ngày hiện tại
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val formattedDay = if (day < 10) "0$day" else "$day"
            val formattedMonth = if (month < 10) "0$month" else "$month"
            et_date.text = Editable.Factory.getInstance().newEditable("$formattedDay/$formattedMonth/$year")
        }

        if (arguments?.getString("date") != null){
            et_date.text = Editable.Factory.getInstance().newEditable(arguments?.getString("date"))
        }
        if (arguments?.getString("searchStr") != null){
           searchStr = arguments?.getString("searchStr")!!
        }

        date = et_date.text.toString()
        dateType = 0
        adapter = RoomManageAdapter(requireContext(), room_list, date, idUser, dateType, searchRoomStr, searchStr)
        adapter.setOnDeleteListener(this)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        val items = listOf("Ngày đặt phòng", "Ngày đến - Ngày đi")
        val spAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinner.adapter = spAdapter

        RoomUtils.getRoomByHotelID(idHotel){
            room_list.clear()
            room_list.addAll(it)
            adapter.notifyDataSetChanged()
        }

        if (arguments?.getString("dateType") != null){
            dateType = arguments?.getString("dateType")!!.toInt()
            filterSpinner.setSelection(dateType)
            adapter.dateType = dateType
            adapter.notifyDataSetChanged()
        }
        if (arguments?.getString("searchRoomStr") != null){
            et_roomType.text = Editable.Factory.getInstance().newEditable(arguments?.getString("searchRoomStr"))
            RoomUtils.getRoomsByType(idHotel, et_roomType.text.toString()){roomL->
                room_list.clear()
                room_list.addAll(roomL)
                adapter.searchRoomStr = arguments?.getString("searchRoomStr")!!
                adapter.notifyDataSetChanged()
            }
        }


        et_roomType.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Không cần thực hiện gì trong trường hợp này
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Không cần thực hiện gì trong trường hợp này
            }

            override fun afterTextChanged(s: Editable?) {
                // Phương thức này được gọi sau khi văn bản đã thay đổi
                val enteredText = s.toString()
                RoomUtils.getRoomsByType(idHotel, enteredText){roomL->
                    room_list.clear()
                    room_list.addAll(roomL)
                    adapter.searchRoomStr = enteredText
                    adapter.notifyDataSetChanged()
                }
            }
        })

        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = items[position]
                if (selectedItem == "Ngày đặt phòng"){
                    adapter.dateType = 0
                    adapter.notifyDataSetChanged()
                } else{
                    adapter.dateType = 1
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Xử lý khi không có mục nào được chọn
            }
        }


        et_date.setOnClickListener {
            it.clearFocus()
            val year: Int
            val month: Int
            val day: Int
            if (et_date.text.toString() == ""){
                // Lấy ngày hiện tại
                val calendar = Calendar.getInstance()
                year = calendar.get(Calendar.YEAR)
                month = calendar.get(Calendar.MONTH)
                day = calendar.get(Calendar.DAY_OF_MONTH)
            }
            else{
                val dateParts  = et_date.text.toString().split("/")
                year = dateParts[2].toInt()
                month = dateParts[1].toInt() - 1
                day = dateParts[0].toInt()
            }
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                    // Xử lý khi người dùng chọn ngày
                    val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                    et_date.setText(selectedDate)
                    adapter.date = et_date.text.toString()
                    adapter.notifyDataSetChanged()
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        addFab.setOnClickListener {
            val arg = Bundle()
            arg.putString("from", "manageRoom")
            arg.putString("idHotel", idHotel)
            arg.putString("date", et_date.text.toString())

            val addRoomFrag = AddRoomFragment(idHotel, idUser)
            addRoomFrag.arguments = arg

            val mainActivity = requireActivity() as MainActivity
            mainActivity.replaceFragment(addRoomFrag)
        }

        billFab.setOnClickListener {
            PurchaseUtils.getAllPurchasesByHotelID(idHotel){
                val list = ArrayList<String>()
                for (bill in it){
                    list.add(bill.ID!!)
                }

                val arg = Bundle()
                arg.putString("from", "edit_room")
                arg.putString("idHotel", idHotel)
                arg.putString("date", et_date.text.toString())
                arg.putString("dateType", dateType.toString())
                arg.putString("searchStr", searchStr)
                arg.putString("searchRoomStr", searchRoomStr)
                arg.putStringArrayList("bills", list)

                val billFrag = BillFragment(idUser)
                billFrag.arguments = arg

                val mainActivity = requireActivity() as MainActivity
                mainActivity.replaceFragment(billFrag)
            }
        }

        menuFab.setOnClickListener {
            toggleFabMode(it)
        }

        btn_back.setOnClickListener {
            val arg = Bundle()
            arg.putString("date", adapter.date)
            arg.putString("dateType", adapter.dateType.toString())
            arg.putString("searchStr", adapter.searchStr)

            val myHotel = MyHotel(idUser)
            myHotel.arguments = arg
            val mainActivity = requireActivity() as MainActivity
            mainActivity.replaceFragment(myHotel)
        }

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRoomDeleted(room: Rooms) {
        Log.d("checkDelete", "vao")

        // Xử lý sự kiện khi người dùng xóa phòng
        PurchaseUtils.getPurchaseByRoom(room.ID_Hotel!!, room.ID!!, SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().time)){
            if (it){
                RoomUtils.deleteRoom(room.ID_Hotel!!, room.ID!!)
                PictureUtils.deleteRoomPictues(room.ID_Hotel!!, room.ID!!)
                PurchaseUtils.deletePurchaseByRoomID(room.ID_Hotel!!, room.ID!!)
                room_list.remove(room)
                adapter.notifyDataSetChanged()
                showSuccessSnackBar("Đã xóa phòng.", requireView())
            } else{
                Log.d("checkDelete", it.toString())
                showSnackBar("Xóa thất bại! Phòng này hiện vẫn đang được giao dịch. Xin kiểm tra lại hóa đơn.", requireView())
            }
        }
    }
    private fun toggleFabMode(v: View) {
        rotate = rotateFab(v, !rotate)
        if (rotate){
            showIn(addLL)
            showIn(billLL)
            requireView().findViewById<View>(R.id.backgroundOverlay).visibility = View.VISIBLE
        } else{
            showOut(addLL)
            showOut(billLL)
            requireView().findViewById<View>(R.id.backgroundOverlay).visibility = View.GONE
        }
    }

    private fun initShowout(v: View){
        v.apply {
            visibility = View.GONE
            translationY = height.toFloat()
            alpha = 0f
        }
    }
    private fun showOut(view: View) {
        view.apply {
            visibility = View.VISIBLE
            alpha = 1f
            translationY = 0f
            animate()
                .setDuration(200)
                .translationY(height.toFloat())
                .setListener(object : AnimatorListenerAdapter(){
                })
                .alpha(0f)
                .start()
        }
    }
    private fun showIn(view: View) {
        view.apply {
            visibility = View.VISIBLE
            alpha = 0f
            translationY = height.toFloat()
            animate()
                .setDuration(200)
                .translationY(0f)
                .setListener(object : AnimatorListenerAdapter(){})
                .alpha(1f)
                .start()
        }
    }

    private fun rotateFab(v: View, rotate: Boolean): Boolean {
        v.animate()
            .setDuration(200)
            .setListener(object : AnimatorListenerAdapter(){})
            .rotation(if (rotate) 180f else 0f)
        return rotate
    }

    private fun showSnackBar(msg: String, view: View) {
        val snackbar = Snackbar.make(view.rootView, msg, Snackbar.LENGTH_LONG)
        // Đổi màu background của Snackbar
        snackbar.view.backgroundTintList = ColorStateList.valueOf(Color.RED)
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }
    private fun showSuccessSnackBar(msg: String, view: View) {
        val snackbar = Snackbar.make(view.rootView, msg, Snackbar.LENGTH_LONG)
        snackbar.view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3193FF"))
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initUI(view: View){
        addLL = view.findViewById(R.id.addLL)
        addFab = view.findViewById(R.id.addFab)
        billLL = view.findViewById(R.id.billLL)
        billFab = view.findViewById(R.id.billFab)
        menuFab = view.findViewById(R.id.menuFab)
        btn_back = view.findViewById(R.id.btn_back)

        lDate = view.findViewById(R.id.lDate)
        et_date = lDate.editText as TextInputEditText

        idHotel = arguments?.getString("idHotel")!!

        lRoomType = view.findViewById(R.id.lRoomType)
        et_roomType = lRoomType.editText as TextInputEditText
        recyclerView = view.findViewById(R.id.recyclerView)
        filterSpinner = view.findViewById(R.id.filterSpinner)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ManageRoomsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(idUser: String) =
            ManageRoomsFragment(idUser).apply {
                arguments = Bundle().apply {
                }
            }
    }
}