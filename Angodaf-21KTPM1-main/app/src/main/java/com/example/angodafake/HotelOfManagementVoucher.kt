package com.example.angodafake

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.Adapter.VoucherOfHotelAdapter
import com.example.angodafake.R
import com.example.angodafake.Utilities.HotelUtils
import com.example.angodafake.Utilities.VoucherUtils
import com.example.angodafake.db.Hotel

class HotelOfManagementVoucher : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_hotel_of_management_voucher)

        val idOwner = intent.getStringExtra("id_user")

        val btnBack: ImageButton = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        val hotelList: RecyclerView = findViewById(R.id.hotelList)
        val layoutManager: RecyclerView.LayoutManager
        var linearAdapter: VoucherOfHotelAdapter

        layoutManager = LinearLayoutManager(this)
        hotelList.layoutManager = layoutManager
        hotelList.setHasFixedSize(true)

        HotelUtils.getHotelByOwnerID(idOwner!!) { hotels ->
            linearAdapter = VoucherOfHotelAdapter(this, hotels)
            hotelList.adapter = linearAdapter

            linearAdapter.onItemClick = { contact ->
                val intent = Intent(this, VoucherList::class.java)
                intent.putExtra("idHotel", contact.ID)
                startActivity(intent)
            }
        }
    }
}