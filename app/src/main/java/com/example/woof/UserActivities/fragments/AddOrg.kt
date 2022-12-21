package com.example.woof.UserActivities.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.example.woof.R
import com.example.woof.viewmodel.DBViewModel
import com.google.android.material.textfield.TextInputEditText

class AddOrg : Fragment() {

    private lateinit var orgNameEditText: TextInputEditText
    private lateinit var orgLocationEditText: TextInputEditText
    private lateinit var orgNumberEditText: TextInputEditText
    private lateinit var orgWebEditText: TextInputEditText
    private lateinit var orgCityEditText: TextInputEditText
    private lateinit var publishOrg: CardView
    private lateinit var whiteLayout: LinearLayout
    private lateinit var progressbar: LottieAnimationView
    private var dbViewModel: DBViewModel? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_org, container, false)

        val type = requireArguments().getString("type").toString()

        orgNameEditText = view.findViewById(R.id.orgName_addOrg)
        orgLocationEditText = view.findViewById(R.id.orgLocation_addOrg)
        orgNumberEditText = view.findViewById(R.id.orgNumber_addOrg)
        orgWebEditText = view.findViewById(R.id.orgWeb_addOrg)
        orgCityEditText = view.findViewById(R.id.orgCity_addOrg)
        publishOrg = view.findViewById(R.id.publish_addOrg)
        whiteLayout = view.findViewById(R.id.whiteLayout_addOrg)
        progressbar = view.findViewById(R.id.progressbar_addOrg)
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        publishOrg.setOnClickListener {
            publishOrg(type, view)
        }

        return view
    }

    private fun publishOrg(type: String, view: View) {
        val orgName = orgNameEditText.text.toString()
        val orgLocation = orgLocationEditText.text.toString()
        val orgNumber = orgNumberEditText.text.toString()
        val orgWebUrl = orgWebEditText.text.toString()
        val orgCity = orgCityEditText.text.toString()
        var allRight = true

        if (orgName.isEmpty()) {
            orgNameEditText.error = "Enter organization name"
            allRight = false
        }
        if (orgLocation.isEmpty()) {
            orgLocationEditText.error = "Enter organization location"
            allRight = false
        }
        if(orgNumber.isEmpty()){
            orgNumberEditText.error = "Enter organization contact number"
            allRight = false
        }
        if(orgCity.isEmpty()){
            orgCityEditText.error = "Enter city name(e.g. Kolkata, Delhi etc.)"
            allRight = false
        }

        if (!allRight) {
            progressbar.visibility = View.GONE
            whiteLayout.visibility = View.GONE
            Toast.makeText(
                requireActivity().applicationContext,
                "Fill up your details properly",
                Toast.LENGTH_SHORT
            ).show()
        }else{
            when(type){
                "Kennel" -> {
                    dbViewModel!!.addKennels(orgName, orgNumber, orgLocation, orgCity, orgWebUrl)
                }
                "PetShop" -> {
                    dbViewModel!!.addPetShops(orgName, orgNumber, orgLocation, orgCity, orgWebUrl)
                }
                "Training" -> {
                    dbViewModel!!.addTrainingCenter(orgName, orgNumber, orgLocation, orgCity, orgWebUrl)
                }
                "Grooming" -> {
                    dbViewModel!!.addGroomingCenter(orgName, orgNumber, orgLocation, orgCity, orgWebUrl)
                }
            }
            Toast.makeText(
                requireActivity().applicationContext,
                "Your organization publish successfully",
                Toast.LENGTH_LONG
            ).show()
            requireFragmentManager().popBackStack()
            Navigation.findNavController(view).navigate(R.id.nav_home_seller)
        }
    }

}