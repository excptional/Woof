package com.example.woof.UserActivities.fragments

import android.annotation.SuppressLint
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
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.google.android.material.textfield.TextInputEditText


class ContactUs : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var emailContact: TextInputEditText
    private lateinit var massageContact: TextInputEditText
    private lateinit var querySpinner: Spinner
    private lateinit var sendBtnContact: CardView
    private lateinit var progressbarContact: LottieAnimationView
    private lateinit var whiteLayoutContact: LinearLayout
    private val emailPattern by lazy { "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+" }
    private val passwordPattern by lazy { "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$" }
    private var massageSubject: String? = null
    private var email: String? = null
    private var password: String? = null
    private var massage: String? = null
    private var dbViewModel: DBViewModel? = null

    private val queryTypeList =
        mutableListOf("Select subject", "Suggestions", "Queries", "Support", "Report", "Other")

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_us, container, false)

        emailContact = view.findViewById(R.id.email_contact)
        massageContact = view.findViewById(R.id.massage_contact)
        sendBtnContact = view.findViewById(R.id.sendBtn_contact)
        querySpinner = view.findViewById(R.id.querySpinner_contact)
        whiteLayoutContact = view.findViewById(R.id.whiteLayout_contact)
        progressbarContact = view.findViewById(R.id.progressbar_contact)

        dbViewModel = ViewModelProvider(requireActivity())[DBViewModel::class.java]

        val aa = ArrayAdapter(
            requireActivity().applicationContext,
            android.R.layout.simple_spinner_item,
            queryTypeList
        )
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        with(querySpinner)
        {
            adapter = aa
            setSelection(0, true)
            onItemSelectedListener = this@ContactUs
            gravity = Gravity.CENTER
            setPopupBackgroundResource(R.color.white)
        }

        sendBtnContact.setOnClickListener {
            whiteLayoutContact.visibility = View.VISIBLE
            progressbarContact.visibility = View.VISIBLE

            email = emailContact.text.toString()
            massage = massageContact.text.toString()

            if (email.isNullOrEmpty() || !email!!.matches(emailPattern.toRegex())) {
                Toast.makeText(requireContext(), "Enter valid email", Toast.LENGTH_SHORT).show()
                emailContact.error = "Enter valid email address"
                whiteLayoutContact.visibility = View.GONE
                progressbarContact.visibility = View.GONE
            } else if (massageSubject.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Select subject first", Toast.LENGTH_SHORT).show()
                whiteLayoutContact.visibility = View.GONE
                progressbarContact.visibility = View.GONE
            } else if (massage.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Can't send an empty massage, write something first",
                    Toast.LENGTH_SHORT
                ).show()
                massageContact.error = "Write something here"
                whiteLayoutContact.visibility = View.GONE
                progressbarContact.visibility = View.GONE
            } else {
                dbViewModel!!.sendFeedback(email!!, massageSubject!!, massage!!)
                dbViewModel!!.feedbackData.observe(viewLifecycleOwner) {
                    if (it == "Feedback send") {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        emailContact.text = null
                        massageSubject = null
                        massageContact.text = null
                    } else Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    whiteLayoutContact.visibility = View.GONE
                    progressbarContact.visibility = View.GONE
                }
            }
        }

        return view
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position == 0) {
            massageSubject = null
            Toast.makeText(requireContext(), "Noting selected!", Toast.LENGTH_SHORT).show()
        } else massageSubject = queryTypeList[position]
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(requireContext(), "Noting selected!", Toast.LENGTH_SHORT).show()
    }

}