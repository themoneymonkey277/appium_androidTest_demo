package com.example.angodafake

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.angodafake.Utilities.PurchaseUtils
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar

class CancelPurchase : AppCompatActivity() {
    private fun showpopup_2(idPurchase:String, detail:String, reason:String, statusPurchase:String, idUser: String) {
        println("OK")
        val diaLog = Dialog(this)
        diaLog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        diaLog.setCancelable(false)
        diaLog.setContentView(R.layout.popup_2)
        diaLog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val window = diaLog.window
        val layoutParams = window?.attributes
        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT // Kích thước ngang theo match_parent
        layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT // Kích thước chiều cao tự động
        window?.attributes = layoutParams
        val btnMaintain: Button = diaLog.findViewById(R.id.btn_maintain)
        val btnCancel: Button = diaLog.findViewById(R.id.btn_cancel)

        btnMaintain.setOnClickListener {
            finish()
        }

        btnCancel.setOnClickListener {
            try {
                val currentDateTime = Calendar.getInstance().time
                val formatter = SimpleDateFormat("HH:mm:ss dd/MM/yyyy")
                val dateNow = formatter.format(currentDateTime)

                PurchaseUtils.cancelPurchase(idPurchase, detail, reason, statusPurchase, dateNow) { result ->
                    println(result)
                    Toast.makeText(baseContext, "Đã hủy thành công đặt phòng của bạn!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Xóa các activity trước đó và chỉ hiển thị MainActivity
                    intent.putExtra("replaceChannel", "myRoom")
                    intent.putExtra("idUser", idUser)
                    setResult(Activity.RESULT_OK, intent)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(baseContext, "Đã xảy ra lỗi khi hủy đặt phòng!", Toast.LENGTH_SHORT).show()
            }
        }

        diaLog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cancel_purchase)

        val myList = listOf("Chọn một trong số các lý do sau",
            "Đặt phòng không được xác nhận kịp thời",
            "Không quá tin tưởng vào uy tín của dịch vụ chúng tôi",
            "Lo lắng về sự an toàn cho vị trí khách sạn",
            "Quyết định chọn khách sạn khác trên Agoda",
            "Không thích chính sách hủy phòng",
            "Không hài lòng với cách thanh toán",
            "Tìm thấy giá thấp hơn trên mạng",
            "Tìm được giá thấp hơn qua dịch vụ địa phương",
            "Thiên tai",
            "Sẽ đặt khách sạn khác trên app của chúng tôi",
            "Sẽ đặt phòng trực tiếp với khách sạn",
            "Khác")

        val spinner: Spinner = findViewById(R.id.spinner)
        val btnBack: ImageButton = findViewById(R.id.btn_back)
        val btnContinue: Button = findViewById(R.id.btn_continue)
        val notic: TextView = findViewById(R.id.notic)

        val idUser = intent.getStringExtra("id_user")
        val idPurchase = intent.getStringExtra("id_purchase")
        val dateCome = intent.getStringExtra("date_come")
        var statusPurchase = intent.getStringExtra("status_purchase")
        var reason: String = ""
        val detail: String = "DA_HUY"

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateNow = LocalDate.now().format(formatter)

        val formattedDate = LocalDate.parse(dateNow, formatter)
        val dateComeDate = LocalDate.parse(dateCome, formatter)
        val daysBetween = ChronoUnit.DAYS.between(formattedDate, dateComeDate)

        if (daysBetween <= 5 || statusPurchase == "CHUA_THANH_TOAN") {
            statusPurchase = "KHONG_HOAN_TIEN"
        } else {
            statusPurchase = "CHUA_HOAN_TIEN"
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnContinue.setOnClickListener {
            val selectedPosition = spinner.selectedItemPosition
            if (selectedPosition == 0) {
                notic.text = "*Vui lòng chọn lý do hủy đặt phòng"
            } else {
                notic.text = ""
                if (idPurchase != null && dateNow != null && idUser != null) {
                    showpopup_2(idPurchase, detail, reason, statusPurchase, idUser)
                }
            }
        }

        ArrayAdapter(this, android.R.layout.simple_spinner_item, myList)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                reason = parent?.getItemAtPosition(position).toString()
            }
        }
    }
}