package com.example.angodafake

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
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
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.angodafake.Utilities.HotelUtils
import com.example.angodafake.Utilities.PictureUtils
import com.example.angodafake.Utilities.VoucherUtils
import com.squareup.picasso.Picasso
import java.text.DecimalFormatSymbols

class VoucherEditForm : AppCompatActivity() {
    private val decimalFormat = DecimalFormat("#,###")
    private lateinit var idVoucher: String
    private lateinit var idHotel: String
    var isMoney: Boolean = true
    fun format(money: Double): String {
        val formatSymbols = DecimalFormatSymbols()
        formatSymbols.groupingSeparator = ','

        val decimalFormat = java.text.DecimalFormat("#,##0", formatSymbols)
        return decimalFormat.format(money)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_voucher_edit_form)

        //Các biến thông báo
        val notic: TextView = findViewById(R.id.notic)
        val notic1: TextView = findViewById(R.id.notic1)
        val notic2: TextView = findViewById(R.id.notic2)
        val notic3: TextView = findViewById(R.id.notic3)
        val notic4: TextView = findViewById(R.id.notic4)

        notic.visibility = View.GONE
        notic1.visibility = View.GONE
        notic2.visibility = View.GONE
        notic3.visibility = View.GONE
        notic4.visibility = View.GONE

        idVoucher = intent.getStringExtra("id").toString()
        idHotel = intent.getStringExtra("idHotel").toString()

        val imageHotel: ImageView = findViewById(R.id.imageRoom)
        PictureUtils.getPictureByHotelID(idHotel) { picture ->
            Picasso.get().load(picture.url).into(imageHotel)
        }

        val btnBack: ImageButton = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        val nameHotel: TextView = findViewById(R.id.nameHotel)
        HotelUtils.getHotelByID(idHotel) { hotel ->
            nameHotel.text = hotel.name
        }

        val min_discount: EditText = findViewById(R.id.editTextNumber)
        min_discount.setText(intent.getStringExtra("min")?.let { format(it.toDouble()) })
        min_discount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                min_discount.removeTextChangedListener(this)

                val input = s.toString().replace("[,.]".toRegex(), "")

                if (input.isNotEmpty()) {
                    val number = input.toLongOrNull()
                    val formattedText = number?.let { decimalFormat.format(it) } ?: input

                    min_discount.setText(formattedText)
                    min_discount.setSelection(formattedText.length)

                    notic.visibility = View.GONE
                } else {
                    notic.visibility = View.VISIBLE
                }

