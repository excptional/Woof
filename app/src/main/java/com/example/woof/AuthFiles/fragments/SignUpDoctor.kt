package com.example.woof.AuthFiles.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.woof.R
import com.example.woof.SplashScreen
import com.example.woof.repo.Response
import com.example.woof.viewmodel.AppViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignUpDoctor : Fragment(), AdapterView.OnItemSelectedListener {

    private var specialityList = arrayListOf(
        "Select speciality",
        "Anesthesia and analgesia",
        "Animal welfare",
        "Behavioral medicine",
        "Clinical pharmacology",
        "Dentistry",
        "Dermatology",
        "Emergency and critical care",
        "Internal medicine",
        "Internal medicine",
        "Nutrition",
        "Ophthalmology",
        "Pathology",
        "Others"
    )

    private lateinit var signUpPasswordDoctor: TextInputEditText
    private lateinit var signUpPasswordLayoutDoctor: TextInputLayout
    private lateinit var signUpCPasswordDoctor: TextInputEditText
    private lateinit var signUpCPasswordLayoutDoctor: TextInputLayout
    private lateinit var signUpRegistrationNo: TextInputEditText
    private lateinit var prevBtnDoctor: CardView
    private lateinit var signUpBtnDoctor: CardView
    private lateinit var signUpProgressbarDoctor: LottieAnimationView
    private lateinit var signUpWhiteLayoutDoctor: LinearLayout
    private lateinit var doctorSpinner: Spinner
    private lateinit var signUpDocSpecialityTextDoctor: TextInputEditText
    private lateinit var signUpDocSpecialityTextLayoutDoctor: LinearLayout

    private var appViewModel: AppViewModel? = null
    private lateinit var docSpeciality: String

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up_doctor, container, false)

        val name = requireArguments().getString("name")
        val number = requireArguments().getString("number")
        val email = requireArguments().getString("email")

        signUpPasswordDoctor = view.findViewById(R.id.signUpPassword_doctor)
        signUpPasswordLayoutDoctor = view.findViewById(R.id.signUpPasswordLayout_doctor)
        signUpCPasswordDoctor = view.findViewById(R.id.signUpCPassword_doctor)
        signUpCPasswordLayoutDoctor = view.findViewById(R.id.signUpCPasswordLayout_doctor)
        signUpRegistrationNo = view.findViewById(R.id.signUpRegNo_doctor)
        prevBtnDoctor = view.findViewById(R.id.prevBtn_doctor)
        signUpBtnDoctor = view.findViewById(R.id.signUpBtn_doctor)
        signUpProgressbarDoctor = view.findViewById(R.id.signUpProgressbar_doctor)
        signUpWhiteLayoutDoctor = view.findViewById(R.id.signUpWhiteLayout_doctor)
        doctorSpinner = view.findViewById(R.id.doctorSpinner)
        signUpDocSpecialityTextDoctor = view.findViewById(R.id.signUpDocSpecialityText_doctor)
        signUpDocSpecialityTextLayoutDoctor =
            view.findViewById(R.id.signUpDocSpecialityTextLayout_doctor)

        appViewModel = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        val doctorAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            specialityList
        )
        doctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(doctorSpinner)
        {
            adapter = doctorAdapter
            setSelection(0, true)
            onItemSelectedListener = this@SignUpDoctor
            gravity = android.view.Gravity.CENTER
            setPopupBackgroundResource(com.example.woof.R.color.cream)
        }

        signUpBtnDoctor.setOnClickListener {
            signUp(name!!, number!!, email!!)
        }

        prevBtnDoctor.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.authFrameLayout, SignUp()).commit()
        }

        return view
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position == 0) {
            docSpeciality = ""
            signUpDocSpecialityTextLayoutDoctor.visibility = View.GONE
            Toast.makeText(
                requireContext(),
                "Nothing selected...select to continue",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            if (specialityList[position] == "Others")
                signUpDocSpecialityTextLayoutDoctor.visibility = View.VISIBLE
            else
                signUpDocSpecialityTextLayoutDoctor.visibility = View.GONE
            docSpeciality = specialityList[position]
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(
            requireContext(),
            "Nothing selected...select to continue",
            Toast.LENGTH_SHORT
        ).show()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun signUp(
        name: String,
        number: String,
        email: String,
    ) {
        signUpProgressbarDoctor.visibility = View.VISIBLE
        signUpWhiteLayoutDoctor.visibility = View.VISIBLE
        val password = signUpPasswordDoctor.text.toString()
        val cPassword = signUpCPasswordDoctor.text.toString()
        val registrationNo = signUpRegistrationNo.text.toString()
        var allRight = true
        signUpPasswordLayoutDoctor.isPasswordVisibilityToggleEnabled = true
        signUpCPasswordLayoutDoctor.isPasswordVisibilityToggleEnabled = true

        if(docSpeciality == "Others")
            docSpeciality = signUpDocSpecialityTextDoctor.text.toString()

        if (docSpeciality.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Select your speciality",
                Toast.LENGTH_SHORT
            ).show()
            allRight = false
        }


        if (docSpeciality == "Others" && signUpDocSpecialityTextDoctor.text.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                "Write your speciality",
                Toast.LENGTH_SHORT
            ).show()
            allRight = false
        }

        if (password.isEmpty()) {
            signUpPasswordLayoutDoctor.isPasswordVisibilityToggleEnabled = false
            signUpPasswordDoctor.error = "Enter your password"
            allRight = false
        }
        if (password.length < 6) {
            signUpPasswordLayoutDoctor.isPasswordVisibilityToggleEnabled = false
            signUpPasswordDoctor.error = "Enter password more than 6 characters"
            allRight = false
        }
        if (password != cPassword) {
            signUpCPasswordLayoutDoctor.isPasswordVisibilityToggleEnabled = false
            signUpCPasswordDoctor.error = "Password not matched"
            allRight = false
        }
        if (registrationNo.isEmpty()) {
            signUpRegistrationNo.error = "Enter your trade license no."
            allRight = false
        }
        if(docSpeciality.isEmpty()){

        }

        if (!allRight) {
            signUpProgressbarDoctor.visibility = View.GONE
            signUpWhiteLayoutDoctor.visibility = View.GONE
            if(docSpeciality.isNotEmpty() || !(docSpeciality == "Others" && signUpDocSpecialityTextDoctor.text.isNullOrEmpty()))
            Toast.makeText(
                requireActivity().applicationContext,
                "Fill up your details properly",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            appViewModel!!.doctorRegister(
                name,
                registrationNo,
                number,
                email,
                password,
                docSpeciality
            )
            appViewModel!!.response.observe(viewLifecycleOwner) {
                when (it) {
                    is Response.Success -> {
                        signUpProgressbarDoctor.visibility = View.GONE
                        signUpWhiteLayoutDoctor.visibility = View.GONE
                        requireActivity().startActivity(Intent(activity, SplashScreen::class.java))
                        this.onDestroy()
                    }
                    is Response.Failure -> {
                        Toast.makeText(
                            requireActivity().applicationContext,
                            it.errorMassage,
                            Toast.LENGTH_SHORT
                        ).show()
                        signUpProgressbarDoctor.visibility = View.GONE
                        signUpWhiteLayoutDoctor.visibility = View.GONE
                    }
                    else -> {}
                }
            }
        }
    }
}