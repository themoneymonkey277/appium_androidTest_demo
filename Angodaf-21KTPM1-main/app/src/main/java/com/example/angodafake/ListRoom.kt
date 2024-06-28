package com.example.angodafake

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.Adapter.RoomAdapter
import com.example.angodafake.Utilities.RoomUtils
import com.example.angodafake.db.Rooms

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [ListRoom.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListRoom(private val idUser: String) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var roomAdapter: List<Rooms>
    private lateinit var rooms: List<Rooms>
    private lateinit var adapter: RoomAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_room, container, false)
        val nameTextView = view.findViewById<TextView>(R.id.nameHotelSearch)

        val args = arguments
        val itemPosition = args?.getString("hotelPosition")
        val searchText = args?.getString("searchText")
        val hotelName = args?.getString("hotelName")
        val hotelIds = args?.getStringArray("hotelIds")
        val saveIds = args?.getStringArray("saveIds")
        val checkIn = args?.getString("checkIn")
        val checkOut = args?.getString("checkOut")
        val numberOfRooms = args?.getInt("numberOfRooms")
        val numberOfGuests = args?.getInt("numberOfGuests")
        println(checkIn)
        println(checkOut)
        println(numberOfRooms)
        println(numberOfGuests)

        nameTextView.text = hotelName

        if (itemPosition != null) {
            RoomUtils.getRoomByHotelID(itemPosition){ fetchedRoomList   ->
                println(fetchedRoomList)

                rooms = fetchedRoomList
                rooms = rooms.filter { room ->
                    room.capacity!! >= numberOfGuests!! && (room.quantity!! - room.available!!) >= numberOfRooms!!
                }
                val intArray = IntArray(rooms.size)
                // Gán tất cả các phần tử của mảng intArray bằng 1
                intArray.fill(1)

                view.findViewById<ImageView>(R.id.returnHotel).setOnClickListener {
                    val arg = Bundle()
                    arg.putString("hotelPosition", itemPosition)
                    arg.putString("searchText", searchText)
                    arg.putStringArray("hotelIds", hotelIds)
                    arg.putStringArray("saveIds", saveIds)
                    arg.putString("checkIn", checkIn)
                    arg.putString("checkOut", checkOut)
                    arg.putInt("numberOfRooms", numberOfRooms!!)
                    arg.putInt("numberOfGuests", numberOfGuests!!)

                    val filterFragment = Hotel_infor(idUser)
                    filterFragment.arguments = arg

                    val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, filterFragment)
                        .addToBackStack(null)
                        .commit()
                }

                val roomsRecyclerView = view.findViewById<RecyclerView>(R.id.contactsRV)
                roomAdapter = ArrayList(rooms)
                adapter = RoomAdapter(requireContext(), roomAdapter, intArray, checkIn!!, checkOut!!, itemPosition)
                roomsRecyclerView.adapter = adapter
                layoutManager = LinearLayoutManager(requireContext())
                roomsRecyclerView.layoutManager = layoutManager
                roomsRecyclerView.setHasFixedSize(true)

                adapter.setOnItemClickListener(object : RoomAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        // Xử lý khi item được click
                    }

                    override fun onPlusClick(position: Int) {
                        intArray[position] = intArray[position] + 1
                        adapter.notifyItemChanged(position)
                    }

                    override fun onMinusClick(position: Int) {
                        if(intArray[position] > 1) {
                            intArray[position] = intArray[position] - 1
                        }
                        else{
                            intArray[position] = 1
                        }
                        adapter.notifyItemChanged(position)
                    }

                    override fun onBookRoomClick(position: Int) {
                        val intent = Intent(activity, BookRoom::class.java)
                        intent.putExtra("checkInTime", checkIn)
                        intent.putExtra("checkOutTime", checkOut)
                        intent.putExtra("hotelName", hotelName)
                        intent.putExtra("hotelID", itemPosition)
                        intent.putExtra("roomID", position.toString())
                        intent.putExtra("roomQuantity", intArray[position].toString())
                        intent.putExtra("idUser", idUser)
                        startActivity(intent)
                    }
                })
            }
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyProfile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, idUser: String) =
            ListRoom(idUser).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}