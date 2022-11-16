package com.example.woof.AuthFiles.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.woof.R
import com.example.woof.viewmodel.AppViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class SignUp : Fragment(), AdapterView.OnItemSelectedListener {

    private var userType = arrayOf("Select your usertype", "Normal User", "Seller", "Doctor")
    private lateinit var signUpEmail: TextInputEditText
    private lateinit var signUpName: TextInputEditText
    private lateinit var signUpTradeLicenseNoLayout: LinearLayout
    private lateinit var signUpRegistrationNoLayout: LinearLayout
    private lateinit var nextBtn: CardView
    private lateinit var signUpProgressbar: LottieAnimationView
    private lateinit var signInText: TextView
    private lateinit var signUpPhoneNumber: TextInputEditText
    private lateinit var signUpWhiteLayout: LinearLayout
    private var spinnerText: String = "Select your usertype"
    private var appViewModel: AppViewModel? = null
    private val validPhoneNumberPattern by lazy { "^(\\+\\d{1,3}[- ]?)?\\d{10}\$" }
    private val emailPattern by lazy { "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+" }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        appViewModel = ViewModelProvider(requireActivity())[AppViewModel::class.java]


        signUpName = view.findViewById(R.id.signUpName_default)
        nextBtn = view.findViewById(R.id.signUpNextButton_default)
        signInText = view.findViewById(R.id.signInText)
        signUpPhoneNumber = view.findViewById(R.id.signUpPhone_default)
        signUpProgressbar = view.findViewById(R.id.signUpProgressbar)
        signUpWhiteLayout = view.findViewById(R.id.signUpWhiteLayout)
        signUpEmail = view.findViewById(R.id.signUpEmail_default)

        signInText.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.authFrameLayout, SignIn()).commit()
        }

        val mySpinner: Spinner = view.findViewById(R.id.mySpinner)

        val aa = ArrayAdapter(
            requireActivity().applicationContext,
            android.R.layout.simple_spinner_item,
            userType
        )
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        with(mySpinner)
        {
            adapter = aa
            setSelection(0, true)
            onItemSelectedListener = this@SignUp
            gravity = Gravity.CENTER
            setPopupBackgroundResource(R.color.cream)
        }

        nextBtn.setOnClickListener {
            next()
        }

        return view
    }

    override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {

        spinnerText = userType[position]
        when (position) {
            1 -> {
                Toast.makeText(
                    requireContext(),
                    "Normal user selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
            2 -> {
                Toast.makeText(
                    requireActivity().applicationContext,
                    "Seller selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
            3 -> {
                Toast.makeText(
                    requireContext(),
                    "doctor selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                Toast.makeText(
                    requireContext(),
                    "Nothing selected...select user type to continue",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Toast.makeText(
            requireContext(),
            "Nothing selected...select user type to continue",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun next() {
        signUpProgressbar.visibility = View.VISIBLE
        signUpWhiteLayout.visibility = View.VISIBLE

        if (spinnerText == "Select your usertype") {
            signUpProgressbar.visibility = View.GONE
            signUpWhiteLayout.visibility = View.GONE
            Toast.makeText(
                requireContext(),
                "Nothing selected...select user type to continue",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val name = signUpName.text.toString()
            val phoneNumber = signUpPhoneNumber.text.toString()
            val email = signUpEmail.text.toString()

            var allRight = true

            if (name.isEmpty()) {
                signUpName.error = "Enter your name"
                allRight = false
            }
            if (phoneNumber.isEmpty() || !phoneNumber.matches(validPhoneNumberPattern.toRegex())) {
                signUpPhoneNumber.error = "Enter valid phone number"
                allRight = false
            }
            if (email.isEmpty() || !email.matches(emailPattern.toRegex())) {
                signUpEmail.error = "Enter valid email address"
                allRight = false
            }

            if (!allRight) {
                signUpProgressbar.visibility = View.GONE
                signUpWhiteLayout.visibility = View.GONE
                Toast.makeText(
                    requireActivity().applicationContext,
                    "Fill up your details properly",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                var nextFragment = Fragment()
                val bundle = Bundle()
                bundle.putString("name", name)
                bundle.putString("number", phoneNumber)
                bundle.putString("email", email)

                when (spinnerText) {
                    "Normal User" -> nextFragment = SignUpNormalUser()
                    "Seller" -> nextFragment = SignUpSeller()
                    "Doctor" -> nextFragment = SignUpDoctor()
                }
                nextFragment.arguments = bundle
                signUpProgressbar.visibility = View.GONE
                signUpWhiteLayout.visibility = View.GONE
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.authFrameLayout, nextFragment).commit()
            }
        }

    }

}