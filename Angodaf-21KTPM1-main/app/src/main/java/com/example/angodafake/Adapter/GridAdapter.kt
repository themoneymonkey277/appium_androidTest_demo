package com.example.angodafake.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.angodafake.R
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso

class GridAdapter(private var context: Context, private var imgList: ArrayList<String>) : BaseAdapter() {
    private class ViewHolder(row: View?){
        var pic: ImageView? = null
        var btn_remove: Button? = null
        val storageRef = Firebase.storage.reference

        init{
            pic = row?.findViewById(R.id.gridImage)
            btn_remove = row?.findViewById(R.id.btn_remove)
        }
    }

    override fun getCount(): Int {
        return imgList.size
    }

    override fun getItem(position: Int): Any {
        return imgList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.grid_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val pic = imgList[position]
        Picasso.get().load(pic)
            .into(viewHolder.pic!!)

        viewHolder.btn_remove?.setOnClickListener {
            viewHolder.storageRef.child(pic.substring(pic.lastIndexOf('/') + 1, pic.indexOf('?'))).delete().addOnSuccessListener {
                imgList.removeAt(position)
                notifyDataSetChanged()
            }.addOnFailureListener {
                Toast.makeText(this.context, pic, Toast.LENGTH_SHORT).show()
            }

        }
        return view
    }

}