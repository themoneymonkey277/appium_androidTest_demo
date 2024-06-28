package com.example.angodafake

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.angodafake.db.Picture_Hotel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.storage.storage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [UploadImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadImageFragment(private var idUser: String) : Fragment() {
    // TODO: Rename and change types of parameters
    private var fromFrag: String? = null

    private lateinit var btn_back : Button
    private lateinit var btn_upload: Button
    private lateinit var uploadImage: ImageView
    private lateinit var progressBar: ProgressBar
    private var imageUri: Uri? = null

    private val storageRef = Firebase.storage.reference

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
        val view = inflater.inflate(R.layout.fragment_upload_image, container, false)
        initUI(view)

        btn_back.setOnClickListener {
            prevStepWithData("")
        }

        progressBar.visibility = View.INVISIBLE

        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
                if (it.resultCode == Activity.RESULT_OK){
                    val data = it.data
                    imageUri = data?.data
                    uploadImage.setImageURI(imageUri)
                } else{
                    Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show()
                }
        }

        uploadImage.setOnClickListener {
            val photoPicker = Intent()
            photoPicker.setAction(Intent.ACTION_GET_CONTENT)
            photoPicker.setType("image/*")
            activityResultLauncher.launch(photoPicker)
        }

        btn_upload.setOnClickListener {
            if (imageUri != null){
                uploadToFirebase(imageUri!!)
            } else{
                Toast.makeText(requireContext(), "Please select image", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun uploadToFirebase(imageUri: Uri) {
        val imgRef = storageRef.child("${System.currentTimeMillis()}.${getFileExtension(imageUri)}")
        imgRef.putFile(imageUri)
            .addOnSuccessListener {
                imgRef.downloadUrl.addOnSuccessListener {
                    progressBar.visibility = View.INVISIBLE
                    prevStepWithData(it.toString())
                }
            }.addOnProgressListener {
                progressBar.visibility = View.VISIBLE
            }.addOnFailureListener {
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getFileExtension(imageUri: Uri): String {
        val contentResolver = requireContext().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(imageUri))!!
    }

    private fun prevStepWithData(pic : String){
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

        if (pic != ""){
            val pics = arguments?.getStringArrayList("pics")
            pics?.add(pic)
            arg.putStringArrayList("pics", pics)
        } else{
            val pics = arguments?.getStringArrayList("pics")
            arg.putStringArrayList("pics", pics)
        }

        if (fromFrag == "edit"){
            arg.putString("from", arguments?.getString("from"))
            arg.putString("idHotel", arguments?.getString("idHotel"))
            arg.putString("date", arguments?.getString("date"))
            arg.putString("dateType", arguments?.getString("date"))
            arg.putString("searchStr", arguments?.getString("searchStr"))
        }

        val addHotelImageFragment = addHotelImageFragment(idUser)
        addHotelImageFragment.arguments = arg

        val mainActivity = requireActivity() as MainActivity
        mainActivity.replaceFragment(addHotelImageFragment)
    }

    private fun initUI(view: View){
        btn_back = view.findViewById(R.id.btn_back)
        btn_upload = view.findViewById(R.id.btn_upload)
        uploadImage = view.findViewById(R.id.uploadImage)
        progressBar = view.findViewById(R.id.progressBar)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UploadImageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(fromFrag: String, idUser: String) =
            UploadImageFragment(idUser).apply {
                arguments = Bundle().apply {
                    putString("from", fromFrag)
                }
            }
    }
}