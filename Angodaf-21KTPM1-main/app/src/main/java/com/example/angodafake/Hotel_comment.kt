package com.example.angodafake

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.Adapter.CommentAdapter
import com.example.angodafake.Utilities.CommentUtils
import com.example.angodafake.db.Comment

class Hotel_comment : AppCompatActivity() {
    private var commentList: MutableList<Comment> = mutableListOf()
    private var filterList: MutableList<Comment> = mutableListOf()
    private lateinit var commentField: RecyclerView
    private lateinit var emptyField: RelativeLayout
    private var choice: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_hotel_comment)

        val btnBack:ImageButton = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        val point:TextView = findViewById(R.id.textView1)
        point.text = intent.getStringExtra("point")

        val status:TextView = findViewById(R.id.textView2)
        status.text = intent.getStringExtra("rateStatus")

        val quantityComment: TextView = findViewById(R.id.textView3)
        quantityComment.text = intent.getStringExtra("cmt")

        val cleanDouble = intent.getStringExtra("clean")?.toDouble()
        val clean1:ProgressBar = findViewById(R.id.bar_condition1)
        clean1.progress = (cleanDouble!!*10).toInt()
        val clean2: TextView = findViewById(R.id.point_condition1)
        clean2.text = cleanDouble.toString()

        val convenienceDouble = intent.getStringExtra("convenience")?.toDouble()
        val convenience1:ProgressBar = findViewById(R.id.bar_condition2)
        convenience1.progress = (convenienceDouble!!*10).toInt()
        val convenience2:TextView = findViewById(R.id.point_condition2)
        convenience2.text = convenienceDouble.toString()

        val locationDouble = intent.getStringExtra("location")?.toDouble()
        val location1:ProgressBar = findViewById(R.id.bar_condition3)
        location1.progress = (locationDouble!!*10).toInt()
        val location2:TextView = findViewById(R.id.point_condition3)
        location2.text = locationDouble.toString()

        val serviceDouble = intent.getStringExtra("service")?.toDouble()
        val service1:ProgressBar = findViewById(R.id.bar_condition4)
        service1.progress = (serviceDouble!!*10).toInt()
        val service2:TextView = findViewById(R.id.point_condition4)
        service2.text = serviceDouble.toString()

        val moneyDouble = intent.getStringExtra("money")?.toDouble()
        val money1:ProgressBar = findViewById(R.id.bar_condition5)
        money1.progress = (moneyDouble!!*10).toInt()
        val money2:TextView = findViewById(R.id.point_condition5)
        money2.text = moneyDouble.toString()

        var layoutManager: RecyclerView.LayoutManager
        var linearAdapter: CommentAdapter

        CommentUtils.getCommentsByIDHotel(intent.getStringExtra("idHotel")!!) {comments ->
            commentList.clear()
            commentList = comments

            emptyField = findViewById(R.id.emptyField)
            commentField = findViewById(R.id.commentField)

            if (commentList.isEmpty()) {
                emptyField.visibility = View.VISIBLE
                commentField.visibility = View.GONE
            } else {
                emptyField.visibility = View.GONE
                commentField.visibility = View.VISIBLE

                layoutManager = LinearLayoutManager(this)
                commentField.layoutManager = layoutManager
                commentField.setHasFixedSize(true)

                linearAdapter = CommentAdapter(this, commentList)
                commentField.adapter = linearAdapter
            }
        }

        val btnSort:TextView = findViewById(R.id.button)
        btnSort.setOnClickListener {
            val dialog = Dialog(this)
            val inflater = LayoutInflater.from(this)
            val dialogView = inflater.inflate(R.layout.popup_3, null)

            val btnClose: ImageButton = dialogView.findViewById(R.id.btn_close)
            btnClose.setOnClickListener {
                dialog.dismiss()
            }

            val radioGroup: RadioGroup = dialogView.findViewById(R.id.radioGroup)
            val radioButton: RadioButton = dialogView.findViewById(R.id.radioButton)
            val radioButton1:RadioButton = dialogView.findViewById(R.id.radioButton1)
            val radioButton2:RadioButton = dialogView.findViewById(R.id.radioButton2)
            val radioButton3:RadioButton = dialogView.findViewById(R.id.radioButton3)
            val radioButton4:RadioButton = dialogView.findViewById(R.id.radioButton4)
            val radioButton5:RadioButton = dialogView.findViewById(R.id.radioButton5)
            val radioButton6:RadioButton = dialogView.findViewById(R.id.radioButton6)

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
                6 -> {
                    radioButton6.isChecked = true
                }
            }

            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                filterList.clear()
                when (checkedId) {
                    R.id.radioButton -> {
                        for (comment in commentList) {
                            if (comment.point!! >= 9) {
                                filterList.add(comment)
                            }
                        }
                        choice = 0
                        btnSort.text = radioButton.text
                    }
                    R.id.radioButton1 -> {
                        for (comment in commentList) {
                            if (comment.point!! >= 8 && comment.point!! < 9) {
                                filterList.add(comment)
                            }
                        }
                        choice = 1
                        btnSort.text = radioButton1.text
                    }
                    R.id.radioButton2 -> {
                        for (comment in commentList) {
                            if (comment.point!! >= 7 && comment.point!! < 8) {
                                filterList.add(comment)
                            }
                        }
                        choice = 2
                        btnSort.text = radioButton2.text
                    }
                    R.id.radioButton3 -> {
                        for (comment in commentList) {
                            if (comment.point!! >= 6 && comment.point!! < 7) {
                                filterList.add(comment)
                            }
                        }
                        choice = 3
                        btnSort.text = radioButton3.text
                    }
                    R.id.radioButton4 -> {
                        for (comment in commentList) {
                            if (comment.point!! >= 5 && comment.point!! < 6) {
                                filterList.add(comment)
                            }
                        }
                        choice = 4
                        btnSort.text = radioButton4.text
                    }
                    R.id.radioButton5 -> {
                        for (comment in commentList) {
                            if (comment.point!! >= 3 && comment.point!! < 5) {
                                filterList.add(comment)
                            }
                        }
                        choice = 5
                        btnSort.text = radioButton5.text
                    }
                    R.id.radioButton6 -> {
                        for (comment in commentList) {
                            if (comment.point!! < 3) {
                                filterList.add(comment)
                            }
                        }
                        choice = 6
                        btnSort.text = radioButton6.text
                    }
                }
            }

            val btnAccept: Button = dialogView.findViewById(R.id.btn_accept)
            btnAccept.setOnClickListener {
                if (filterList.isEmpty()) {
                    emptyField.visibility = View.VISIBLE
                    commentField.visibility = View.GONE
                } else {
                    emptyField.visibility = View.GONE
                    commentField.visibility = View.VISIBLE

                    linearAdapter = CommentAdapter(this, filterList)
                    commentField.adapter = linearAdapter
                }

                dialog.dismiss()
            }

            val btnUnAccept: Button = dialogView.findViewById(R.id.btn_unaccept)
            btnUnAccept.setOnClickListener {
                emptyField.visibility = View.GONE
                commentField.visibility = View.VISIBLE

                linearAdapter = CommentAdapter(this, commentList)
                commentField.adapter = linearAdapter
                choice = -1
                btnSort.text = "Phân loại"
                dialog.dismiss()
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
    }
}