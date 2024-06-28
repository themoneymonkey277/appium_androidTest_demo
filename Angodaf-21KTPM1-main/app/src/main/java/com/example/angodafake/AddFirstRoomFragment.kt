package com.example.angodafake

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/**
 * A simple [Fragment] subclass.
 * Use the [AddFirstRoomFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFirstRoomFragment(private var idHotel: String, private var idUser: String) : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var btn_next: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_first_room, container, false)
        btn_next = view.findViewById(R.id.btn_next)
        btn_next.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.replaceFragment(AddRoomFragment(idHotel, idUser))
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
         * @return A new instance of fragment AddFirstRoomFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(idHotel: String, idUser: String) =
            AddFirstRoomFragment(idHotel, idUser).apply {
                arguments = Bundle().apply {
                }
            }
    }
}