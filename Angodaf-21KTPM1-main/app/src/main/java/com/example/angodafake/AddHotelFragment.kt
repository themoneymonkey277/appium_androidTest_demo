package com.example.angodafake

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import com.example.angodafake.Utilities.UserUtils
import com.example.angodafake.db.User
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.text.SimpleDateFormat
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [AddHotelFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddHotelFragment(private var idUser: String) : Fragment() {
    // TODO: Rename and change types of parameters
    private var fromFrag: String? = null

    private lateinit var btn_back: ImageButton
    private lateinit var btn_next: Button

    private lateinit var lHotelName: TextInputLayout
    private lateinit var et_hotelName: TextInputEditText
    private lateinit var lCity: TextInputLayout
    private lateinit var actvCity: MaterialAutoCompleteTextView
    private lateinit var lLocationDetail: TextInputLayout
    private lateinit var et_locationDetail: TextInputEditText
    private lateinit var lLongitude: TextInputLayout
    private lateinit var et_longitude: TextInputEditText
    private lateinit var lLatitude: TextInputLayout
    private lateinit var et_latitude: TextInputEditText
    private lateinit var ratingBar: RatingBar
    private lateinit var tv_oneStar: TextView
    private lateinit var tv_twoStar: TextView
    private lateinit var tv_threeStar: TextView
    private lateinit var tv_fourStar: TextView
    private lateinit var tv_fiveStar: TextView
    private lateinit var lPhoneN: TextInputLayout
    private lateinit var et_phoneN: TextInputEditText
    private lateinit var lDescription : TextInputLayout
    private lateinit var et_description : TextInputEditText
    private lateinit var lConveniences : TextInputLayout
    private lateinit var et_conveniences : TextInputEditText
    private lateinit var lHighlight : TextInputLayout
    private lateinit var et_highlight : TextInputEditText
    private lateinit var lCheckIn : TextInputLayout
    private lateinit var et_checkin : TextInputEditText
    private lateinit var lCheckOut : TextInputLayout
    private lateinit var et_checkout : TextInputEditText
    private lateinit var progressBar2 : ProgressBar
    private lateinit var errorText : TextView

    private lateinit var cb_momo: CheckBox
    private lateinit var tv_merchantCode : TextView
    private lateinit var lMerchantCode : TextInputLayout
    private lateinit var et_merchantCode :TextInputEditText
    private lateinit var tv_momoGuide : TextView
    private lateinit var errorText_payment : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fromFrag = it.getString("from")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_hotel, container, false)
        initUI(view)

        val cities = resources.getStringArray(R.array.cities)
        val adapterActv = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, cities)
        actvCity.setAdapter(adapterActv)

        et_hotelName.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lHotelName.error = null
            }
        }
        actvCity.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lCity.error = null
            }
        }
        et_locationDetail.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lLocationDetail.error = null
            }
        }
        et_phoneN.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lPhoneN.error = null
            }
        }
        et_description.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lDescription.error = null
            }
        }
        et_conveniences.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lConveniences.error = null
            }
        }
        et_highlight.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lHighlight.error = null
            }
        }
        et_checkin.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lCheckIn.error = null
                errorText.visibility = View.GONE
            }
        }
        et_checkout.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lCheckOut.error = null
                errorText.visibility = View.GONE
            }
        }

        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            displayStarText(rating.toInt())
        }

        et_checkin.setOnClickListener {
            et_checkin.error = null
            et_checkout.error = null
            errorText.visibility  = View.GONE
            hideKeyboard(requireActivity())
            val calendar = Calendar.getInstance()
            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                requireContext(), { _, hourOfDay, minute ->
                    // Hiển thị thời gian đã chọn
                    et_checkin.text = Editable.Factory.getInstance().newEditable(String.format("%02d:%02d", hourOfDay, minute))
                },
                hourOfDay,
                minute,
                true
            )
            timePickerDialog.show()
        }

        et_checkout.setOnClickListener {
            et_checkin.error = null
            et_checkout.error = null
            errorText.visibility  = View.GONE
            hideKeyboard(requireActivity())
            val calendar = Calendar.getInstance()
            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                requireContext(), { _, hourOfDay, minute ->
                    // Hiển thị thời gian đã chọn
                    et_checkout.text = Editable.Factory.getInstance().newEditable(String.format("%02d:%02d", hourOfDay, minute))
                },
                hourOfDay,
                minute,
                true
            )
            timePickerDialog.show()
        }

        cb_momo.setOnCheckedChangeListener { buttonView, isChecked ->
            errorText_payment.visibility = View.GONE
            if (isChecked){
                displayMomoMethod()
            } else{
                hideMomoMethod()
            }
        }

        btn_back.setOnClickListener {
            if (fromFrag == "MyProfile"){
                val mainActivity = requireActivity() as MainActivity
                mainActivity.replaceFragment(MyProfile(idUser))
            }
            else{
                val arg = Bundle()
                arg.putString("date", arguments?.getString("date"))
                arg.putString("dateType", arguments?.getString("dateType"))
                arg.putString("searchStr", arguments?.getString("searchStr"))

                val myHotel = MyHotel(idUser)
                myHotel.arguments = arg

                val mainActivity = requireActivity() as MainActivity
                mainActivity.replaceFragment(myHotel)
            }
        }

        btn_next.setOnClickListener {
            if (checkAll()){
                nextStepWithData()
            }
        }


        progressBar2.setOnClickListener {
            if (checkAll()){
                nextStepWithData()
            }
        }
        return view
    }

    private fun displayStarText(star: Int){
        tv_oneStar.visibility = View.INVISIBLE
        tv_twoStar.visibility = View.INVISIBLE
        tv_threeStar.visibility = View.INVISIBLE
        tv_fourStar.visibility = View.INVISIBLE
        tv_fiveStar.visibility = View.INVISIBLE
        when (star){
            1 -> tv_oneStar.visibility = View.VISIBLE
            2 -> tv_twoStar.visibility = View.VISIBLE
            3 -> tv_threeStar.visibility = View.VISIBLE
            4 -> tv_fourStar.visibility = View.VISIBLE
            5 -> tv_fiveStar.visibility = View.VISIBLE
        }
    }

    private fun isCheckEmpty(textInputLayout: TextInputLayout, editText: EditText, name: String) : Boolean{
        return if (editText.text.toString().trim() == ""){
            textInputLayout.error = "$name không được để trống."
            if (name == "!"){
                errorText.text = "Giờ check-in, check-out không được để trống."
                errorText.visibility = View.VISIBLE
            }
            true
        } else{
            textInputLayout.error = null
            false
        }
    }

    private fun validatePhoneNumber(): Boolean {
        return if (et_phoneN.text.toString().trim().length != 10 && et_phoneN.text.toString().trim().length != 11){
            lPhoneN.error = "Số di động không hợp lệ."
            false
        } else true
    }
    private fun validateCheckin(): Boolean{
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        if (et_checkin.text.toString() != "" && et_checkout.text.toString() != ""){
            try {
                val checkIn = timeFormat.parse(et_checkin.text.toString())
                val checkOut = timeFormat.parse(et_checkout.text.toString())

                if (checkOut.after(checkIn)) {
                    et_checkin.error = null
                    et_checkout.error = null
                    errorText.visibility  = View.GONE
                    return true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            et_checkin.error = "!"
            et_checkout.error = "!"
            errorText.text = "Giờ check-in phải sớm hơn giờ check-out."
            errorText.visibility  = View.VISIBLE
        }
        return false
    }

    private fun clearFocus(){
        et_hotelName.clearFocus()
        actvCity.clearFocus()
        et_locationDetail.clearFocus()
        et_phoneN.clearFocus()
        et_description.clearFocus()
        et_conveniences.clearFocus()
        et_highlight.clearFocus()
        et_merchantCode.clearFocus()
    }
    fun hideKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        // Kiểm tra xem view nào đang có focus
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        // Sử dụng InputMethodManager để ẩn bàn phím
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun checkAll() : Boolean{
        hideKeyboard(requireActivity())
        clearFocus()
        var check = true
        if (isCheckEmpty(lHotelName, et_hotelName, "Tên khách sạn"))
            check = false
        if (isCheckEmpty(lCity, actvCity, "Tên thành phố/ Tỉnh thành"))
            check = false
        if (isCheckEmpty(lLocationDetail, et_locationDetail, "Địa chỉ chi tiết"))
            check = false
        if (isCheckEmpty(lLongitude, et_longitude, "Kinh độ"))
            check = false
        if (isCheckEmpty(lLatitude, et_latitude, "Vĩ độ"))
            check = false
        if (isCheckEmpty(lPhoneN, et_phoneN, "Số điện thoại") || !validatePhoneNumber())
            check = false
        if (isCheckEmpty(lDescription, et_description, "Mô tả khách sạn"))
            check = false
        if (isCheckEmpty(lConveniences, et_conveniences, "Tiện nghi"))
            check = false
        if (isCheckEmpty(lHighlight, et_highlight, "Điểm nổi bật"))
            check = false
        if (isCheckEmpty(lCheckIn, et_checkin, "!") || isCheckEmpty(lCheckOut, et_checkout, "!"))
            check = false
        if (!validateCheckin())
            check = false
        if (cb_momo.isChecked){
            if (isCheckEmpty(lMerchantCode, et_merchantCode, "Mã doanh nghiệp"))
                check = false
        }
        if (!cb_momo.isChecked){
            errorText_payment.visibility = View.VISIBLE
            check = false
        }
        return check
    }

    private fun displayMomoMethod(){
        tv_merchantCode.visibility = View.VISIBLE
        lMerchantCode.visibility = View.VISIBLE
        tv_momoGuide.visibility = View.VISIBLE
    }
    private fun hideMomoMethod(){
        tv_merchantCode.visibility = View.GONE
        lMerchantCode.visibility = View.GONE
        tv_momoGuide.visibility = View.GONE
    }

    private fun nextStepWithData(){
        val arg = Bundle()

        arg.putString("from", fromFrag)
        arg.putString("hotelName", et_hotelName.text.toString().trim())
        arg.putString("city", actvCity.text.toString().trim())
        arg.putString("locationDetail", et_locationDetail.text.toString().trim())
        arg.putString("longitude", et_longitude.text.toString())
        arg.putString("latitude", et_latitude.text.toString())
        arg.putInt("star", ratingBar.rating.toInt())
        arg.putString("phoneN", et_phoneN.text.toString().trim())
        arg.putString("description", et_description.text.toString().trim())
        arg.putString("convenient", et_conveniences.text.toString().trim())
        arg.putString("highlight", et_highlight.text.toString().trim())
        arg.putString("checkin", et_checkin.text.toString().trim())
        arg.putString("checkout", et_checkout.text.toString().trim())
        arg.putString("merchantCode", et_merchantCode.text.toString().trim())

        if (arguments?.getStringArrayList("pics") != null){
            arg.putStringArrayList("pics", arguments?.getStringArrayList("pics"))
        }
        if (fromFrag == "edit"){
            arg.putString("idHotel", arguments?.getString("idHotel"))
            arg.putString("date", arguments?.getString("date"))
            arg.putString("dateType", arguments?.getString("dateType"))
            arg.putString("searchStr", arguments?.getString("searchStr"))
        }

        val addHotelImageFragment = addHotelImageFragment(idUser)
        addHotelImageFragment.arguments = arg

        val mainActivity = requireActivity() as MainActivity
        mainActivity.replaceFragment(addHotelImageFragment)
    }

    private fun initUI(view: View){
        btn_back = view.findViewById(R.id.btn_back)
        btn_next = view.findViewById(R.id.btn_next)

        lHotelName = view.findViewById(R.id.lHotelName)
        et_hotelName = lHotelName.editText as TextInputEditText
        lCity = view.findViewById(R.id.lCity)
        actvCity = view.findViewById(R.id.actvCity)
        lLocationDetail = view.findViewById(R.id.lLocationDetail)
        et_locationDetail = lLocationDetail.editText as TextInputEditText
        lLongitude = view.findViewById(R.id.lLongitude)
        et_longitude = lLongitude.editText as TextInputEditText
        lLatitude = view.findViewById(R.id.lLatitude)
        et_latitude = lLatitude.editText as TextInputEditText
        ratingBar = view.findViewById(R.id.ratingBar5)
        tv_oneStar = view.findViewById(R.id.tv_oneStar)
        tv_twoStar = view.findViewById(R.id.tv_twoStar)
        tv_threeStar = view.findViewById(R.id.tv_threeStar)
        tv_fourStar = view.findViewById(R.id.tv_fourStar)
        tv_fiveStar = view.findViewById(R.id.tv_fiveStar)
        lPhoneN = view.findViewById(R.id.lPhoneN)
        et_phoneN = lPhoneN.editText as TextInputEditText
        lDescription = view.findViewById(R.id.lDescription)
        et_description = lDescription.editText as TextInputEditText
        lConveniences = view.findViewById(R.id.lConveniences)
        et_conveniences = lConveniences.editText as TextInputEditText
        lHighlight = view.findViewById(R.id.lHighlight)
        et_highlight = lHighlight.editText as TextInputEditText
        lCheckIn = view.findViewById(R.id.lCheckIn)
        et_checkin = lCheckIn.editText as TextInputEditText
        lCheckOut = view.findViewById(R.id.lCheckOut)
        et_checkout = lCheckOut.editText as TextInputEditText
        progressBar2 = view.findViewById(R.id.progressBar2)
        errorText = view.findViewById(R.id.errorText)

        cb_momo = view.findViewById(R.id.cb_momo)
        tv_merchantCode = view.findViewById(R.id.tv_merchantCode)
        lMerchantCode = view.findViewById(R.id.lMerchantCode)
        et_merchantCode  = lMerchantCode.editText as TextInputEditText
        tv_momoGuide = view.findViewById(R.id.tv_momoGuide)
        errorText_payment = view.findViewById(R.id.errorText_payment)

        val hotelName = arguments?.getString("hotelName")
        if (hotelName != null) {
            et_hotelName.text = Editable.Factory.getInstance().newEditable(arguments?.getString("hotelName"))
            actvCity.text = Editable.Factory.getInstance().newEditable(arguments?.getString("city"))
            et_locationDetail.text = Editable.Factory.getInstance().newEditable(arguments?.getString("locationDetail"))
            et_longitude.text = Editable.Factory.getInstance().newEditable(arguments?.getString("longitude"))
            et_latitude.text = Editable.Factory.getInstance().newEditable(arguments?.getString("latitude"))
            val star = arguments?.getInt("star")
            if (star != null) {
                ratingBar.rating = star.toFloat()
                displayStarText(star)
            }

            et_phoneN.text = Editable.Factory.getInstance().newEditable(arguments?.getString("phoneN"))
            et_description.text = Editable.Factory.getInstance().newEditable(arguments?.getString("description"))
            et_conveniences.text = Editable.Factory.getInstance().newEditable(arguments?.getString("convenient"))
            et_highlight.text = Editable.Factory.getInstance().newEditable(arguments?.getString("highlight"))
            et_checkin.text = Editable.Factory.getInstance().newEditable(arguments?.getString("checkin"))
            et_checkout.text = Editable.Factory.getInstance().newEditable(arguments?.getString("checkout"))

            val merchantCode = arguments?.getString("merchantCode")
            if (merchantCode != null){
                cb_momo.isChecked = true
                displayMomoMethod()
                et_merchantCode.text = Editable.Factory.getInstance().newEditable(arguments?.getString("merchantCode"))
            }
        }

        if (fromFrag == "edit"){
            view.findViewById<TextView>(R.id.addHotel_title).text = "Sửa thông tin khách sạn"
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddHotelFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(fromFrag: String, idUser: String) =
            AddHotelFragment(idUser).apply {
                arguments = Bundle().apply {
                    putString("from", fromFrag)
                }
            }
    }
}