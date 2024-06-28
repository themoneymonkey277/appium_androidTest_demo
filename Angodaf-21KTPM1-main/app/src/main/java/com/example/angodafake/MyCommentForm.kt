package com.example.angodafake

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.angodafake.Adapter.CommentAdapter
import com.example.angodafake.Utilities.CommentUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MyCommentForm : AppCompatActivity() {
    private var choice: Int = -1
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_my_comment_form)

        val hotelName: TextView = findViewById(R.id.textView)
        hotelName.text = intent.getStringExtra("hotelName")

        val ID_Purchase: TextView = findViewById(R.id.textView2)
        ID_Purchase.text = intent.getStringExtra("ID_Purchase")

        val scrollView:ScrollView = findViewById(R.id.scrollView)
        
        val ID_Owner = intent.getStringExtra("ID_Owner")
        val ID_Hotel = intent.getStringExtra("ID_Hotel")

        val count1: TextView = findViewById(R.id.count1)
        val count2: TextView = findViewById(R.id.count2)

        count1.visibility = View.GONE
        count2.visibility = View.GONE

        //Các biến thông báo
        val notic: TextView = findViewById(R.id.notic)
        val notic1: TextView = findViewById(R.id.notic1)
        val notic2: TextView = findViewById(R.id.notic2)

        notic.visibility = View.GONE
        notic1.visibility = View.GONE
        notic2.visibility = View.GONE

        val btnBack: ImageButton = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.progress = 2

        val relativeLayout: RelativeLayout = findViewById(R.id.comment)
        val relativeLayout1: RelativeLayout = findViewById(R.id.comment1)
        val relativeLayout2: RelativeLayout = findViewById(R.id.comment2)
        val relativeLayout3: RelativeLayout = findViewById(R.id.comment3)
        val relativeLayout4: RelativeLayout = findViewById(R.id.comment4)
        val relativeLayout5: RelativeLayout = findViewById(R.id.comment5)
        val relativeLayout6: RelativeLayout = findViewById(R.id.comment6)

        relativeLayout.visibility = View.VISIBLE
        relativeLayout1.visibility = View.GONE
        relativeLayout2.visibility = View.GONE
        relativeLayout3.visibility = View.GONE
        relativeLayout4.visibility = View.GONE
        relativeLayout5.visibility = View.GONE
        relativeLayout6.visibility = View.GONE

        var money: Int = 0
        var location: Int = 0
        var clean: Int = 0
        var service: Int = 0
        var convenience: Int = 0

        val btnYes: Button = findViewById(R.id.button)
        val btnNo: Button = findViewById(R.id.button1)

        btnYes.setOnClickListener {
            relativeLayout1.visibility = View.VISIBLE
            scrollView.post {
                scrollView.smoothScrollTo(relativeLayout.bottom, relativeLayout1.bottom)
            }

            if (progressBar.progress == 2) {
                progressBar.progress += 14
            }
        }

        btnNo.setOnClickListener {
            relativeLayout1.visibility = View.VISIBLE
            scrollView.post {
                scrollView.smoothScrollTo(relativeLayout.bottom, relativeLayout1.bottom)
            }

            if (progressBar.progress == 2) {
                progressBar.progress += 14
            }
        }

        val ratingBar: RatingBar = findViewById(R.id.ratingBar)
        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            relativeLayout2.visibility = View.VISIBLE
            scrollView.post {
                scrollView.smoothScrollTo(relativeLayout1.bottom, relativeLayout2.bottom)
            }

            if (progressBar.progress == 16) {
                progressBar.progress += 14
            }
            money = ratingBar.rating.toInt()
        }

        val ratingBar1: RatingBar = findViewById(R.id.ratingBar1)
        ratingBar1.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            relativeLayout3.visibility = View.VISIBLE
            scrollView.post {
                scrollView.smoothScrollTo(relativeLayout2.bottom, relativeLayout3.bottom)
            }

            if (progressBar.progress == 30) {
                progressBar.progress += 14
            }
            location = ratingBar.rating.toInt()
        }

        val ratingBar2: RatingBar = findViewById(R.id.ratingBar2)
        ratingBar2.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            relativeLayout4.visibility = View.VISIBLE
            scrollView.post {
                scrollView.smoothScrollTo(relativeLayout3.bottom, relativeLayout4.bottom)
            }

            if (progressBar.progress == 44) {
                progressBar.progress += 14
            }
            clean = ratingBar.rating.toInt()
        }

        val ratingBar3: RatingBar = findViewById(R.id.ratingBar3)
        ratingBar3.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            relativeLayout5.visibility = View.VISIBLE
            scrollView.post {
                scrollView.smoothScrollTo(relativeLayout4.bottom, relativeLayout5.bottom)
            }

            if (progressBar.progress == 58) {
                progressBar.progress += 14
            }
            service = ratingBar.rating.toInt()
        }

        val ratingBar4: RatingBar = findViewById(R.id.ratingBar4)
        ratingBar4.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            relativeLayout6.visibility = View.VISIBLE
            scrollView.post {
                scrollView.smoothScrollTo(relativeLayout5.bottom, relativeLayout6.bottom)
            }

            if (progressBar.progress == 72) {
                progressBar.progress += 14
            }
            convenience = ratingBar.rating.toInt()
        }

        val typeCustomer: TextView = findViewById(R.id.typeCustomer)
        typeCustomer.setOnClickListener {
            val dialog = Dialog(this)
            val inflater = LayoutInflater.from(this)
            val dialogView = inflater.inflate(R.layout.popup_4, null)

            val radioGroup: RadioGroup = dialogView.findViewById(R.id.radioGroup)
            val radioButton: RadioButton = dialogView.findViewById(R.id.radioButton)
            val radioButton1: RadioButton = dialogView.findViewById(R.id.radioButton1)
            val radioButton2: RadioButton = dialogView.findViewById(R.id.radioButton2)
            val radioButton3: RadioButton = dialogView.findViewById(R.id.radioButton3)
            val radioButton4: RadioButton = dialogView.findViewById(R.id.radioButton4)
            val radioButton5: RadioButton = dialogView.findViewById(R.id.radioButton5)

            when (choice) {
                0 -> {
                    radioButton.isChecked = true
                }
                1 -> {
                    radioButton1.isChecked = true
                }
                2 -> {
                    radioButton2.isChecked = true
                }
                3 -> {
                    radioButton3.isChecked = true
                }
                4 -> {
                    radioButton4.isChecked = true
                }
                5 -> {
                    radioButton5.isChecked = true
                }
            }

            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.radioButton -> {
                        choice = 0
                        typeCustomer.text = radioButton.text
                        notic.visibility = View.GONE
                        dialog.dismiss()
                    }
                    R.id.radioButton1 -> {
                        choice = 1
                        typeCustomer.text = radioButton1.text
                        notic.visibility = View.GONE
                        dialog.dismiss()
                    }
                    R.id.radioButton2 -> {
                        choice = 2
                        typeCustomer.text = radioButton2.text
                        notic.visibility = View.GONE
                        dialog.dismiss()
                    }
                    R.id.radioButton3 -> {
                        choice = 3
                        typeCustomer.text = radioButton3.text
                        notic.visibility = View.GONE
                        dialog.dismiss()
                    }
                    R.id.radioButton4 -> {
                        choice = 4
                        typeCustomer.text = radioButton4.text
                        notic.visibility = View.GONE
                        dialog.dismiss()
                    }
                    R.id.radioButton5 -> {
                        choice = 5
                        typeCustomer.text = radioButton5.text
                        notic.visibility = View.GONE
                        dialog.dismiss()
                    }
                }
            }

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(dialogView)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val window = dialog.window
            val layoutParams = window?.attributes
            layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT // Kích thước ngang theo match_parent
            layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT // Kích thước chiều cao tự động
            window?.attributes = layoutParams

            dialog.show()
        }

        val title:EditText = findViewById(R.id.editTextText)
        title.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val maxCharacterCount = 50 // Số lượng ký tự tối đa cho phép

                val inputText = s.toString().trim()

                if (inputText.isNotEmpty()) {
                    count1.visibility = View.VISIBLE
                    count1.text = "${inputText.length}/50"
                }

                if (inputText.length > maxCharacterCount) {
                    val truncatedText = inputText.substring(0, maxCharacterCount)
                    title.setText(truncatedText)
                    title.setSelection(truncatedText.length)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                title.removeTextChangedListener(this)

                val input = s.toString().replace(" ", "")

                if (input.isNotEmpty()) {
                    notic1.visibility = View.GONE
                } else {
                    notic1.visibility = View.VISIBLE
                }

                title.addTextChangedListener(this)
            }
        })

        val content:EditText = findViewById(R.id.editTextText1)
        content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val maxCharacterCount = 200 // Số lượng ký tự tối đa cho phép

                val inputText = s.toString().trim()

                if (inputText.isNotEmpty()) {
                    count2.visibility = View.VISIBLE
                    count2.text = "${inputText.length}/200"
                }

                if (inputText.length > maxCharacterCount) {
                    val truncatedText = inputText.substring(0, maxCharacterCount)
                    title.setText(truncatedText)
                    title.setSelection(truncatedText.length)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                content.removeTextChangedListener(this)

                val input = s.toString().replace(" ", "")

                if (input.isNotEmpty()) {
                    notic2.visibility = View.GONE
                } else {
                    notic2.visibility = View.VISIBLE
                }

                content.addTextChangedListener(this)
            }
        })

        val btnSendCmt: Button = findViewById(R.id.btn_sendcmt)
        btnSendCmt.setOnClickListener {
            if (progressBar.progress == 86) {
                progressBar.progress += 14
            }

            if (typeCustomer.text.isEmpty()) {
                notic.visibility = View.VISIBLE
            } 
            if (title.text.isEmpty()) {
                notic1.visibility = View.VISIBLE
            } 
            if (content.text.isEmpty()) {
                notic2.visibility = View.VISIBLE
            } 
            
            if (typeCustomer.text.isNotEmpty() && title.text.isNotEmpty() && content.text.isNotEmpty()) {
                val currentDate = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val time = currentDate.format(formatter)

                money *= 2
                location *= 2
                clean *= 2
                service *= 2
                convenience *= 2

                val point = (money.toDouble() + location.toDouble() + clean.toDouble() + service.toDouble() + convenience.toDouble()) / 5

                CommentUtils.addComment(ID_Owner!!, ID_Hotel!!, ID_Purchase.text.toString(), time, point, content.text.toString(), money, location, clean, service, convenience, typeCustomer.text.toString(), title.text.toString()) {result ->
                    val noticDialog = AlertDialog.Builder(this)
                        .setTitle("Cảm ơn ý kiến của quý khách!")
                        .setMessage("Bài đánh giá của quý khách sẽ sớm được đăng!")
                        .setCancelable(false)
                        .setNegativeButton("Đóng") {_,_ ->
                            val intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Xóa các activity trước đó và chỉ hiển thị MainActivity
                            intent.putExtra("replaceChannel", "myRoom")
                            intent.putExtra("idUser", ID_Owner)
                            setResult(Activity.RESULT_OK, intent)
                            startActivity(intent)
                            finish()
                        }
                        .show()
                }
            }
        }
    }
}