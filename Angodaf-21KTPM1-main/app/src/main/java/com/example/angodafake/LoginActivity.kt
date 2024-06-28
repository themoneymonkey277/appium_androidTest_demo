package com.example.angodafake

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Layout
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TabHost
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.angodafake.Utilities.UserUtils
import com.example.angodafake.db.User
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginActivity : AppCompatActivity() {
    private lateinit var tabHost : TabHost
    private lateinit var auth: FirebaseAuth
    private var oneTapClient: SignInClient? = null
    private lateinit var signInRequest: BeginSignInRequest
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //dang nhap bang gg
        auth = Firebase.auth

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.default_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()

        //dang nhap bang email/ sdt
        tabHost = findViewById(R.id.tabHost)
        tabHost.setup()

        var tabSpec : TabHost.TabSpec? = null

        tabSpec = tabHost.newTabSpec("tab_email")
        tabSpec.setIndicator("Email", null)
        tabSpec.setContent(R.id.tab_email)
        tabHost.addTab(tabSpec)

        tabSpec = tabHost.newTabSpec("tab_phone")
        tabSpec.setIndicator("Di động", null)
        tabSpec.setContent(R.id.tab_phone)
        tabHost.addTab(tabSpec)

        setUpTabEmail()
        tabHost.setOnTabChangedListener { tabId ->
            when (tabId) {
                "tab_email" -> {
                    setUpTabEmail()
                }
                "tab_phone" -> {
                    setUpTabPhoneN()
                }
            }
        }
    }

    fun signinGoogle(view: View){
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Main).launch {
            signinGoogle()
        }
    }

    private suspend fun signinGoogle(){
        val result = oneTapClient?.beginSignIn(signInRequest)?.await()
        val intentSenderRequest = IntentSenderRequest.Builder(result!!.pendingIntent).build()
        activityResultLauncher.launch(intentSenderRequest)
    }

    private val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ){result->
            if (result.resultCode == RESULT_OK){
                try{
                    val credential = oneTapClient!!.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null){
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseCredential).addOnCompleteListener {
                            if (it.isSuccessful){
                                findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
                                val user = auth.currentUser
                                showSuccessSnackBar("Đăng nhập thành công!")
                                UserUtils.getUserByID(user!!.uid){getUser->
                                    if (getUser != null){
                                        val handler = Handler()
                                        handler.postDelayed({
                                            val intent = Intent(this, MainActivity::class.java)
                                            intent.putExtra("idUser", user.uid)
                                            startActivity(intent)
                                            finish()
                                        }, 1000)
                                    } else{
                                        val name = user.displayName
                                        val dob = ""
                                        val email = user.email
                                        val gender = ""
                                        val number = ""
                                        val country = ""
                                        val cardNumber = ""
                                        val cardName = ""

                                        val us = User(null,name, dob, gender, number, email, country, cardNumber, cardName)
                                        val userID = auth.currentUser!!.uid
                                        Firebase.database.reference.child("users").child(userID).setValue(us)
                                        val handler = Handler()
                                        handler.postDelayed({
                                            val intent = Intent(this, MainActivity::class.java)
                                            intent.putExtra("idUser", user.uid)
                                            startActivity(intent)
                                            finish()
                                        }, 1000)
                                    }
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                showSnackBar("Đăng nhập thành công!")
                            }
                        }
                    }
                } catch (e: ApiException){
                    e.printStackTrace()
                }
            }

        }
    private fun setUpTabEmail(){
        val lEmail = findViewById<TextInputLayout>(R.id.lEmail)
        val etEmail = lEmail.editText as TextInputEditText
        val lPass = findViewById<TextInputLayout>(R.id.lPass)
        val etPass = lPass.editText as TextInputEditText

        findViewById<Button>(R.id.btn_login_email).setOnClickListener {
            hideKeyboard(this)
            val email = etEmail.text.toString().trim()
            val pass = etPass.text.toString().trim()
            val checkEmail = validateEmail(email, lEmail)
            val checkPass = validatePass(pass, lPass)
            //Thong tin nhap vao hop le
            if (checkEmail && checkPass){
                signInWithEmail(email, pass, "Email hoặc Mật khẩu không đúng.")
            }
        }

        findViewById<TextView>(R.id.register).setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("with", "email")
            startActivity(intent)
            finish()
        }
    }

    private fun setUpTabPhoneN(){
        val lPhoneN = findViewById<TextInputLayout>(R.id.lPhoneN)
        val etPhoneN = lPhoneN.editText as TextInputEditText

        val lPass2 = findViewById<TextInputLayout>(R.id.lPass2)
        val etPass2 = lPass2.editText as TextInputEditText

        findViewById<Button>(R.id.btn_login_phoneN).setOnClickListener {
            hideKeyboard(this)
            val phoneN = etPhoneN.text.toString().trim()
            val pass = etPass2.text.toString().trim()
            val checkPhoneN = validatePhoneNumber(phoneN, lPhoneN)
            val checkPass = validatePass(pass, lPass2)
            //Thong tin nhap vao hop le
            if (checkPhoneN && checkPass){
                signInWithPhoneN(phoneN, pass)
            }
        }
        findViewById<TextView>(R.id.register2).setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("with", "phoneNumber")
            startActivity(intent)
            finish()
        }
    }

    private fun signInWithEmail(email: String, password: String, msg: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    showSuccessSnackBar("Đăng nhập thành công!")
                    val handler = Handler()
                    handler.postDelayed({
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("idUser", user!!.uid)
                        startActivity(intent)
                        finish()
                    }, 1000)
                } else {
                    // If sign in fails, display a message to the user.
                    showSnackBar(msg)
                }
            }
    }

    private fun signInWithPhoneN(phoneN: String, password: String, ){
        UserUtils.getUserByPhoneNumber(phoneN) {
            if (it == null) {
                showSnackBar("Số di động hoặc Mật khẩu không đúng.")
            } else {
                if (it.email == "") {
                    val fEmailFromPhoneN = "$phoneN@gmail.com"
                    signInWithEmail(
                        fEmailFromPhoneN,
                        password,
                        "Số di động hoặc Mật khẩu không đúng."
                    )
                } else {
                    signInWithEmail(it.email!!, password, "Số di động hoặc Mật khẩu không đúng.")
                }
            }
        }
    }

    private fun validateEmail(email: String, lEmail: TextInputLayout): Boolean {
        return if (email == ""){
            lEmail.error = "Email không được để trống."
            false
        } else{
            val emailPattern = Regex("^([a-z\\d\\.-]+)@([a-z\\d-]+)\\.([a-z]{2,8})(\\.[a-z]{2,8})?\$")
            if (!email.matches(emailPattern)){
                lEmail.error = "Định dạng email không hợp lệ."
                false
            } else{
                lEmail.error = null
                true
            }
        }
    }

    private fun validatePhoneNumber(phoneN: String, lPhoneN: TextInputLayout): Boolean {
        return if (phoneN == ""){
            lPhoneN.error = "Số di động không được để trống."
            false
        } else{
            if (phoneN.length != 10 && phoneN.length != 11){
                lPhoneN.error = "Số di động không hợp lệ."
                false
            } else{
                lPhoneN.error = null
                true
            }
        }
    }


    private fun validatePass(pass: String, lPass: TextInputLayout): Boolean {
        return if (pass == ""){
            lPass.error = "Mật khẩu không được để trống."
            false
        } else if (pass.length < 8){
            lPass.error = "Mật khẩu phải có ít nhất 8 kí tự."
            false
        } else{
            lPass.error = null
            true
        }
    }
    @SuppressLint("RestrictedApi")
    private fun showSnackBar(msg: String) {
        val snackbar = Snackbar.make(findViewById(R.id.rootView), msg, Snackbar.LENGTH_LONG)
        // Đổi màu background của Snackbar
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