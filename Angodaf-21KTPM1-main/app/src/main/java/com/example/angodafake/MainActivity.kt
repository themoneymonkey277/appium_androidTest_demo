package com.example.angodafake

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.angodafake.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var idUser: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idUser = intent.getStringExtra("idUser")
        val replaceChannel = intent.getStringExtra("replaceChannel")
        val idOwner = intent.getStringExtra("idUser")
        if (replaceChannel == "myRoom") {
            replaceFragment(MyRoom(idOwner!!))
        } else if (replaceChannel == "Hotel_infor") {
            val nameHotel = intent.getStringExtra("nameHotel")
            replaceFragment(Home(idUser!!, nameHotel))
        } else {
            replaceFragment(Home(idUser!!, ""))
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.home -> replaceFragment(Home(idUser!!, ""))
                R.id.room -> replaceFragment(MyRoom(idUser!!))
                R.id.hotel -> replaceFragment(MyHotel(idUser!!))
                R.id.bookmark -> replaceFragment(Bookmark())
                R.id.profile -> replaceFragment(MyProfile(idUser!!))
            }
            true
        }
    }

    fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        // Cập nhật BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        when (fragment) {
            is Home -> bottomNavigationView.menu.findItem(R.id.home).isChecked = true
            is MyRoom -> bottomNavigationView.menu.findItem(R.id.room).isChecked = true
            is MyHotel -> bottomNavigationView.menu.findItem(R.id.hotel).isChecked = true
            is Bookmark -> bottomNavigationView.menu.findItem(R.id.bookmark).isChecked = true
            is MyProfile -> bottomNavigationView.menu.findItem(R.id.profile).isChecked = true
        }
    }
}