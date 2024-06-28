package com.example.angodafake

import android.app.AlertDialog
import android.icu.text.DecimalFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.angodafake.Utilities.HotelUtils
import com.example.angodafake.Utilities.PictureUtils
import com.example.angodafake.Utilities.VoucherUtils
import com.squareup.picasso.Picasso

class VoucherAddForm : AppCompatActivity() {
    private val decimalFormat = DecimalFormat("#,###")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_voucher_add_form)

        //Các biến thông báo
        val notic: TextView = findViewById(R.id.notic)
        val notic1: TextView = findViewById(R.id.notic1)
        val notic2: TextView = findViewById(R.id.notic2)
        val notic3: TextView = findViewById(R.id.notic3)
        val notic4: TextView = findViewById(R.id.notic4)
        val notic5: TextView = findViewById(R.id.notic5)

        notic.visibility = View.GONE
        notic1.visibility = View.GONE
        notic2.visibility = View.GONE
        notic3.visibility = View.GONE
        notic4.visibility = View.GONE
        notic5.visibility = View.GONE

        var isMoney: Int = 0
        val idHotel = intent.getStringExtra("idHotel").toString()

        val imageHotel: ImageView = findViewById(R.id.imageRoom)
        PictureUtils.getPictureByHotelID(idHotel) { picture ->
            Picasso.get().load(picture.url).into(imageHotel)
        }

        val btnBack: ImageButton = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        val nameHotel: TextView = findViewById(R.id.nameHotel)
        HotelUtils.getHotelByID(idHotel!!) { hotel ->
            nameHotel.text = hotel.name
        }

        //Thuộc tính chung
        val limited_price: EditText = findViewById(R.id.limited_price)
        limited_price.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                limited_price.removeTextChangedListener(this)

                val input = s.toString().replace("[,.]".toRegex(), "")

                if (input.isNotEmpty()) {
                    val number = input.toLongOrNull()
                    val formattedText = number?.let { decimalFormat.format(it) } ?: input

                    limited_price.setText(formattedText)
                    limited_price.setSelection(formattedText.length)

                    notic.visibility = View.GONE
                } else {
                    notic.visibility = View.VISIBLE
                }

                limited_price.addTextChangedListener(this)
            }
        })

        val discount_quantity: EditText = findViewById(R.id.discount_quantity)
        discount_quantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                discount_quantity.removeTextChangedListener(this)

                val input = s.toString().replace("[,.]".toRegex(), "")

                if (input.isNotEmpty()) {
                    notic4.visibility = View.GONE
                } else {
                    notic4.visibility = View.VISIBLE
                }

                discount_quantity.addTextChangedListener(this)
            }
        })

        //Thuộc tính của giảm giá theo phần trăm
        val discount_percentage: EditText = findViewById(R.id.discount_percentage)
        discount_percentage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                discount_percentage.removeTextChangedListener(this)

                val input = s.toString().replace("[,.]".toRegex(), "")

                if (input.isNotEmpty()) {
                    notic1.visibility = View.GONE
                } else {
                    notic1.visibility = View.VISIBLE
                }

                discount_percentage.addTextChangedListener(this)
            }
        })

        val discount_max: EditText = findViewById(R.id.discount_max)
        discount_max.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                discount_max.removeTextChangedListener(this)

                val input = s.toString().replace("[,.]".toRegex(), "")

                if (input.isNotEmpty()) {
                    val number = input.toLongOrNull()
                    val formattedText = number?.let { decimalFormat.format(it) } ?: input

                    discount_max.setText(formattedText)
                    discount_max.setSelection(formattedText.length)

                    notic2.visibility = View.GONE
                } else {
                    notic2.visibility = View.VISIBLE
                }

                discount_max.addTextChangedListener(this)
            }
        })

        //Thuộc tính của giảm giá theo số tiền
        val discount_money: EditText = findViewById(R.id.discount_money)
        discount_money.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                discount_money.removeTextChangedListener(this)

                val input = s.toString().replace("[,.]".toRegex(), "")

                if (input.isNotEmpty()) {
                    val number = input.toLongOrNull()
                    val formattedText = number?.let { decimalFormat.format(it) } ?: input

                    discount_money.setText(formattedText)
                    discount_money.setSelection(formattedText.length)

                    notic3.visibility = View.GONE
                } else {
                    notic3.visibility = View.VISIBLE
                }

                discount_money.addTextChangedListener(this)
            }
        })

        val byMoney: RelativeLayout = findViewById(R.id.byMoney)
        val byPercentage: RelativeLayout = findViewById(R.id.byPercentage)

        byMoney.visibility = View.GONE
        byPercentage.visibility = View.GONE

        val radioGroup: RadioGroup = findViewById(R.id.radioGroup)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                //Chọn giảm giá theo phần trăm
                R.id.radioButton -> {
                    byPercentage.visibility = View.VISIBLE
                    byMoney.visibility = View.GONE
                    isMoney = 2
                    notic5.visibility = View.GONE
                }
                //Chọn giảm giá theo số tiền
                R.id.radioButton1 -> {
                    byMoney.visibility = View.VISIBLE
                    byPercentage.visibility =View.GONE
                    isMoney = 1
                    notic5.visibility = View.GONE
                }
            }
        }

        val btnCreate: Button = findViewById(R.id.button)
        btnCreate.setOnClickListener {
            var limited: Double = -1.0
            var quantity: Int = -1
            var max: Double = -1.0
            var money: Double = -1.0
            var percentage: Int = -1

            if (limited_price.text.toString().isEmpty()) {
                notic.visibility = View.VISIBLE
            } else {
                limited = limited_price.text.toString().replace(",","").toDouble()
                notic.visibility = View.GONE
            }

            if (discount_quantity.text.toString().isEmpty()) {
                notic4.visibility = View.VISIBLE
            } else {
                quantity = discount_quantity.text.toString().toInt()
                notic4.visibility = View.GONE
            }

            if (isMoney == 0) {
                notic5.visibility = View.VISIBLE
            } else if (isMoney == 1) {
                if (discount_money.text.toString().isEmpty()) {
                    notic3.visibility = View.VISIBLE
                } else {
                    money = discount_money.text.toString().replace(",","").toDouble()
                    max = 0.0
                    percentage = 0
                    notic3.visibility = View.GONE
                }
                notic5.visibility = View.GONE
            } else {
                if (discount_percentage.text.toString().isNotEmpty() && discount_max.text.toString().isNotEmpty()) {
                    max = discount_max.text.toString().replace(",","").toDouble()
                    percentage = discount_percentage.text.toString().toInt()
                    money = 0.0
                    notic1.visibility = View.GONE
                    notic2.visibility = View.GONE
                } else if (discount_percentage.text.toString().isEmpty()) {
                    notic1.visibility = View.VISIBLE
                    notic2.visibility = View.GONE
                } else if (discount_max.text.toString().isEmpty()) {
                    notic1.visibility = View.GONE
                    notic2.visibility = View.VISIBLE
                } else {
                    notic1.visibility = View.VISIBLE
                    notic2.visibility = View.VISIBLE
                }
                notic5.visibility = View.GONE
            }

            println(max)
            println(limited)
            println(percentage)
            println(money)
            println(quantity)
            println(isMoney)

            if (max != -1.0 && limited != -1.0 && percentage != -1 && money != -1.0 && quantity != -1) {
                VoucherUtils.addVoucher(idHotel, max, limited, percentage, money, quantity) { result ->
                    val noticDialog = AlertDialog.Builder(this)
                        .setTitle("Hoàn thành!")
                        .setMessage("Phiếu Voucher của bạn đã được tạo thành công!")
                        .setCancelable(false)
                        .setNegativeButton("Đóng") {_,_ -> finish()}
                        .show()
                }
            }
        }
    }
}