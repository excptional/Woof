package com.example.woof.UserActivities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import com.example.woof.R

class HomeSeller : Fragment() {

    private lateinit var addFoodAcc: CardView
    private lateinit var addMedicine: CardView
    private lateinit var addKennel: CardView
    private lateinit var addPetShop: CardView
    private lateinit var addTrainingCent: CardView
    private lateinit var addGroomingCent: CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_seller, container, false)

        addFoodAcc = view.findViewById(R.id.addFoodAndAcc_homeSeller)
        addMedicine = view.findViewById(R.id.addMedicine_homeSeller)
        addKennel = view.findViewById(R.id.addKennel_homeSeller)
        addPetShop = view.findViewById(R.id.addPetShop_homeSeller)
        addTrainingCent = view.findViewById(R.id.addTrainingCent_homeSeller)
        addGroomingCent = view.findViewById(R.id.addGroomingCent_homeSeller)

        addFoodAcc.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "FoodAcc")
            Navigation.findNavController(it).navigate(R.id.nav_addAccMed, bundle)
        }

        addMedicine.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "Medicine")
            Navigation.findNavController(it).navigate(R.id.nav_addAccMed, bundle)
        }

        addKennel.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "Kennel")
            Navigation.findNavController(it).navigate(R.id.nav_addOrg, bundle)
        }

        addPetShop.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "PetShop")
            Navigation.findNavController(it).navigate(R.id.nav_addOrg, bundle)
        }

        addTrainingCent.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "Training")
            Navigation.findNavController(it).navigate(R.id.nav_addOrg, bundle)
        }

        addGroomingCent.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "Grooming")
            Navigation.findNavController(it).navigate(R.id.nav_addOrg, bundle)
        }

        return  view
    }

}