package com.example.angodafake

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        FirebaseApp.initializeApp(this)

        auth = Firebase.auth

        val handler = Handler()
        handler.postDelayed(nextActivity(), 1500)

    }

    private fun nextActivity(): Runnable {
        val currentUser = auth.currentUser
        return Runnable {
            if (currentUser != null){
                //Da dang nhap truoc do
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("idUser", currentUser.uid)
                startActivity(intent)
                finish()
            }
            else{
                //Chua dang nhap, chuyen sang man hinh dang nhap
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}