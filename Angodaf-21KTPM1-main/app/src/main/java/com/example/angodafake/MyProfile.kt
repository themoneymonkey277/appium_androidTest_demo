package com.example.angodafake

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.angodafake.Utilities.UserUtils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

// TODO: Rename parameter arguments, choose names that match

/**
 * A simple [Fragment] subclass.
 * Use the [MyProfile.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyProfile(private var idUser: String) : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var btn_bookmark: Button
    private lateinit var btn_logout: Button
    private lateinit var profileTittle: TextView
    private lateinit var btn_myHotel: Button
    private lateinit var btn_profile: Button
    private lateinit var btn_add_hotel: Button
    private lateinit var btn_my_comments: Button
    private lateinit var btn_chat : Button
    private lateinit var btn_changePw: Button

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
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        auth = Firebase.auth
        initUI(view)
        UserUtils.getNameByID(idUser) {
            profileTittle.text = "Chào mừng, $it!"
        }

        btn_profile.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.replaceFragment(EditInfoFragment(idUser))
        }

        btn_bookmark.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.replaceFragment(Bookmark())
        }

        btn_myHotel.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.replaceFragment(MyHotel(idUser))
        }

        btn_add_hotel.setOnClickListener {
            val arg = Bundle()

            arg.putString("from", "MyProfile")
            val AddHotelFragment = AddHotelFragment(idUser)
            AddHotelFragment.arguments = arg

            val mainActivity = requireActivity() as MainActivity
            mainActivity.binding.bottomNavigationView.selectedItemId = R.id.hotel
            mainActivity.replaceFragment(AddHotelFragment)
        }

        btn_chat.setOnClickListener {
            val intent = Intent(requireActivity(), ChatList::class.java)
            intent.putExtra("id_user", idUser)
            startActivity(intent)
        }

        btn_changePw.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.replaceFragment(ChangePwFragment(idUser))
        }

        btn_my_comments.setOnClickListener {
            val intent = Intent(requireActivity(), MyComment::class.java)
            intent.putExtra("id_user", idUser)
            startActivity(intent)
        }

        btn_logout.setOnClickListener {
            auth.signOut()
            showSuccessSnackBar("Đăng xuất thành công", it)

            val handler = Handler()
            handler.postDelayed({
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }, 500)

        }
        return view
    }

    private fun initUI(view: View){
        btn_bookmark = view.findViewById(R.id.btn_bookmark)
        btn_logout = view.findViewById(R.id.btn_logout)
        profileTittle = view.findViewById(R.id.profileTittle)
        btn_myHotel = view.findViewById(R.id.btn_my_hotel)
        btn_profile = view.findViewById(R.id.btn_profile)
        btn_add_hotel = view.findViewById(R.id.btn_add_hotel)
        btn_my_comments = view.findViewById(R.id.btn_my_comments)
        btn_chat = view.findViewById(R.id.btn_chat)
        btn_changePw = view.findViewById(R.id.btn_changePw)
    }

    private fun showSuccessSnackBar(msg: String, view: View) {
        val snackbar = Snackbar.make(view.rootView, msg, Snackbar.LENGTH_LONG)
        snackbar.view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3193FF"))
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
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
        fun newInstance(idUser: String) =
            MyProfile(idUser).apply {
                arguments = Bundle().apply {
                }
            }
    }

}