package com.example.angodafake

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TabHost
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.angodafake.Adapter.ActiveVoucherAdapter
import com.example.angodafake.Adapter.InactiveVoucherAdapter
import com.example.angodafake.Utilities.VoucherUtils
import com.example.angodafake.db.Voucher

class VoucherList : AppCompatActivity() {
    private var activeVoucherList: MutableList<Voucher> = mutableListOf()
    private var inactiveVoucherList: MutableList<Voucher> = mutableListOf()
    private lateinit var idHotel: String

    private lateinit var activeVoucher: RecyclerView
    private lateinit var backGround1: ImageView
    private lateinit var textView1: TextView
    private lateinit var createNew1: Button
    private lateinit var addNew1: ImageButton

    private lateinit var inactiveVoucher: RecyclerView
    private lateinit var backGround2: ImageView
    private lateinit var textView2: TextView
    private lateinit var createNew2: Button
    private lateinit var addNew2: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_voucher_list)

        val btnBack: ImageButton = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        idHotel = intent.getStringExtra("idHotel").toString()

        activeVoucher = findViewById(R.id.activeVoucherList)
        backGround1 = findViewById(R.id.imageView1)
        textView1 = findViewById(R.id.text_no_place1)
        createNew1 = findViewById(R.id.btn_order1)
        addNew1 = findViewById(R.id.btn_add1)

        inactiveVoucher = findViewById(R.id.inactiveVoucherList)
        backGround2 = findViewById(R.id.imageView2)
        textView2 = findViewById(R.id.text_no_place2)
        createNew2 = findViewById(R.id.btn_order2)
        addNew2 = findViewById(R.id.btn_add2)

        VoucherUtils.getAllVouchers(idHotel) { vouchers ->
            activeVoucherList.clear()
            inactiveVoucherList.clear()

            for (voucher in vouchers) {
                if (voucher.quantity!! > 0) {
                    activeVoucherList.add(voucher)
                } else {
                    inactiveVoucherList.add(voucher)
                }
            }

            if (activeVoucherList.size == 0 ) {
                backGround1.visibility = View.VISIBLE
                textView1.visibility = View.VISIBLE
                createNew1.visibility = View.VISIBLE

                createNew1.setOnClickListener {
                    val intent = Intent(this, VoucherAddForm::class.java)
                    intent.putExtra("idHotel", idHotel)
                    startActivity(intent)
                }

                activeVoucher.visibility = View.GONE
                addNew1.visibility = View.GONE
            } else {
                backGround1.visibility = View.GONE
                textView1.visibility = View.GONE
                createNew1.visibility = View.GONE

                activeVoucher.visibility = View.VISIBLE
                addNew1.visibility = View.VISIBLE

                addNew1.setOnClickListener {
                    val intent = Intent(this, VoucherAddForm::class.java)
                    intent.putExtra("idHotel", idHotel)
                    startActivity(intent)
                }
            }

            if (inactiveVoucherList.size == 0) {
                backGround2.visibility = View.VISIBLE
                textView2.visibility = View.VISIBLE
                createNew2.visibility = View.VISIBLE

                createNew2.setOnClickListener {
                    val intent = Intent(this, VoucherAddForm::class.java)
                    intent.putExtra("idHotel", idHotel)
                    startActivity(intent)
                }

                inactiveVoucher.visibility = View.GONE
                addNew2.visibility = View.GONE
            } else {
                backGround2.visibility = View.GONE
                textView2.visibility = View.GONE
                createNew2.visibility = View.GONE

                inactiveVoucher.visibility = View.VISIBLE
                addNew2.visibility = View.VISIBLE

                addNew2.setOnClickListener {
                    val intent = Intent(this, VoucherAddForm::class.java)
                    intent.putExtra("idHotel", idHotel)
                    startActivity(intent)
                }
            }
        }

        val tabHost: TabHost = findViewById(R.id.tabHost)
        tabHost.setup()

        var tabSpec : TabHost.TabSpec?
        tabSpec = tabHost.newTabSpec("tab1")
        tabSpec.setIndicator("Còn hiệu lực", null)
        tabSpec.setContent(R.id.tab1)
        tabHost.addTab(tabSpec)

        tabSpec = tabHost.newTabSpec("tab2")
        tabSpec.setIndicator("Hết hạn", null)
        tabSpec.setContent(R.id.tab2)
        tabHost.addTab(tabSpec)

        setUpTab1()
        tabHost.setOnTabChangedListener { tabId ->
            when (tabId) {
                "tab1" -> {
                    setUpTab1()
                }
                "tab2" -> {
                    setUpTab2()
                }
            }
        }
    }

    private fun setUpTab1() {
        val layoutManager: RecyclerView.LayoutManager
        val linearAdapter: ActiveVoucherAdapter

        layoutManager = LinearLayoutManager(this)
        activeVoucher.layoutManager = layoutManager
        activeVoucher.setHasFixedSize(true)

        linearAdapter = ActiveVoucherAdapter(this, activeVoucherList)
        activeVoucher.adapter = linearAdapter
    }

    private fun setUpTab2() {
        val layoutManager: RecyclerView.LayoutManager
        val linearAdapter: InactiveVoucherAdapter

        layoutManager = LinearLayoutManager(this)
        inactiveVoucher.layoutManager = layoutManager
        inactiveVoucher.setHasFixedSize(true)

        linearAdapter = InactiveVoucherAdapter(this, inactiveVoucherList)
        inactiveVoucher.adapter = linearAdapter
    }
}