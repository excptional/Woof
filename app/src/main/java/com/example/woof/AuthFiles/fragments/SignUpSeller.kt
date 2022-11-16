package com.example.woof.AuthFiles.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.woof.R
import com.example.woof.SplashScreen
import com.example.woof.repo.Response
import com.example.woof.repo.Usertype
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class SignUpSeller : Fragment() {
    private lateinit var signUpPasswordSeller: TextInputEditText
    private lateinit var signUpPasswordLayoutSeller: TextInputLayout
    private lateinit var signUpCPasswordSeller: TextInputEditText
    private lateinit var signUpCPasswordLayoutSeller: TextInputLayout
    private lateinit var signUpUploadDocumentTextSeller: TextView
    private lateinit var signUpClearDocumentSeller: ImageView
    private lateinit var signUpTradeLicenseNoSeller: TextInputEditText
    private lateinit var signUpWhiteLayoutSeller: LinearLayout
    private lateinit var signUpProgressbarSeller: LottieAnimationView
    private lateinit var prevBtnSeller: CardView
    private lateinit var signUpBtnSeller: CardView
    private val emailPattern by lazy { "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+" }
    private var appViewModel: AppViewModel? = null
    private var dbViewModel: DBViewModel? = null
    private var docUrl: String? = null

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(3, 4)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setOutputCompressQuality(50)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up_seller, container, false)

        appViewModel = ViewModelProvider(requireActivity())[AppViewModel::class.java]
        dbViewModel = ViewModelProvider(requireActivity())[DBViewModel::class.java]

        val name = requireArguments().getString("name")
        val number = requireArguments().getString("number")
        val email = requireArguments().getString("email")

        signUpPasswordSeller = view.findViewById(R.id.signUpPassword_seller)
        signUpPasswordLayoutSeller = view.findViewById(R.id.signUpPasswordLayout_seller)
        signUpCPasswordSeller = view.findViewById(R.id.signUpCPassword_seller)
        signUpCPasswordLayoutSeller = view.findViewById(R.id.signUpCPasswordLayout_seller)
        signUpTradeLicenseNoSeller = view.findViewById(R.id.signUpTradeLicense_seller)
        signUpUploadDocumentTextSeller = view.findViewById(R.id.uploadDocumentText_seller)
        signUpClearDocumentSeller = view.findViewById(R.id.clearDocument_seller)
        signUpProgressbarSeller = view.findViewById(R.id.signUpProgressbar_seller)
        signUpWhiteLayoutSeller = view.findViewById(R.id.signUpWhiteLayout_seller)
        prevBtnSeller = view.findViewById(R.id.prevBtn_seller)
        signUpBtnSeller = view.findViewById(R.id.signUpBtn_seller)

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
            if (uri != null) {
                signUpProgressbarSeller.visibility = View.VISIBLE
                dbViewModel!!.uploadTradeLicDoc(uri)
                getDocUrl()
                signUpUploadDocumentTextSeller.text = uri.lastPathSegment
                signUpClearDocumentSeller.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireContext(), "No image is uploaded", Toast.LENGTH_SHORT).show()
            }
        }

        signUpUploadDocumentTextSeller.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }

        signUpClearDocumentSeller.setOnClickListener {
            signUpUploadDocumentTextSeller.text = null
            signUpClearDocumentSeller.visibility = View.INVISIBLE
        }

        signUpBtnSeller.setOnClickListener {
            signUp(name!!, number!!, email!!)
        }

        prevBtnSeller.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.authFrameLayout, SignUp()).commit()
        }

        return view
    }

    private fun getDocUrl() {
        dbViewModel!!.dbLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    dbViewModel!!.tradeLicUrl.observe(viewLifecycleOwner){
                        docUrl = it!!
                        signUpProgressbarSeller.visibility = View.GONE
                    }

                    Toast.makeText(requireContext(), "Upload document image successfully", Toast.LENGTH_SHORT).show()
                }
                is Response.Failure -> {
                    signUpProgressbarSeller.visibility = View.GONE
                    signUpProgressbarSeller.visibility = View.GONE
                    Toast.makeText(requireContext(), it.errorMassage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signUp(name: String, number: String, email: String) {

        signUpProgressbarSeller.visibility = View.VISIBLE
        signUpWhiteLayoutSeller.visibility = View.VISIBLE
        val password = signUpPasswordSeller.text.toString()
        val cPassword = signUpCPasswordSeller.text.toString()
        val tradeLicenseNo = signUpTradeLicenseNoSeller.text.toString()
        var allRight = true

        if (password.isEmpty()) {
            signUpPasswordLayoutSeller.isPasswordVisibilityToggleEnabled = false
            signUpPasswordSeller.error = "Enter your password"
            allRight = false
        }
        if (password.length < 6) {
            signUpPasswordLayoutSeller.isPasswordVisibilityToggleEnabled = false
            signUpPasswordSeller.error = "Enter password more than 6 characters"
            allRight = false
        }
        if (password != cPassword) {
            signUpCPasswordLayoutSeller.isPasswordVisibilityToggleEnabled = false
            signUpCPasswordSeller.error = "Password not matched"
            allRight = false
        }
        if (tradeLicenseNo.isEmpty()) {
            signUpTradeLicenseNoSeller.error = "Enter your trade license no."
            allRight = false
        }
        if(signUpUploadDocumentTextSeller.text.isEmpty()){
            Toast.makeText(requireContext(), "Upload your trade license image in jpg format", Toast.LENGTH_SHORT).show()
            allRight = false
        }

        if (!allRight) {
            signUpProgressbarSeller.visibility = View.GONE
            signUpWhiteLayoutSeller.visibility = View.GONE
            Toast.makeText(
                requireActivity().applicationContext,
                "Fill up your details properly",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            appViewModel!!.sellerRegister(name, tradeLicenseNo, docUrl, number, email, password)
            appViewModel!!.response.observe(viewLifecycleOwner) {
                when (it) {
                    is Response.Success -> {
                        signUpProgressbarSeller.visibility = View.GONE
                        signUpWhiteLayoutSeller.visibility = View.GONE
                        requireActivity().startActivity(Intent(activity, SplashScreen::class.java))
                    }
                    is Response.Failure -> {
                        Toast.makeText(
                            requireActivity().applicationContext,
                            it.errorMassage,
                            Toast.LENGTH_SHORT
                        ).show()
                        signUpProgressbarSeller.visibility = View.GONE
                        signUpWhiteLayoutSeller.visibility = View.GONE
                    }
                    else -> {}
                }
            }
        }
    }
}