package com.example.woof.UserActivities.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.woof.R
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView

class BookDoctor : Fragment(), AdapterView.OnItemSelectedListener {

    private var species = arrayListOf(
        "Select your pet species",
        "Dog",
        "Cat",
        "Bird",
        "Rabbit",
        "Fish",
        "Cow",
        "Goat",
        "Sheep",
        "Panda",
        "Reptile",
        "Rat",
        "Others"
    )

    private var timings = arrayListOf(
        "Select timings",
        "9:00 AM to 12:30 PM",
        "2:00 PM to 5: 30",
        "6:00 PM to 9 PM"
    )

    private lateinit var timingsSpinner: Spinner
    private lateinit var speciesSpinner: Spinner
    private lateinit var writeIssue: TextInputEditText
    private lateinit var bookDoctor: CardView
    private lateinit var profileName: TextView
    private lateinit var profileImage: CircleImageView
    private lateinit var drSpeciality: TextView
    private var yourPetSpecies = ""
    private var yourTimings = ""
    private var appViewModel: AppViewModel? = null
    private var dbViewModel: DBViewModel? = null
    private lateinit var userName: String
    private lateinit var userImage: String
    private lateinit var selectedDate: String
    private lateinit var dateLayout: LinearLayout
    private lateinit var dateText: TextView
    private lateinit var myUser: FirebaseUser

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_doctor, container, false)

        val name = requireArguments().getString("name")
        val image = requireArguments().getString("image url")
        val speciality = requireArguments().getString("speciality")

        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        appViewModel!!.userdata.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                myUser = user
                getUserDataFromDatabase(user)
            }
        }

        timingsSpinner = view.findViewById(R.id.timingsSpinner_bookDoc)
        speciesSpinner = view.findViewById(R.id.speciesSpinner_bookDoc)
        writeIssue = view.findViewById(R.id.writeIssues_bookDoc)
        bookDoctor = view.findViewById(R.id.bookNow_bookDoc)
        profileName = view.findViewById(R.id.name_bookDoc)
        profileImage = view.findViewById(R.id.profileImage_bookDoc)
        drSpeciality = view.findViewById(R.id.speciality_bookDoc)
        dateText = view.findViewById(R.id.dateText_bookDoc)
        dateLayout = view.findViewById(R.id.dateLayout_bookDoc)

        profileName.text = name
        Glide.with(view).load(image).into(profileImage)
        drSpeciality.text = speciality

        val aa1 = ArrayAdapter(
            requireActivity().applicationContext,
            android.R.layout.simple_spinner_item,
            timings
        )
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        with(timingsSpinner)
        {
            adapter = aa1
            setSelection(0, true)
            onItemSelectedListener = this@BookDoctor
            gravity = Gravity.CENTER
            setPopupBackgroundResource(R.color.white)
        }

        val aa2 = ArrayAdapter(
            requireActivity().applicationContext,
            android.R.layout.simple_spinner_item,
            species
        )
        aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        with(speciesSpinner)
        {
            adapter = aa2
            setSelection(0, true)
            onItemSelectedListener = this@BookDoctor
            gravity = Gravity.CENTER
            setPopupBackgroundResource(R.color.white)
        }

        dateLayout.setOnClickListener {
            showCalender()
        }

        bookDoctor.setOnClickListener {
            if(selectedDate.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Select date first",
                    Toast.LENGTH_SHORT
                ).show()
            }else if (yourTimings.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Select timings first",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (yourPetSpecies.isEmpty()){
                Toast.makeText(
                    requireContext(),
                    "Select your pet species first",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (writeIssue.text.isNullOrEmpty()){
                Toast.makeText(
                    requireContext(),
                    "Write something about your pet condition",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                dbViewModel!!.bookDoctor(userName, userImage, myUser.uid, selectedDate, yourTimings, yourPetSpecies, writeIssue.text.toString())

                Toast.makeText(
                    requireContext(),
                    "Booking successfully",
                    Toast.LENGTH_SHORT
                ).show()
//                requireFragmentManager().popBackStack()
                Navigation.findNavController(it).navigate(R.id.nav_hospitals_and_clinics)
            }
        }

        return view
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent!!.id == R.id.timingsSpinner_bookDoc) {
            if (position == 0) {
                Toast.makeText(
                    requireContext(),
                    "Nothing selected...select to continue",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                yourTimings = timings[position]
            }
        } else {
            if (position == 0) {
                Toast.makeText(
                    requireContext(),
                    "Nothing selected...select to continue",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                yourPetSpecies = species[position]
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(
            requireContext(),
            "Nothing selected...select to continue",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun getUserDataFromDatabase(user: FirebaseUser) {
        dbViewModel!!.getProfileData(user)
        dbViewModel!!.profileData.observe(viewLifecycleOwner) { dataList ->
            userName = dataList[1]!!
            userImage = dataList[2]!!
        }
    }

    private fun showCalender() {
        val dialog = Dialog(requireContext())
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_calender)

        val calender: CalendarView = dialog.findViewById(R.id.calender)

        calender.setOnDateChangeListener { view, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth-${month + 1}-$year"
            dateText.text = selectedDate
            dialog.hide()
        }
        dialog.show()
    }

}