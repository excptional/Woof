package com.example.woof.AuthFiles.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.woof.R
import com.example.woof.SplashScreen
import com.example.woof.repo.Response
import com.example.woof.viewmodel.AppViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignUp : Fragment(), AdapterView.OnItemSelectedListener {

    private var userType = arrayOf("Select your usertype", "Normal User", "Seller", "Doctor")
    private lateinit var signUpName: TextInputEditText
    private lateinit var signUpEmail: TextInputEditText
    private lateinit var signUpPassword: TextInputEditText
    private lateinit var signUpPasswordLayout: TextInputLayout
    private lateinit var signUpPetName: TextInputEditText
    private lateinit var signUpRegistrationNo: TextInputEditText
    private lateinit var signUpTradeLicenseNo: TextInputEditText
    private lateinit var signUpTradeLicenseNoLayout: LinearLayout
    private lateinit var signUpRegistrationNoLayout: LinearLayout
    private lateinit var signUpPetNameLayout: LinearLayout
    private lateinit var signUpBtn: CardView
    private lateinit var signUpProgressbar: LottieAnimationView
    private lateinit var signInText: TextView
    private lateinit var signUpPhoneNumber: TextInputEditText
    private lateinit var signUpWhiteLayout: LinearLayout
    private var spinnerText: String = "Select your usertype"
    private val emailPattern by lazy { "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+" }
    private val validNumericPattern by lazy { "^(0|[1-9][0-9]*)\$" }
    private var appViewModel: AppViewModel? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        appViewModel = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        signUpName = view.findViewById(R.id.signUpName)
        signUpEmail = view.findViewById(R.id.signUpEmail)
        signUpPassword = view.findViewById(R.id.signUpPassword)
        signUpPasswordLayout = view.findViewById(R.id.signUpPasswordLayout)
        signUpBtn = view.findViewById(R.id.signUpButton)
        signUpProgressbar = view.findViewById(R.id.signUpProgressbar)
        signUpPetName = view.findViewById(R.id.signUpPetName)
        signUpRegistrationNo = view.findViewById(R.id.signUpRegistrationNo)
        signUpTradeLicenseNo = view.findViewById(R.id.signUpTradeLicense)
        signUpTradeLicenseNoLayout = view.findViewById(R.id.signUpTradeLicenseLayout)
        signUpRegistrationNoLayout = view.findViewById(R.id.signUpRegistrationNoLayout)
        signUpPetNameLayout = view.findViewById(R.id.signUpPetNameLayout)
        signInText = view.findViewById(R.id.signInText)
        signUpPhoneNumber = view.findViewById(R.id.signUpPhone)
        signUpWhiteLayout = view.findViewById(R.id.signUpWhiteLayout)

        signInText.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.authFrameLayout, SignIn()).commit()
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

        signUpBtn.setOnClickListener {
            signUp()
        }

        return view
    }

    override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {

        spinnerText = userType[position]
        when (position) {
            1 -> {
                signUpPetNameLayout.visibility = View.VISIBLE
                signUpRegistrationNoLayout.visibility = View.GONE
                signUpTradeLicenseNoLayout.visibility = View.GONE
            }
            2 -> {
                signUpPetNameLayout.visibility = View.GONE
                signUpRegistrationNoLayout.visibility = View.GONE
                signUpTradeLicenseNoLayout.visibility = View.VISIBLE
            }
            3 -> {
                signUpPetNameLayout.visibility = View.GONE
                signUpRegistrationNoLayout.visibility = View.VISIBLE
                signUpTradeLicenseNoLayout.visibility = View.GONE
            }
            else -> {
                signUpPetNameLayout.visibility = View.GONE
                signUpRegistrationNoLayout.visibility = View.GONE
                signUpTradeLicenseNoLayout.visibility = View.GONE
                Toast.makeText(
                    requireActivity().applicationContext,
                    "Nothing selected...select user type to continue",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Toast.makeText(
            requireActivity().applicationContext,
            "Nothing selected...select user type to continue",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun signUp() {
        signUpProgressbar.visibility = View.VISIBLE
        signUpWhiteLayout.visibility = View.VISIBLE

        if (spinnerText == "Select your usertype") {
            signUpProgressbar.visibility = View.GONE
            signUpWhiteLayout.visibility = View.GONE
            Toast.makeText(
                requireActivity().applicationContext,
                "Nothing selected...select user type to continue",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val name = signUpName.text.toString()
            val email = signUpEmail.text.toString()
            val password = signUpPassword.text.toString()
            val petName = signUpPetName.text.toString()
            val phoneNumber = signUpPhoneNumber.text.toString()
            val registrationNo = signUpRegistrationNo.text.toString()
            val tradeLicenseNo = signUpTradeLicenseNo.text.toString()
            var allRight = true

            if (name.isEmpty()) {
                signUpName.error = "Enter your name"
                allRight = false
            }
            if (email.isEmpty()) {
                signUpEmail.error = "Enter your email address"
                allRight = false
            }
            if (password.isEmpty()) {
                signUpPasswordLayout.isPasswordVisibilityToggleEnabled = false
                signUpPassword.error = "Enter your password"
                allRight = false
            }
            if (phoneNumber.isEmpty()) {
                signUpPhoneNumber.error = "Enter your phone number"
                allRight = false
            }
            if (!phoneNumber.matches(validNumericPattern.toRegex()) or (phoneNumber.length != 10)) {
                signUpPhoneNumber.error = "Enter valid phone number"
                allRight = false
            }
            if (!email.matches(emailPattern.toRegex())) {
                signUpEmail.error = "Enter valid email address"
                allRight = false
            }
            if (password.length < 6) {
                signUpPasswordLayout.isPasswordVisibilityToggleEnabled = false
                signUpPassword.error = "Enter password more than 6 characters"
                allRight = false
            }
            if (registrationNo.isEmpty() && tradeLicenseNo.isEmpty() && petName.isEmpty()) {
                when (spinnerText) {
                    "Normal User" -> signUpPetName.error = "Enter your pet name"
                    "Seller" -> signUpRegistrationNo.error = "Enter business Trade License Number"
                    "Doctor" -> signUpPetName.error = "Enter Registration Number"
                }
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
                when(spinnerText){
                    "Normal User" -> appViewModel!!.normalUserRegister(name, petName, phoneNumber, email, password)
                    "Seller" -> appViewModel!!.sellerRegister(name, tradeLicenseNo, phoneNumber, email, password)
                    "Doctor" -> appViewModel!!.doctorRegister(name, registrationNo, phoneNumber, email, password)
                }

                appViewModel!!.response.observe(
                    viewLifecycleOwner
                ) {
                    when (it) {
                        is Response.Success -> {
                            signUpProgressbar.visibility = View.GONE
                            signUpWhiteLayout.visibility = View.GONE
                            requireActivity().startActivity(Intent(activity, SplashScreen::class.java))
                        }
                        is Response.Failure -> {
                            Toast.makeText(
                                requireActivity().applicationContext,
                                it.errorMassage,
                                Toast.LENGTH_SHORT
                            ).show()
                            signUpProgressbar.visibility = View.GONE
                            signUpWhiteLayout.visibility = View.GONE
                        }
                        else -> {}
                    }
                }
            }
        }

    }

}