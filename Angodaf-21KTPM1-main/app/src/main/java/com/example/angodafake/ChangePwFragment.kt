package com.example.angodafake

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.example.angodafake.Utilities.UserUtils
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [ChangePwFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChangePwFragment(private var idUser: String) : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var lOldPw: TextInputLayout
    private lateinit var et_oldPw: TextInputEditText
    private lateinit var lNewPw: TextInputLayout
    private lateinit var et_newPw: TextInputEditText
    private lateinit var lConfirmedNewPw: TextInputLayout
    private lateinit var et_confirmedNewPw: TextInputEditText

    private lateinit var btn_back: ImageButton
    private lateinit var btn_change: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_change_pw, container, false)
        initUI(view)

        et_oldPw.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                lOldPw.error = null
            }
        }
        et_newPw.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                lNewPw.error = null
            }
        }
        et_confirmedNewPw.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                lConfirmedNewPw.error = null
            }
        }

        btn_back.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.replaceFragment(MyProfile(idUser))
        }

        btn_change.setOnClickListener {
            hideKeyboard(requireActivity())
            if (!isCheckEmpty(lOldPw, et_oldPw, "Mật khẩu cũ")){
                val pw = et_oldPw.text.toString()
                val user = Firebase.auth.currentUser
                UserUtils.getUserByID(idUser){
                    val email = if (it!!.email == "" || it.email == null){
                        "${it.phoneN}@gmail.com"
                    } else{
                        it.email
                    }
                    val credential = EmailAuthProvider.getCredential(email, pw)
                    user!!.reauthenticate(credential).addOnCompleteListener {
                        if (it.isSuccessful){
                            if (checkNewPw()){
                                user.updatePassword(et_newPw.text.toString())
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            showSuccessSnackBar(view, "Thay đổi mật khẩu thành công")
                                            val handler = Handler()
                                            handler.postDelayed({
                                                Firebase.auth.signOut()
                                                val intent = Intent(requireContext(), LoginActivity::class.java)
                                                startActivity(intent)
                                            }, 500)
                                        }
                                    }
                            }
                        } else{
                            lOldPw.error = "Mật khẩu cũ không đúng"
                        }
                    }
                }
            }
        }

        return view
    }
    private fun checkNewPw(): Boolean{
        var check = true
        if (isCheckEmpty(lNewPw, et_newPw,  "Mật khẩu mới"))
            check = false
        if (isCheckEmpty(lConfirmedNewPw, et_confirmedNewPw,  "Mật khẩu nhập lại"))
            check = false
        if (check){
            if (et_newPw.text.toString().length < 8) {
                lNewPw.error = "Mật khẩu phải có ít nhất 8 kí tự."
                check = false
            }
            if (et_newPw.text.toString() != et_confirmedNewPw.text.toString()){
                lConfirmedNewPw.error = "Mật khẩu nhập lại không đúng"
                check = false
            }
        }
        return check
    }

    private fun showSuccessSnackBar(view: View, msg: String) {
        val snackbar = Snackbar.make(view.rootView, msg, Snackbar.LENGTH_LONG)
        snackbar.view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3193FF"))
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }
    private fun showSnackBar(view: View, msg: String) {
        val snackbar = Snackbar.make(view.rootView, msg, Snackbar.LENGTH_LONG)
        // Đổi màu background của Snackbar
        snackbar.view.backgroundTintList = ColorStateList.valueOf(Color.RED)
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
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

    private fun isCheckEmpty(textInputLayout: TextInputLayout, editText: EditText, name: String) : Boolean{
        return if (editText.text.toString().trim() == ""){
            textInputLayout.error = "$name không được để trống."
            true
        } else{
            textInputLayout.error = null
            false
        }
    }
    private fun initUI(view: View){
        lOldPw = view.findViewById(R.id.lOldPw)
        et_oldPw = lOldPw.editText as TextInputEditText
        lNewPw = view.findViewById(R.id.lNewPw)
        et_newPw = lNewPw.editText as TextInputEditText
        lConfirmedNewPw = view.findViewById(R.id.lConfirmedNewPw)
        et_confirmedNewPw = lConfirmedNewPw.editText as TextInputEditText

        btn_back = view.findViewById(R.id.btn_back)
        btn_change = view.findViewById(R.id.btn_change)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChangePwFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(idUser: String) =
            ChangePwFragment(idUser).apply {
                arguments = Bundle().apply {
                }
            }
    }
}