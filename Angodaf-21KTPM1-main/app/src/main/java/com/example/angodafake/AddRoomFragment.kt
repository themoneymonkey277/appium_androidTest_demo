package com.example.angodafake

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [AddRoomFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddRoomFragment(private var idHotel: String, private var idUser: String) : Fragment() {
    // TODO: Rename and change types of parameters
    private var fromFrag: String? = null

    private lateinit var lRoomType: TextInputLayout
    private lateinit var et_roomType: TextInputEditText
    private lateinit var lAcreage: TextInputLayout
    private lateinit var et_acreage: TextInputEditText
    private lateinit var lDirection: TextInputLayout
    private lateinit var et_direction: TextInputEditText
    private lateinit var lBenefit: TextInputLayout
    private lateinit var et_benefit: TextInputEditText
    private lateinit var lSingleBed: TextInputLayout
    private lateinit var et_singleBed: TextInputEditText
    private lateinit var lDoubleBed: TextInputLayout
    private lateinit var et_doubleBed: TextInputEditText
    private lateinit var lPrice: TextInputLayout
    private lateinit var et_price: TextInputEditText
    private lateinit var lQuantity: TextInputLayout
    private lateinit var et_quantity: TextInputEditText

    private lateinit var btn_back: ImageButton
    private lateinit var btn_next: Button
    private lateinit var progressBar2: ProgressBar

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
        val view = inflater.inflate(R.layout.fragment_add_room, container, false)
        initUI(view)

        et_roomType.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lRoomType.error = null
            }
        }
        et_acreage.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lAcreage.error = null
            }
        }
        et_direction.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lDirection.error = null
            }
        }
        et_benefit.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lBenefit.error = null
            }
        }
        et_singleBed.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lSingleBed.error = null
            }
        }
        et_doubleBed.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lDoubleBed.error = null
            }
        }
        et_price.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lPrice.error = null
            }
        }
        et_quantity.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lQuantity.error = null
            }
        }

        btn_back.setOnClickListener {
            if (fromFrag == "edit" || fromFrag == "manageRoom"){
                val arg = Bundle()
                arg.putString("date", arguments?.getString("date"))
                arg.putString("idHotel", arguments?.getString("idHotel"))
                arg.putString("dateType", arguments?.getString("dateType"))
                arg.putString("searchStr", arguments?.getString("searchStr"))
                arg.putString("searchRoomStr", arguments?.getString("searchRoomStr"))

                val manageRoomFrg = ManageRoomsFragment(idUser)
                manageRoomFrg.arguments = arg

                val mainActivity = requireActivity() as MainActivity
                mainActivity.replaceFragment(manageRoomFrg)
            }
        }

        btn_next.setOnClickListener {
            if (checkAll()){
                nextStepWithData()
            }
        }

        progressBar2.setOnClickListener{
            if (checkAll()){
                nextStepWithData()
            }
        }

        return view
    }

    private fun isCheckEmpty(textInputLayout: TextInputLayout, editText: EditText, name: String) : Boolean{
        return if (editText.text.toString().trim() == ""){
            textInputLayout.error = "$name không được để trống."
            true
        } else{
            textInputLayout.error = null
            false
        }
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
        if (isCheckEmpty(lRoomType, et_roomType, "Loại phòng"))
            check = false
        if (isCheckEmpty(lAcreage, et_acreage, "Diện tích"))
            check = false
        if (isCheckEmpty(lDirection, et_direction, "Hướng "))
            check = false
        if (isCheckEmpty(lBenefit, et_benefit, "Lợi ích"))
            check = false
        if (isCheckEmpty(lSingleBed, et_singleBed, "Số giường đơn"))
            check = false
        if (isCheckEmpty(lDoubleBed, et_doubleBed, "Số giường đôi"))
            check = false
        if (isCheckEmpty(lPrice, et_price, "Giá phòng"))
            check = false
        if (isCheckEmpty(lQuantity, et_quantity, "Số lượng phòng"))
            check = false
        return check
    }
    private fun clearFocus(){
        et_roomType.clearFocus()
        et_acreage.clearFocus()
        et_direction.clearFocus()
        et_benefit.clearFocus()
        et_singleBed.clearFocus()
        et_doubleBed.clearFocus()
        et_price.clearFocus()
        et_quantity.clearFocus()
    }

    private fun nextStepWithData(){
        val arg = Bundle()

        arg.putString("from", fromFrag)
        arg.putString("roomType", et_roomType.text.toString().trim())
        arg.putString("acreage", et_acreage.text.toString().trim())
        arg.putString("direction", et_direction.text.toString().trim())
        arg.putString("benefit", et_benefit.text.toString().trim())
        arg.putString("singleBed", et_singleBed.text.toString().trim())
        arg.putString("doubleBed", et_doubleBed.text.toString().trim())
        arg.putString("price", et_price.text.toString().trim())
        arg.putString("quantity", et_quantity.text.toString().trim())

        if (fromFrag == "edit"){
            arg.putString("idHotel", arguments?.getString("idHotel"))
            arg.putString("idRoom", arguments?.getString("idRoom"))
            arg.putString("date", arguments?.getString("date"))
            arg.putString("dateType", arguments?.getString("dateType"))
            arg.putString("searchStr", arguments?.getString("searchStr"))
            arg.putString("searchRoomStr", arguments?.getString("searchRoomStr"))
        } else if (fromFrag == "manageRoom"){
            arg.putString("idHotel", arguments?.getString("idHotel"))
            arg.putString("date", arguments?.getString("date"))
            arg.putString("dateType", arguments?.getString("dateType"))
            arg.putString("searchStr", arguments?.getString("searchStr"))
            arg.putString("searchRoomStr", arguments?.getString("searchRoomStr"))
        }

        if (arguments?.getStringArrayList("pics") != null){
            arg.putStringArrayList("pics", arguments?.getStringArrayList("pics"))
        }

        val addRoomImageFragment = AddRoomImageFragment(idHotel, idUser)
        addRoomImageFragment.arguments = arg

        val mainActivity = requireActivity() as MainActivity
        mainActivity.replaceFragment(addRoomImageFragment)
    }

    private fun initUI(view: View){
        lRoomType = view.findViewById(R.id.lRoomType)
        et_roomType = lRoomType.editText as TextInputEditText
        lAcreage = view.findViewById(R.id.lAcreage)
        et_acreage = lAcreage.editText as TextInputEditText
        lDirection = view.findViewById(R.id.lDirection)
        et_direction = lDirection.editText as TextInputEditText
        lBenefit = view.findViewById(R.id.lBenefit)
        et_benefit = lBenefit.editText as TextInputEditText
        lSingleBed = view.findViewById(R.id.lSingleBed)
        et_singleBed = lSingleBed.editText as TextInputEditText
        lDoubleBed = view.findViewById(R.id.lDoubleBed)
        et_doubleBed = lDoubleBed.editText as TextInputEditText
        lPrice = view.findViewById(R.id.lPrice)
        et_price = lPrice.editText as TextInputEditText
        lQuantity = view.findViewById(R.id.lQuantity)
        et_quantity = lQuantity.editText as TextInputEditText

        btn_back = view.findViewById(R.id.btn_back)
        btn_next = view.findViewById(R.id.btn_next)
        progressBar2 = view.findViewById(R.id.progressBar2)

        val roomType = arguments?.getString("roomType")
        if (roomType != null) {
            et_roomType.text = Editable.Factory.getInstance().newEditable(arguments?.getString("roomType"))
            et_acreage.text = Editable.Factory.getInstance().newEditable(arguments?.getString("acreage"))
            et_direction.text = Editable.Factory.getInstance().newEditable(arguments?.getString("direction"))
            et_benefit.text = Editable.Factory.getInstance().newEditable(arguments?.getString("benefit"))
            et_singleBed.text = Editable.Factory.getInstance().newEditable(arguments?.getString("singleBed"))
            et_doubleBed.text = Editable.Factory.getInstance().newEditable(arguments?.getString("doubleBed"))
            et_price.text = Editable.Factory.getInstance().newEditable(arguments?.getString("price"))
            et_quantity.text = Editable.Factory.getInstance().newEditable(arguments?.getString("quantity"))
        }

        if (fromFrag == "edit"){
            view.findViewById<TextView>(R.id.addRoom_title).text = "Sửa thông tin phòng"
            btn_back.visibility = View.VISIBLE
        } else if (fromFrag == "manageRoom"){
            btn_back.visibility = View.VISIBLE
        }
        else{
            btn_back.visibility = View.GONE
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddRoomFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(idHotel: String, idUser: String) =
            AddRoomFragment(idHotel, idUser).apply {
                arguments = Bundle().apply {
                    putString("from", fromFrag)
                }
            }
    }
}