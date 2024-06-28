package com.example.angodafake

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import com.example.angodafake.Adapter.GridAdapter
import com.example.angodafake.Utilities.HotelUtils
import com.example.angodafake.Utilities.PictureUtils
import com.example.angodafake.db.Hotel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [addHotelImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class addHotelImageFragment(private var idUser: String) : Fragment() {
    // TODO: Rename and change types of parameters
    private var fromFrag: String? = null

    private lateinit var btn_back: ImageButton
    private lateinit var fab: FloatingActionButton
    private lateinit var progressBar1: ProgressBar
    private lateinit var btn_add: Button
    private lateinit var gridView: GridView
    private lateinit var adapter: GridAdapter
    private lateinit var picList : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fromFrag = it.getString("from")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_hotel_image, container, false)

        initUI(view)

        adapter = GridAdapter(requireContext(), picList)
        gridView.adapter = adapter

        btn_back.setOnClickListener {
            prevStepWithData()
        }

        fab.setOnClickListener{
            nextStepWithData()
        }

        progressBar1.setOnClickListener {
            prevStepWithData()
        }

        btn_add.setOnClickListener {
            if (picList.size != 0){
                val ID_Owner = idUser
                val name = arguments?.getString("hotelName")
                val phoneNumber = arguments?.getString("phoneN")
                val locationDetail = arguments?.getString("locationDetail")
                val city = arguments?.getString("city")
                val description = arguments?.getString("description")
                val conveniences = arguments?.getString("convenient")
                val highlight = arguments?.getString("highlight")
                val star =  arguments?.getInt("star")
                val checkIn = arguments?.getString("checkin")
                val checkOut = arguments?.getString("checkout")
                val merchantCode = arguments?.getString("merchantCode")
                val longitude = arguments?.getString("longitude")!!.toDouble()
                val latitude = arguments?.getString("latitude")!!.toDouble()
                val hotel = Hotel(null, ID_Owner, name, phoneNumber, locationDetail, city, description, conveniences, highlight, star, null, null, checkIn, checkOut, merchantCode, longitude, latitude,)
                if (fromFrag == "edit"){
                    HotelUtils.updateHotel(arguments?.getString("idHotel")!!, hotel){
                        if (it != null){
                            PictureUtils.updateHotelPictures(it, picList)

                            val arg = Bundle()
                            arg.putString("date", arguments?.getString("date"))
                            arg.putString("dateType", arguments?.getString("dateType"))
                            arg.putString("searchStr", arguments?.getString("searchStr"))
                            val myHotel = MyHotel(idUser)
                            myHotel.arguments = arg
                            val mainActivity = requireActivity() as MainActivity
                            showSuccessSnackBar("Cập nhật thông tin thành công.", view)
                            mainActivity.replaceFragment(myHotel)
                        }
                        else{
                            showSnackBar("Cập nhật thông tin thất bại.", view)
                        }
                    }
                } else{
                    HotelUtils.addHotel(hotel){
                        PictureUtils.addHotelPictures(it,picList)
                        val mainActivity = requireActivity() as MainActivity
                        mainActivity.replaceFragment(AddFirstRoomFragment(it, idUser))
                        showSuccessSnackBar("Thêm khách sạn thành công.", view)
                    }
                }
            } else{
                showSnackBar("Thêm ít nhất 1 ảnh của khách sạn.", view)
            }

        }
        return view
    }

    private fun prevStepWithData(){
        val arg = Bundle()

        arg.putString("from", fromFrag)
        arg.putString("hotelName", arguments?.getString("hotelName"))
        arg.putString("city", arguments?.getString("city"))
        arg.putString("locationDetail", arguments?.getString("locationDetail"))
        arg.putString("longitude", arguments?.getString("longitude"))
        arg.putString("latitude", arguments?.getString("latitude"))
        arg.putInt("star", arguments?.getInt("star")!!)
        arg.putString("phoneN", arguments?.getString("phoneN"))
        arg.putString("description", arguments?.getString("description"))
        arg.putString("convenient", arguments?.getString("convenient"))
        arg.putString("highlight", arguments?.getString("highlight"))
        arg.putString("checkin", arguments?.getString("checkin"))
        arg.putString("checkout", arguments?.getString("checkout"))
        arg.putString("merchantCode", arguments?.getString("merchantCode"))

        arg.putStringArrayList("pics", picList)
        if (fromFrag == "edit"){
            arg.putString("idHotel", arguments?.getString("idHotel"))
            arg.putString("date", arguments?.getString("date"))
            arg.putString("dateType", arguments?.getString("dateType"))
            arg.putString("searchStr", arguments?.getString("searchStr"))
        }

        val addHotelFragment = AddHotelFragment(idUser)
        addHotelFragment.arguments = arg

        val mainActivity = requireActivity() as MainActivity
        mainActivity.replaceFragment(addHotelFragment)
    }

    private fun nextStepWithData(){
        val arg = Bundle()

        arg.putString("from", fromFrag)
        arg.putString("hotelName", arguments?.getString("hotelName"))
        arg.putString("city", arguments?.getString("city"))
        arg.putString("locationDetail", arguments?.getString("locationDetail"))
        arg.putString("longitude", arguments?.getString("longitude"))
        arg.putString("latitude", arguments?.getString("latitude"))
        arg.putInt("star", arguments?.getInt("star")!!)
        arg.putString("phoneN", arguments?.getString("phoneN"))
        arg.putString("description", arguments?.getString("description"))
        arg.putString("convenient", arguments?.getString("convenient"))
        arg.putString("highlight", arguments?.getString("highlight"))
        arg.putString("checkin", arguments?.getString("checkin"))
        arg.putString("checkout", arguments?.getString("checkout"))
        arg.putString("merchantCode", arguments?.getString("merchantCode"))

        arg.putStringArrayList("pics", picList)
        if (fromFrag == "edit"){
            arg.putString("from", arguments?.getString("edit"))
            arg.putString("idHotel", arguments?.getString("idHotel"))
            arg.putString("date", arguments?.getString("date"))
            arg.putString("dateType", arguments?.getString("dateType"))
            arg.putString("searchStr", arguments?.getString("searchStr"))
        }

        val uploadImageFragment = UploadImageFragment(idUser)
        uploadImageFragment.arguments = arg

        val mainActivity = requireActivity() as MainActivity
        mainActivity.replaceFragment(uploadImageFragment)
    }

    private fun showSnackBar(msg: String, view: View) {
        val snackbar = Snackbar.make(view.rootView, msg, Snackbar.LENGTH_LONG)
        // Đổi màu background của Snackbar
        snackbar.view.backgroundTintList = ColorStateList.valueOf(Color.RED)
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }
    private fun showSuccessSnackBar(msg: String, view: View) {
        val snackbar = Snackbar.make(view.rootView, msg, Snackbar.LENGTH_LONG)
        snackbar.view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3193FF"))
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }

    private fun initUI(view: View){
        fab = view.findViewById(R.id.floatingActionButton)
        btn_back = view.findViewById(R.id.btn_back)
        progressBar1 = view.findViewById(R.id.progressBar1)
        btn_add = view.findViewById(R.id.btn_add)

        gridView = view.findViewById(R.id.gridView)

        picList = ArrayList()
        val tmp = arguments?.getStringArrayList("pics")
        if (tmp != null){
            picList.addAll(tmp)
        }

        if (fromFrag == "edit"){
            view.findViewById<TextView>(R.id.addHotel_title).text = "Chỉnh sửa thông tin khách sạn"
            btn_add.text = "Hoàn tất chỉnh sửa"
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment addHotelImageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(fromFrag: String, idUser: String) =
            addHotelImageFragment(idUser).apply {
                arguments = Bundle().apply {
                    putString("from", fromFrag)
                }
            }
    }
}