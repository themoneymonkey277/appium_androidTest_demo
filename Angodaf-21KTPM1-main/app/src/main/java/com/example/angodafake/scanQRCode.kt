package com.example.angodafake

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.CodeScannerView
import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.angodafake.Utilities.HotelUtils
import com.example.angodafake.Utilities.PurchaseUtils
import com.example.angodafake.Utilities.RoomUtils
import com.example.angodafake.Utilities.UserUtils
import com.google.firebase.database.core.utilities.Utilities

class ScanQRCodeActivity : AppCompatActivity() {
    private lateinit var scanQRCode: CodeScanner
    private lateinit var dialog: Dialog
    private lateinit var anim: LottieAnimationView
    private lateinit var idUser: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qrcode)

        dialog = Dialog(this)
        dialog.setContentView(R.layout.purchased)
        anim = dialog.findViewById(R.id.checkPurchase)
        dialog.setOnDismissListener(DialogInterface.OnDismissListener {
            finish()
        })

        idUser = intent.getStringExtra("idUser").toString()

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 123)
        } else {
            starScanning()
        }
    }

    private fun starScanning() {
        val scannerView: CodeScannerView = findViewById(R.id.codeScannerView)
        scanQRCode = CodeScanner(this, scannerView)
        scanQRCode.camera = CodeScanner.CAMERA_BACK
        scanQRCode.formats = CodeScanner.ALL_FORMATS
        scanQRCode.autoFocusMode = AutoFocusMode.SAFE
        scanQRCode.scanMode = ScanMode.SINGLE
        scanQRCode.isAutoFocusEnabled = true
        scanQRCode.isFlashEnabled = false
        scanQRCode.decodeCallback = DecodeCallback {
            runOnUiThread{
                PurchaseUtils.updatePurchaseStatus(it.text){ result ->
                    if (result == "success") {
                        // Xử lý khi cập nhật thành công
                        println(it.text)
                        PurchaseUtils.getPurchaseByID(it.text){ purchase ->
                            HotelUtils.getHotelByOwnerID(idUser){hotelList->
                                var check = false
                                for (hotel in hotelList){
                                    if (hotel.ID == purchase.ID_Hotel){
                                        check = true
                                        break
                                    }
                                }
                                if (check){
                                    UserUtils.getUserByID(purchase.ID_Owner!!){ user ->
                                        dialog.findViewById<TextView>(R.id.customerName).text = user!!.name
                                    }
                                    HotelUtils.getHotelByID(purchase.ID_Hotel!!){ hotel ->
                                        dialog.findViewById<TextView>(R.id.hotelName).text = hotel.name
                                        dialog.findViewById<TextView>(R.id.timeCheckIn).text = "${hotel.checkIn} ${purchase.date_come}"
                                        dialog.findViewById<TextView>(R.id.timeCheckOut).text = "${hotel.checkOut} ${purchase.date_go}"
                                    }
                                    RoomUtils.getRoomByID(purchase.ID_Hotel!!, purchase.ID_Room!!){ room ->
                                        dialog.findViewById<TextView>(R.id.roomID).text = room.type
                                    }
                                    dialog.findViewById<TextView>(R.id.textView54).text = "Vui lòng kiểm tra lại thông tin đặt phòng sớm nhất."
                                    showDialog(dialog, anim)
                                } else{
                                    Toast.makeText(this, "Không sở hữu khách sạn", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            }
                        }
                    } else {
                        // Xử lý khi cập nhật thất bại
//                        showDialog(dialog, anim)
                        println("fail")
                    }
                }
            }
        }
        scanQRCode.errorCallback = ErrorCallback {
            runOnUiThread {
                println("error")
            }
        }
        scannerView.setOnClickListener{
            scanQRCode.startPreview()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                starScanning()
            } else {

            }
        }
    }
    override fun onResume() {
        super.onResume()
        if (::scanQRCode.isInitialized){
            scanQRCode.startPreview()
        }
    }
    override fun onPause() {
        if (::scanQRCode.isInitialized){
            scanQRCode.releaseResources()
        }
        super.onPause()
    }

    private fun showDialog(dialog: Dialog, anim: LottieAnimationView){
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed(Runnable{
            anim.visibility = View.VISIBLE
            anim.playAnimation()
        }, 300)
    }
}