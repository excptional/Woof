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
import com.example.woof.viewmodel.DBViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignUpNormalUser : Fragment(), AdapterView.OnItemSelectedListener {

    private var species = arrayListOf(
        "Select pet species",
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

    private var dogBreed = arrayListOf(
        "Select breed",
        "Labrador Retriever",
        "German Shepherd",
        "Golden Retriever",
        "Bulldog",
        "French Bulldog",
        "Beagle",
        "Poodle",
        "Rottweiler",
        "Husky",
        "Doberman Pinscher",
        "German Shorthaired Pointer",
        "Yorkshire Terrier",
        "Dachshund",
        "Pembroke Welsh Corgi",
        "Australian Shepherd",
        "Boxer",
        "Cavalier King Charles Spaniel",
        "Great Dane",
        "Others"
    )

    private var catBreed = arrayListOf(
        "Select breed",
        "Gray Tabby",
        "Scottish Fold",
        "Persian",
        "Japanese Bobtail",
        "Norwegian Forest Cat",
        "Siamese",
        "British Shorthair",
        "Calico",
        "Snowshoe",
        "Polydactyl",
        "Others"
    )

    private var birdBreed = arrayListOf(
        "Select breed",
        "Budgerigar",
        "Cockatiel",
        "Cockatoo",
        "Hyacinth Macaw",
        "Dove",
        "Parrotlet",
        "Green-Cheeked Conure",
        "Hahn’s Macaw",
        "Others"
    )

    private var fishBreed = arrayListOf(
        "Select breed",
        "Guppie",
        "Platy",
        "Mollie",
        "Anablep",
        "Zebra Danio",
        "Kribensis Cichlid",
        "Firemouth Cichlid",
        "Rosy Red Minnow",
        "Rosy Barb",
        "Others"
    )
    private var defaultList = arrayListOf(
        "Select Breed",
        "Others"
    )

    private lateinit var signUpPasswordUser: TextInputEditText
    private lateinit var signUpPasswordLayoutUser: TextInputLayout
    private lateinit var signUpCPasswordUser: TextInputEditText
    private lateinit var signUpCPasswordLayoutUser: TextInputLayout
    private lateinit var signUpPetNameUser: TextInputEditText
    private lateinit var prevBtnUser: CardView
    private lateinit var signUpBtnUser: CardView
    private lateinit var signUpProgressbarUser: LottieAnimationView
    private lateinit var signUpWhiteLayoutUser: LinearLayout
    private lateinit var speciesSpinner: Spinner
    private lateinit var breedSpinner: Spinner
    private lateinit var breedSpinnerLayoutUser: LinearLayout
    private lateinit var speciesNameLayoutUser: LinearLayout
    private lateinit var speciesNameUser: TextInputEditText
    private var appViewModel: AppViewModel? = null
    private var selectedSpecies: String? = null
    private var selectedBreed: String? = null


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up_normal_user, container, false)

        val name = requireArguments().getString("name")
        val number = requireArguments().getString("number")
        val email = requireArguments().getString("email")

        signUpPasswordUser = view.findViewById(R.id.signUpPassword_user)
        signUpPasswordLayoutUser = view.findViewById(R.id.signUpPasswordLayout_user)
        signUpCPasswordUser = view.findViewById(R.id.signUpCPassword_user)
        signUpCPasswordLayoutUser = view.findViewById(R.id.signUpCPasswordLayout_user)
        signUpPetNameUser = view.findViewById(R.id.signUpPetName_user)
        prevBtnUser = view.findViewById(R.id.prevBtn_user)
        signUpBtnUser = view.findViewById(R.id.signUpBtn_user)
        signUpProgressbarUser = view.findViewById(R.id.signUpProgressbar_user)
        signUpWhiteLayoutUser = view.findViewById(R.id.signUpWhiteLayout_user)
        breedSpinnerLayoutUser = view.findViewById(R.id.breedSpinnerLayout_user)
        speciesSpinner = view.findViewById(R.id.speciesSpinner_user)
        breedSpinner = view.findViewById(R.id.breedSpinner_user)
        speciesNameUser = view.findViewById(R.id.signUpSpeciesName_user)
        speciesNameLayoutUser = view.findViewById(R.id.signUpSpeciesNameLayout_user)

        appViewModel = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        val speciesAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            species
        )
        speciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(speciesSpinner)
        {
            adapter = speciesAdapter
            setSelection(0, true)
            onItemSelectedListener = this@SignUpNormalUser
            gravity = Gravity.CENTER
            setPopupBackgroundResource(R.color.cream)
        }

        signUpBtnUser.setOnClickListener {
            signUp(name!!, number!!, email!!, selectedSpecies, selectedBreed)
        }

        prevBtnUser.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.authFrameLayout, SignUp()).commit()
        }

        return view
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent!!.id == R.id.speciesSpinner_user) {
            selectedBreed = null
            if (position == 0) {
                breedSpinnerLayoutUser.visibility = View.GONE
                speciesNameLayoutUser.visibility = View.GONE
                selectedSpecies = null
            } else if (species[position] == "Others") {
                speciesNameLayoutUser.visibility = View.VISIBLE
                breedSpinnerLayoutUser.visibility = View.GONE
                selectedSpecies = species[position]
            } else {
                breedSpinnerLayoutUser.visibility = View.GONE
                val breedAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    whichSpecies(species[position])
                )

                breedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                with(breedSpinner)
                {
                    adapter = breedAdapter
                    setSelection(0, true)
                    onItemSelectedListener = this@SignUpNormalUser
                    gravity = Gravity.CENTER
                    setPopupBackgroundResource(R.color.cream)
                }
                selectedSpecies = species[position]
                breedSpinnerLayoutUser.visibility = View.VISIBLE
                speciesNameLayoutUser.visibility = View.GONE
            }
        } else {
            if (position != 0) {
                selectedBreed = whichSpecies(selectedSpecies!!)[position]
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        breedSpinnerLayoutUser.visibility = View.GONE
        speciesNameLayoutUser.visibility = View.GONE
        Toast.makeText(
            requireContext(),
            "Nothing selected...select to continue",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun whichSpecies(str: String): ArrayList<String> {
        val temp = when (str) {
            "Dog" -> dogBreed
            "Cat" -> catBreed
            "Bird" -> birdBreed
            "Fish" -> fishBreed
            else -> defaultList
        }
        return temp
    }

    private fun signUp(
        name: String,
        number: String,
        email: String,
        species: String?,
        breed: String?
    ) {
        signUpProgressbarUser.visibility = View.VISIBLE
        signUpWhiteLayoutUser.visibility = View.VISIBLE
        val password = signUpPasswordUser.text.toString()
        val cPassword = signUpCPasswordUser.text.toString()
        val petName = signUpPetNameUser.text.toString()
        var allRight = true
        signUpPasswordLayoutUser.isPasswordVisibilityToggleEnabled = true
        signUpCPasswordLayoutUser.isPasswordVisibilityToggleEnabled = true

        if (selectedSpecies == "Others") {
            selectedSpecies = speciesNameUser.text.toString()
        }
        if (selectedBreed == "Others") {
            selectedBreed = "NA"
        }

        if (selectedSpecies.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                "Select your pet's species",
                Toast.LENGTH_SHORT
            ).show()
            allRight = false
        }


        if (selectedBreed.isNullOrEmpty() && breedSpinnerLayoutUser.visibility == View.VISIBLE) {
            Toast.makeText(
                requireContext(),
                "Select your pet's breed",
                Toast.LENGTH_SHORT
            ).show()
            allRight = false
        }

        if (password.isEmpty()) {
            signUpPasswordLayoutUser.isPasswordVisibilityToggleEnabled = false
            signUpPasswordUser.error = "Enter your password"
            allRight = false
        }
        if (password.length < 6) {
            signUpPasswordLayoutUser.isPasswordVisibilityToggleEnabled = false
            signUpPasswordUser.error = "Enter password more than 6 characters"
            allRight = false
        }
        if (password != cPassword) {
            signUpCPasswordLayoutUser.isPasswordVisibilityToggleEnabled = false
            signUpCPasswordUser.error = "Password not matched"
            allRight = false
        }
        if (petName.isEmpty()) {
            signUpPetNameUser.error = "Enter your trade license no."
            allRight = false
        }

        if (!allRight) {
            signUpProgressbarUser.visibility = View.GONE
            signUpWhiteLayoutUser.visibility = View.GONE
            if (selectedSpecies.isNullOrEmpty() || selectedBreed.isNullOrEmpty()) {
                Toast.makeText(
                    requireActivity().applicationContext,
                    "Fill up your details properly",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            appViewModel!!.normalUserRegister(
                name,
                petName,
                number,
                email,
                password,
                species,
                breed
            )
            appViewModel!!.response.observe(viewLifecycleOwner) {
                when (it) {
                    is Response.Success -> {
                        signUpProgressbarUser.visibility = View.GONE
                        signUpWhiteLayoutUser.visibility = View.GONE
                        requireActivity().startActivity(Intent(activity, SplashScreen::class.java))
                    }
                    is Response.Failure -> {
                        Toast.makeText(
                            requireActivity().applicationContext,
                            it.errorMassage,
                            Toast.LENGTH_SHORT
                        ).show()
                        signUpProgressbarUser.visibility = View.GONE
                        signUpWhiteLayoutUser.visibility = View.GONE
                    }
                    else -> {}
                }
            }
        }
    }

}