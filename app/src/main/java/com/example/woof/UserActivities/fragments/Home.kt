package com.example.woof.UserActivities.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.woof.R
import com.example.woof.viewmodel.DBViewModel

class Home : Fragment() {

    private lateinit var feedBtn: CardView
    private lateinit var foodAccBtn: CardView
    private lateinit var trainGroomBtn: CardView
    private lateinit var petSopKennelBtn: CardView
    private lateinit var hospitalClinicBtn: CardView
    private lateinit var medicineBtn: CardView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        feedBtn = view.findViewById(R.id.feedBtn)
        foodAccBtn = view.findViewById(R.id.foodAccBtn)
        trainGroomBtn = view.findViewById(R.id.trainGroomBtn)
        petSopKennelBtn = view.findViewById(R.id.petShopKennelBtn)
        hospitalClinicBtn = view.findViewById(R.id.hospitalClinicBtn)
        medicineBtn = view.findViewById(R.id.medicinesBtn)

        val imageList = ArrayList<SlideModel>() // Create image list

// imageList.add(SlideModel("String Url" or R.drawable)
// imageList.add(SlideModel("String Url" or R.drawable, "title") You can add title

        imageList.add(SlideModel("https://bit.ly/2YoJ77H", "The animal population decreased by 58 percent in 42 years.", ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel("https://bit.ly/2BteuF2", "Elephants and tigers may become extinct.", ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel("https://bit.ly/3fLJf72", "And people do that.", ScaleTypes.CENTER_CROP))


        val imageSlider = view.findViewById<ImageSlider>(R.id.image_slider)
        imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)

        feedBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_feed)
        }

        foodAccBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_food_and_accessories)
        }

        trainGroomBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_training_and_grooming)
        }

        petSopKennelBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_pet_shop_and_kennel)
        }

        hospitalClinicBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_hospitals_and_clinics)
        }

        medicineBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_medicines)
        }

        return view
    }

}