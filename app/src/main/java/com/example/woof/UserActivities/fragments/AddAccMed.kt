package com.example.woof.UserActivities.fragments

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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.example.woof.R
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseUser
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class AddAccMed : Fragment() {

    private lateinit var productNameEditText: TextInputEditText
    private lateinit var productDescriptionEditText: TextInputEditText
    private lateinit var productPriceEditText: TextInputEditText
    private lateinit var addProductImage: LinearLayout
    private lateinit var productImage: ImageView
    private lateinit var afterLayout: LinearLayout
    private lateinit var beforeLayout: LinearLayout
    private lateinit var postProduct: CardView
    private lateinit var whiteLayout: LinearLayout
    private lateinit var progressbar: LottieAnimationView
    private var appViewModel: AppViewModel? = null
    private var dbViewModel: DBViewModel? = null
    private lateinit var myUser: FirebaseUser
    private lateinit var productImageUri: Uri

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(9, 12)
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
        val view = inflater.inflate(R.layout.fragment_add_acc_med, container, false)

        productNameEditText = view.findViewById(R.id.productName_addAcc)
        productDescriptionEditText = view.findViewById(R.id.productDescription_addAcc)
        productPriceEditText = view.findViewById(R.id.productPrice_addAcc)
        addProductImage = view.findViewById(R.id.addProductImage_addAcc)
        postProduct = view.findViewById(R.id.postProduct_addAcc)
        afterLayout = view.findViewById(R.id.afterLayout_addAcc)
        beforeLayout = view.findViewById(R.id.beforeLayout_addAcc)
        productImage = view.findViewById(R.id.productImage_addAcc)
        whiteLayout = view.findViewById(R.id.whiteLayout_addAcc)
        progressbar = view.findViewById(R.id.progressbar_addAcc)
        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        appViewModel!!.userdata.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                myUser = user
            }
        }

        val purpose = requireArguments().getString("type").toString()

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
            if (uri != null) {
                productImageUri = uri
                productImage.setImageURI(uri)
                if(beforeLayout.isVisible) {
                    beforeLayout.visibility = View.GONE
                    afterLayout.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(requireContext(), "No image is selected", Toast.LENGTH_SHORT).show()
            }
        }

        addProductImage.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }

        postProduct.setOnClickListener {
            postProduct(purpose, view)
        }

        return view
    }

    private fun postProduct(type: String, view: View) {
        val productName = productNameEditText.text.toString()
        val productDescription = productDescriptionEditText.text.toString()
        val productPrice = productPriceEditText.text.toString()
        var allRight = true

        if (productName.isEmpty()) {
            productNameEditText.error = "Enter product name"
            allRight = false
        }
        if (productPrice.isEmpty()) {
            productPriceEditText.error = "Enter product price"
            allRight = false
        }
        if(productDescription.isEmpty() && type == "Medicine"){
            productDescriptionEditText.error = "Enter description here"
            allRight = false
        }
        if(beforeLayout.isVisible){
            Toast.makeText(
                requireActivity().applicationContext,
                "Add your product image",
                Toast.LENGTH_SHORT
            ).show()
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
            if(type == "FoodAcc") {
                dbViewModel!!.addAccessories(myUser.uid, productName, productImageUri, productPrice)
            }else{
                dbViewModel!!.addMedicines(myUser.uid, productName, productImageUri, productPrice, productDescription)
            }
            Toast.makeText(
                requireActivity().applicationContext,
                "Your product publish successfully",
                Toast.LENGTH_LONG
            ).show()
            requireFragmentManager().popBackStack()
            Navigation.findNavController(view).navigate(R.id.nav_home_seller)
        }

    }

}