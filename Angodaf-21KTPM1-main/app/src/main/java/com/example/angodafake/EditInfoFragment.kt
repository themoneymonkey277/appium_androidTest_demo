package com.example.angodafake

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.example.angodafake.Utilities.UserUtils
import com.example.angodafake.db.User
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [EditInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */



class EditInfoFragment(private var idUser: String) : Fragment() {
    interface PasswordDialogListener {
        fun onSubmitClicked(password: String)
    }
    // TODO: Rename and change types of parameters
    private val checkArray = BooleanArray(7) { false } //kiem tra tinh hop le thong tin nguoi dung nhap
    private lateinit var lName : TextInputLayout
    private lateinit var etName : TextInputEditText
    private lateinit var lDob : TextInputLayout
    private lateinit var etDob : TextInputEditText
    private lateinit var rgGender : RadioGroup
    private lateinit var lCountry : TextInputLayout
    private lateinit var actvCountry : MaterialAutoCompleteTextView
    private lateinit var lEmail : TextInputLayout
    private lateinit var etEmail : TextInputEditText
    private lateinit var lPhoneN : TextInputLayout
    private lateinit var etPhoneN : TextInputEditText
    private lateinit var btn_back : ImageButton
    private lateinit var btn_changeInf : Button

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
        val view = inflater.inflate(R.layout.fragment_edit_info, container, false)
        initUI(view)

        val check = mutableListOf(true, true, true, true, true)

        UserUtils.getUserByID(idUser){
            if (it!!.name != null){
                etName.text = Editable.Factory.getInstance().newEditable(it.name)
            }
            if (it.dob != null){
                etDob.text = Editable.Factory.getInstance().newEditable(it.dob)
            }
            if (it.gender != null){
                var radioBtnId : Int = -1
                when (it.gender){
                    "Male" ->  radioBtnId = view.findViewById<View?>(R.id.radioButton).id
                    "Female" -> radioBtnId = view.findViewById<View?>(R.id.radioButton2).id
                    "Other" -> radioBtnId = view.findViewById<View?>(R.id.radioButton3).id
                }
                rgGender.check(radioBtnId)
            }
            if (it.country != null){
                actvCountry.text = Editable.Factory.getInstance().newEditable(it.country)
            }
            if (it.email != null){
                etEmail.text = Editable.Factory.getInstance().newEditable(it.email)
            }
            if (it.phoneN != null){
                etPhoneN.text = Editable.Factory.getInstance().newEditable(it.phoneN)
            }
        }

        etName.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                lName.error = null
            }
        }

        //ngay sinh
        etDob.setOnClickListener{
            clearFocus()
            hideKeyboard(requireActivity())
            lDob.error = null
            val year: Int
            val month: Int
            val day: Int
            if (etDob.text.toString() == ""){
                // Lấy ngày hiện tại
                val calendar = Calendar.getInstance()
                year = calendar.get(Calendar.YEAR)
                month = calendar.get(Calendar.MONTH)
                day = calendar.get(Calendar.DAY_OF_MONTH)
            }
            else{
                val date = etDob.text.toString().split("/")
                year = date[2].toInt()
                month = date[1].toInt() - 1
                day = date[0].toInt()
            }
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                    // Xử lý khi người dùng chọn ngày
                    val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                    etDob.setText(selectedDate)
                    if (!validateDob(selectedYear, selectedMonth, selectedDayOfMonth)) {
                        check[1] = false
                    }
                    else{
                        check[1] = true
                    }
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        //quoc tich
        val countries = resources.getStringArray(R.array.countries)
        val adapterActv = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, countries)
        actvCountry.setAdapter(adapterActv)
        actvCountry.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lCountry.error = null
            }
        }
        actvCountry.setOnItemClickListener { parent, view, position, id ->
            hideKeyboard(requireActivity())
            clearFocus()
        }

        btn_back.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.replaceFragment(MyProfile(idUser))
        }

        btn_changeInf.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch{
                clearFocus()
                //ten
                check[0] = validateName(lName, etName)

                //ngay sinh
                if (!check[1]) {
                    lDob.error = "Bạn phải đủ 18 tuổi để đăng kí"
                }
                else{ //ngay sinh khong duoc de trong
                    check[1] = !isCheckEmpty(lDob, etDob, "Ngày sinh") && check[1]
                }

                //quoc tich
                if (actvCountry.text.toString().trim() == ""){
                    check[2] = true
                    lCountry.error = null
                }
                else if (actvCountry.text.toString().trim() !in countries){
                    lCountry.error = "Quốc tịch không hợp lệ"
                    check[2] = false
                }
                else{
                    check[2] = true
                    lCountry.error = null
                }

                //email
                check[3] = validateEmail(lEmail, etEmail)

                //Sdt
                if (etPhoneN.text.toString().trim() == ""){
                    check[4] = true
                }
                else{
                    check[4] = validatePhoneNumber(lPhoneN, etPhoneN)
                }

                if (check[0] && check[1] && check[2] && check[3] && check[4]){
                    val name = etName.text.toString().trim()
                    val dob = etDob.text.toString().trim()
                    var gender: String ?= null
                    if (rgGender.checkedRadioButtonId != -1){
                        gender = view.findViewById<RadioButton>(rgGender.checkedRadioButtonId).text.toString().trim()
                    }
                    val number = etPhoneN.text.toString().trim()
                    val email = etEmail.text.toString().trim()
                    val country = actvCountry.text.toString().trim()
                    val user = User(null, name, dob, gender, number, email, country)
                    UserUtils.getUserByID(idUser){ it ->
                        if (email != it!!.email || number != it.phoneN){
                            val providers = Firebase.auth.currentUser!!.providerData
                            var c = true
                            for (profile in providers) {
                                // Nếu user đăng nhập bằng Google, thì không được phép chỉnh sửa thông tin email
                                if (profile.providerId == "google.com") {
                                    c = false
                                    break
                                }
                            }
                            if (!c){
                                lEmail.error = "Không được đổi thông tin này."
                                showSnackBar(view, "Bạn đăng nhập bằng tài khoản google này, vì vậy email không được phép chỉnh sửa")
                            } else{
                                openPwDialog( object: PasswordDialogListener{
                                    override fun onSubmitClicked(password: String) {
                                        UserUtils.updateUserByID(idUser, user, password){check->
                                            if (check){
                                                hideKeyboard(requireActivity())
                                                showSuccessSnackBar(view, "Thay đổi thông tin thành công.")
                                            }
                                            else{
                                                hideKeyboard(requireActivity())
                                                showSnackBar(view, "Mật khẩu không đúng.")
                                            }
                                        }
                                    }
                                }
                                )
                            }
                        }
                        else{
                            UserUtils.updateUserByID(idUser, user, ""){check->
                                hideKeyboard(requireActivity())
                                showSuccessSnackBar(view, "Thay đổi thông tin thành công.")
                            }
                        }
                    }


                }
            }

        }

        return view
    }

    @SuppressLint("ResourceType")
    private fun openPwDialog(listener: PasswordDialogListener){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_password_dialog)

        val window = dialog.window ?: return

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.attributes.gravity = Gravity.CENTER

        dialog.setCancelable(true)

        val et_Password = dialog.findViewById<EditText>(R.id.et_Password)
        val btn_submit = dialog.findViewById<Button>(R.id.btn_submit)
        val btn_cancel = dialog.findViewById<Button>(R.id.btn_cancel)

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }

        btn_submit.setOnClickListener {
            listener.onSubmitClicked(et_Password.text.trim().toString())
            hideKeyboard(requireActivity())
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun initUI(view: View){
        lName = view.findViewById(R.id.lName)
        etName = lName.editText as TextInputEditText
        lDob = view.findViewById(R.id.lDob)
        etDob = lDob.editText as TextInputEditText
        rgGender = view.findViewById(R.id.radioGroup)
        lCountry = view.findViewById(R.id.lCountry)
        actvCountry = view.findViewById(R.id.actvCountry)
        lEmail = view.findViewById(R.id.lEmail)
        etEmail = lEmail.editText as TextInputEditText
        lPhoneN = view.findViewById(R.id.lPhoneN)
        etPhoneN = lPhoneN.editText as TextInputEditText
        btn_back = view.findViewById(R.id.btn_back)
        btn_changeInf = view.findViewById(R.id.btn_changeInf)
    }
    private fun showSuccessSnackBar(view: View, msg: String) {
        val snackbar = Snackbar.make(view.findViewById(R.id.rootView), msg, Snackbar.LENGTH_LONG)
        snackbar.view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3193FF"))
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }
    private fun showSnackBar(view: View, msg: String) {
        val snackbar = Snackbar.make(view.findViewById(R.id.rootView), msg, Snackbar.LENGTH_LONG)
        // Đổi màu background của Snackbar
        snackbar.view.backgroundTintList = ColorStateList.valueOf(Color.RED)
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }

    private fun clearFocus(){
        etName.clearFocus()
        actvCountry.clearFocus()
        etEmail.clearFocus()
        etPhoneN.clearFocus()
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

    private fun validateName(lName: TextInputLayout, etName: TextInputEditText): Boolean {
        return if (isCheckEmpty(lName, etName, "Tên")){
            false
        } else if (etName.text.toString().trim().matches(Regex("^[a-zA-ZÀ-ỹ\\s']+\$"))) {
            lName.error = null
            true
        } else{
            lName.error = "Tên không hợp lệ! Tên không chứa số và các kí tự đặc biệt."
            false
        }
    }

    private fun validateDob(selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int): Boolean {
        val selectedCalendar = Calendar.getInstance().apply {
            set(selectedYear, selectedMonth, selectedDayOfMonth)
        }
        val currentDate = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
        }
        val minimumAge = 18
        currentDate.add(Calendar.YEAR, -minimumAge)
        return selectedCalendar.before(currentDate)
    }

    private suspend fun validateEmail(lEmail: TextInputLayout, etEmail: TextInputEditText): Boolean {
        return if (isCheckEmpty(lEmail, etEmail, "Email")){
            false
        } else{
            val emailPattern = Regex("^([a-z\\d\\.-]+)@([a-z\\d-]+)\\.([a-z]{2,8})(\\.[a-z]{2,8})?\$")
            if (!etEmail.text.toString().trim().matches(emailPattern)){
                lEmail.error = "Định dạng email không hợp lệ."
                false
            } else{
                val user = UserUtils.getUserByEmail(etEmail.text.toString().trim())
                if (user != null && user.ID != idUser) {
                    lEmail.error = "Email đã được sử dụng cho một tài khoản khác"
                    false
                } else {
                    lEmail.error = null
                    true
                }
            }
        }
    }
    private suspend fun validatePhoneNumber(lPhoneN: TextInputLayout, etPhoneN: TextInputEditText): Boolean {
        return if (etPhoneN.text.toString().trim().length != 10 && etPhoneN.text.toString().trim().length != 11){
                lPhoneN.error = "Số di động không hợp lệ."
                false
            } else {
                val isPhoneNumberValid = CoroutineScope(Dispatchers.IO).async {
                    val user = UserUtils.getUserByPhoneNumber(etPhoneN.text.toString().trim())
                    user == null || user.ID == idUser
                }.await()

                if (!isPhoneNumberValid) {
                    lPhoneN.error = "Số di động đã được sử dụng cho một tài khoản khác"
                    false
                } else {
                    lPhoneN.error = null
                    true
                }
            }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(idUser: String) =
            EditInfoFragment(idUser).apply {
                arguments = Bundle().apply {
                }
            }
    }
}