                min_discount.addTextChangedListener(this)
            }
        })

        val money_discount: EditText = findViewById(R.id.editTextNumber4)
        money_discount.setText(intent.getStringExtra("money")?.let { format(it.toDouble()) })
        money_discount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                money_discount.removeTextChangedListener(this)

                val input = s.toString().replace("[,.]".toRegex(), "")

                if (input.isNotEmpty()) {
                    val number = input.toLongOrNull()
                    val formattedText = number?.let { decimalFormat.format(it) } ?: input

                    money_discount.setText(formattedText)
                    money_discount.setSelection(formattedText.length)

                    notic3.visibility = View.GONE
                } else {
                    notic3.visibility = View.VISIBLE
                }

                money_discount.addTextChangedListener(this)
            }
        })

        val percent_discount: EditText = findViewById(R.id.editTextNumber2)
        percent_discount.setText(intent.getStringExtra("percent"))
        percent_discount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                percent_discount.removeTextChangedListener(this)

                val input = s.toString().replace("[,.]".toRegex(), "")

                if (input.isNotEmpty()) {
                    notic1.visibility = View.GONE
                } else {
                    notic1.visibility = View.VISIBLE
                }

                percent_discount.addTextChangedListener(this)
            }
        })


        val max_discount: EditText = findViewById(R.id.editTextNumber3)
        max_discount.setText(intent.getStringExtra("max")?.let { format(it.toDouble()) })
        max_discount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                max_discount.removeTextChangedListener(this)

                val input = s.toString().replace("[,.]".toRegex(), "")

                if (input.isNotEmpty()) {
                    val number = input.toLongOrNull()
                    val formattedText = number?.let { decimalFormat.format(it) } ?: input

                    max_discount.setText(formattedText)
                    max_discount.setSelection(formattedText.length)

                    notic2.visibility = View.GONE
                } else {
                    notic2.visibility = View.VISIBLE
                }

                max_discount.addTextChangedListener(this)
            }
        })

        val radioButton: RadioButton = findViewById(R.id.radioButton)
        val radioButton1: RadioButton = findViewById(R.id.radioButton1)

        val byMoney: RelativeLayout = findViewById(R.id.byMoney)
        val byPercentage: RelativeLayout = findViewById(R.id.byPercentage)

        byMoney.visibility = View.GONE
        byPercentage.visibility = View.GONE

        if (money_discount.text.toString() == "0") {
            radioButton.isChecked = true
            isMoney = false

            byMoney.visibility = View.GONE
            byPercentage.visibility = View.VISIBLE

        } else {
            radioButton1.isChecked = true
            isMoney = true

            byMoney.visibility = View.VISIBLE
            byPercentage.visibility = View.GONE
        }

        val quantity_discount: EditText = findViewById(R.id.editTextNumber5)
        quantity_discount.setText(intent.getStringExtra("quantity"))
        quantity_discount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                quantity_discount.removeTextChangedListener(this)

                val input = s.toString().replace("[,.]".toRegex(), "")

                if (input.isNotEmpty()) {
                    notic4.visibility = View.GONE
                } else {
                    notic4.visibility = View.VISIBLE
                }

                quantity_discount.addTextChangedListener(this)
            }
        })

        val edit_btn: Button = findViewById(R.id.button)
        edit_btn.setOnClickListener {
            var limited: Double = -1.0
            var quantity: Int = -1
            var max: Double = -1.0
            var money: Double = -1.0
            var percentage: Int = -1

            if (min_discount.text.toString().isEmpty()) {
                notic.visibility = View.VISIBLE
            } else {
                limited = min_discount.text.toString().replace(",","").toDouble()
                notic.visibility = View.GONE
            }

            if (quantity_discount.text.toString().isEmpty()) {
                notic4.visibility = View.VISIBLE
            } else {
                quantity = quantity_discount.text.toString().toInt()
                notic4.visibility = View.GONE
            }

            if (isMoney) {
                if (money_discount.text.toString().isEmpty()) {
                    notic3.visibility = View.VISIBLE
                } else {
                    money = money_discount.text.toString().replace(",","").toDouble()
                    max = 0.0
                    percentage = 0
                    notic3.visibility = View.GONE
                }

            } else {
                if (percent_discount.text.toString().isNotEmpty() && max_discount.text.toString().isNotEmpty()) {
                    max = max_discount.text.toString().replace(",","").toDouble()
                    percentage = percent_discount.text.toString().toInt()
                    money = 0.0
                    notic1.visibility = View.GONE
                    notic2.visibility = View.GONE
                } else if (percent_discount.text.toString().isEmpty()) {
                    notic1.visibility = View.VISIBLE
                    notic2.visibility = View.GONE
                } else if (max_discount.text.toString().isEmpty()) {
                    notic1.visibility = View.GONE
                    notic2.visibility = View.VISIBLE
                } else {
                    notic1.visibility = View.VISIBLE
                    notic2.visibility = View.VISIBLE
                }
            }

            if (max != -1.0 && limited != -1.0 && percentage != -1 && money != -1.0 && quantity != -1) {
                VoucherUtils.editVoucher(idVoucher, idHotel, max, limited, percentage, money, quantity) { result ->
                    if (result == "success") {
                        val noticDialog = AlertDialog.Builder(this)
                            .setTitle("Hoàn thành!")
                            .setMessage("Phiếu Voucher của bạn đã được chỉnh sửa thành công!")
                            .setCancelable(false)
                            .setNegativeButton("Đóng") {_,_ ->
                                val intent = Intent(this, VoucherList::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Xóa các activity trước đó và chỉ hiển thị MainActivity
                                intent.putExtra("idHotel", idHotel)
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
}