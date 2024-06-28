package com.example.angodafake

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.angodafake.Utilities.UserUtils
import com.example.angodafake.db.User
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignUpActivity : AppCompatActivity() {
    private val checkArray = BooleanArray(5) { false } //kiem tra tinh hop le thong tin nguoi dung nhap
    private lateinit var lName : TextInputLayout
    private lateinit var etName : TextInputEditText
    private lateinit var lDob : TextInputLayout
    private lateinit var etDob : TextInputEditText
    private lateinit var lPass : TextInputLayout
    private lateinit var etPass : TextInputEditText
    private lateinit var lConfirmPass : TextInputLayout
    private lateinit var etConfirmPass : TextInputEditText

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = Firebase.auth
        database = Firebase.database.reference

        lName = findViewById(R.id.lName)
        etName = lName.editText as TextInputEditText
        lDob = findViewById(R.id.lDob)
        etDob = lDob.editText as TextInputEditText
        lPass = findViewById(R.id.lPass)
        etPass = lPass.editText as TextInputEditText
        lConfirmPass = findViewById(R.id.lConfirmPass)
        etConfirmPass = lConfirmPass.editText as TextInputEditText

        val btn_register = findViewById<Button>(R.id.btn_register)
        //ten
        etName.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                checkArray[0] = validateName(lName, etName)
            }
            else{
                lName.error = null
            }
        }

        //ngay sinh
        etDob.setOnClickListener{
            clearFocus()
            hideKeyboard(this)
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
                this,
                { view: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                    // Xử lý khi người dùng chọn ngày
                    val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                    etDob.setText(selectedDate)
                    if (!validateDob(selectedYear, selectedMonth, selectedDayOfMonth)) {
                        lDob.error = "Bạn phải đủ 18 tuổi để đăng kí"
                        checkArray[1] = false
                    }
                    else{
                        lDob.error = null
                        checkArray[1] = true
                    }
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        //nut back
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //password
        etPass.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                checkArray[3] = validatePass(lPass, etPass)
            }
            else{
                lPass.error = null
            }
        }

        //confirmPassword
        etConfirmPass.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                checkArray[4] = validateConfirmPass(lConfirmPass, etConfirmPass, etPass)
            }
            else{
                lConfirmPass.error = null
            }
        }

        //Dang ki voi email
        if (intent.getStringExtra("with") == "email"){
            findViewById<TextView>(R.id.tv_emailPhoneN).text = "Email"
            val lEmail = findViewById<TextInputLayout>(R.id.lEmailOrPhoneN)
            val etEmail = lEmail.editText as TextInputEditText
            etEmail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            etEmail.hint = "Email"

            etEmail.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    checkArray[2] = validateEmail(lEmail, etEmail)
                }
                else{
                    lEmail.error = null
                }
            }

            btn_register.setOnClickListener {
                clearFocus()
                hideKeyboard(this)
                checkAll(etEmail)
                if (checkArray.all { it }){
                    registerWithEmail(lEmail, etEmail)
                }
            }
        }

        //Dang ki voi sdt
        else if (intent.getStringExtra("with") == "phoneNumber"){
            findViewById<TextView>(R.id.tv_emailPhoneN).text = "Số di động"
            val lPhoneN = findViewById<TextInputLayout>(R.id.lEmailOrPhoneN)
            val etPhoneN = lPhoneN.editText as TextInputEditText
            etPhoneN.inputType = InputType.TYPE_CLASS_PHONE
            etPhoneN.hint = "Số di động"

            etPhoneN.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    checkArray[2] = validatePhoneNumber(lPhoneN, etPhoneN)
                }
                else{
                    lPhoneN.error = null
                }
            }

            btn_register.setOnClickListener {
                clearFocus()
                hideKeyboard(this)
                checkAll(etPhoneN)
                if (checkArray.all { it }){
                    registerWithPhoneN(lPhoneN, etPhoneN)
                }
            }
        }

    }

    private fun registerWithEmail(lEmail:TextInputLayout, etEmail: EditText){
        val email = etEmail.text.toString().trim()
        auth.createUserWithEmailAndPassword(email, etPass.text.toString().trim())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val name = etName.text.toString().trim()
                    val dob = etDob.text.toString().trim()
                    val gender = ""
                    val number = ""
                    val country = ""
                    val cardNumber = ""
                    val cardName = ""

                    val user = User(null,name, dob, gender, number, email, country, cardNumber, cardName)
                    val userID = auth.currentUser!!.uid
                    database.child("users").child(userID).setValue(user)
                    showSuccessSnackBar("Tạo tài khoản thành công!")
                    val handler = Handler()
                    handler.postDelayed({
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("idUser", userID)
                        startActivity(intent)
                        finish()
                    }, 1000)
                } else {
                    // If sign in fails, display a message to the user.
                    showFailSnackBar("Người dùng đã tồn tại! Vui lòng nhập email khác.")
                    checkArray[2] = false
                    lEmail.error = "Email đã được sử dụng."
                }
            }
    }

    private fun registerWithPhoneN(lPhoneN:TextInputLayout, etPhoneN: EditText){
        val phoneN = etPhoneN.text.toString().trim()
        UserUtils.getUserByPhoneNumber(phoneN){
            if (it != null){
                showFailSnackBar("Người dùng đã tồn tại! Vui lòng nhập số di động khác.")
                checkArray[2] = false
                lPhoneN.error = "Số di động đã được sử dụng."
            }
            else{
                val fEmailFromPhoneN  = "$phoneN@gmail.com"
                auth.createUserWithEmailAndPassword(fEmailFromPhoneN, etPass.text.toString().trim())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val name = etName.text.toString().trim()
                            val dob = etDob.text.toString().trim()
                            val gender = ""
                            val number = fEmailFromPhoneN.substring(0, fEmailFromPhoneN.indexOf("@"))
                            val country = ""
                            val cardNumber = ""
                            val cardName = ""

                            val user = User(null, name, dob, gender, number, "", country, cardNumber, cardName)
                            val userID = auth.currentUser!!.uid
                            database.child("users").child(userID).setValue(user)
                            showSuccessSnackBar("Tạo tài khoản thành công!")
                            val handler = Handler()
                            handler.postDelayed({
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("idUser", userID)
                                startActivity(intent)
                                finish()
                            }, 1000)
                        } else {
                            // If sign in fails, display a message to the user.
                            showFailSnackBar("Người dùng đã tồn tại! Vui lòng nhập số di động khác.")
                            checkArray[2] = false
                            lPhoneN.error = "Số di động đã được sử dụng."
                        }
                    }
            }
        }
    }

    private fun showFailSnackBar(msg: String) {
        val snackbar = Snackbar.make(findViewById(R.id.rootView), msg, Snackbar.LENGTH_LONG)
        snackbar.view.backgroundTintList = ColorStateList.valueOf(Color.RED)
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }

    private fun showSuccessSnackBar(msg: String) {
        val snackbar = Snackbar.make(findViewById(R.id.rootView), msg, Snackbar.LENGTH_LONG)
        snackbar.view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3193FF"))
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }

    private fun clearFocus(){
        etName.clearFocus()
        findViewById<TextInputLayout>(R.id.lEmailOrPhoneN).editText?.clearFocus()
        etPass.clearFocus()
        etConfirmPass.clearFocus()
    }
    private fun checkAll(editText: EditText){
            checkArray[0] = validateName(lName, etName)
            editText.requestFocus()
            editText.clearFocus()
            checkArray[3] = validatePass(lPass, etPass)
            checkArray[4] = validateConfirmPass(lConfirmPass, etConfirmPass, etPass)
            checkArray[1] = !isCheckEmpty(lDob, etDob, "Ngày sinh") && checkArray[1]
            if (!checkArray[1]){
                lDob.error = "Bạn phải đủ 18 tuổi để đăng kí"
            }
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
    private fun validateEmail(lEmail: TextInputLayout, etEmail: TextInputEditText): Boolean {
        return if (isCheckEmpty(lEmail, etEmail, "Email")){
            false
        } else{
            val emailPattern = Regex("^([a-z\\d\\.-]+)@([a-z\\d-]+)\\.([a-z]{2,8})(\\.[a-z]{2,8})?\$")
            if (!etEmail.text.toString().trim().matches(emailPattern)){
                lEmail.error = "Định dạng email không hợp lệ."
                false
            } else{
                lEmail.error = null
                true
            }
        }
    }
    private fun validatePhoneNumber(lPhoneN: TextInputLayout, etPhoneN: TextInputEditText): Boolean {
        return if (isCheckEmpty(lPhoneN, etPhoneN, "Số di động")){
            false
        } else{
            if (etPhoneN.text.toString().trim().length != 10 && etPhoneN.text.toString().trim().length != 11){
                lPhoneN.error = "Số di động không hợp lệ."
                false
            } else{
                lPhoneN.error = null
                true
            }
        }
    }

    private fun validatePass(lPass: TextInputLayout, etPass: TextInputEditText): Boolean {
        return if (isCheckEmpty(lPass, etPass, "Mật khẩu")){
            false
        } else if (etPass.text.toString().trim().length < 8){
            lPass.error = "Mật khẩu phải có ít nhất 8 kí tự."
            false
        } else{
            lPass.error = null
            true
        }
    }

    private fun validateConfirmPass(lConfirmPass: TextInputLayout, etConfirmPass: TextInputEditText, etPass: TextInputEditText): Boolean {
        return if (isCheckEmpty(lConfirmPass, etConfirmPass, "Xác nhận mật khẩu")){
            false
        } else if (etConfirmPass.text.toString().trim() != etPass.text.toString().trim()){
            lConfirmPass.error = "Mật khẩu xác nhận không đúng."
            false
        } else{
            lConfirmPass.error = null
            true
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
}