package com.example.angodafake

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.Adapter.CommentAdapter
import com.example.angodafake.Adapter.MyCommentAdapter
import com.example.angodafake.Utilities.CommentUtils
import com.example.angodafake.db.Comment

class MyComment : AppCompatActivity() {
    private var myCommentList: MutableList<Comment> = mutableListOf()
    private lateinit var myCommentField: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_my_comment)

        val btnBack: ImageButton = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        myCommentField = findViewById(R.id.myCommentField)
        val imageView: ImageView = findViewById(R.id.imageView)
        val textView: TextView = findViewById(R.id.textView)
        val textView1: TextView = findViewById(R.id.textView1)

        val layoutManager: RecyclerView.LayoutManager
        var linearAdapter: MyCommentAdapter

        layoutManager = LinearLayoutManager(this)
        myCommentField.layoutManager = layoutManager
        myCommentField.setHasFixedSize(true)

        CommentUtils.getCommentsByIDOwner(intent.getStringExtra("id_user")!!) {comments ->
            myCommentList.clear()
            myCommentList = comments
            println(myCommentList)

            if (myCommentList.isEmpty()) {
                imageView.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
                textView1.visibility = View.VISIBLE

                myCommentField.visibility = View.GONE
            } else {
                imageView.visibility = View.GONE
                textView.visibility = View.GONE
                textView1.visibility = View.GONE

                myCommentField.visibility = View.VISIBLE

                linearAdapter = MyCommentAdapter(this, myCommentList)
                myCommentField.adapter = linearAdapter
            }
        }
    }
}