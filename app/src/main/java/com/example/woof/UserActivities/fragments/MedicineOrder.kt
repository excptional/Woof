package com.example.woof.UserActivities.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.woof.R
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseUser

class MedicineOrder : Fragment() {

    private lateinit var medicineNameOrder: TextView
    private lateinit var medicineImageOrder: ImageView
    private lateinit var medicinePriceOrder: TextView
    private lateinit var medicineDescriptionOrder: TextView
    private lateinit var medicineRatingTextOrder: TextView
    private lateinit var medicineRatingBarOrder: RatingBar
    private lateinit var placeOrderBtn: CardView
    private var appViewModel: AppViewModel? = null
    private var dbViewModel: DBViewModel? = null
    private lateinit var userName: String
    private lateinit var userPH: String
    private lateinit var userUID: String
    private lateinit var userType: String

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_medicine_order, container, false)

        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        appViewModel!!.userdata.observe(viewLifecycleOwner){
            getUserData(it!!)
        }

        val medicineName: String = requireArguments().getString("name")!!
        val medicineImageUrl: String = requireArguments().getString("image")!!
        val medicineDescription: String = requireArguments().getString("description")!!
        val medicinePrice: String = requireArguments().getString("price")!!
        val medicineRating: String = requireArguments().getString("rating")!!
        val sellerID: String = requireArguments().getString("sellerId")!!

        medicineNameOrder = view.findViewById(R.id.medicineName_order)
        medicineImageOrder = view.findViewById(R.id.medicineImage_order)
        medicinePriceOrder = view.findViewById(R.id.medicinePrice_order)
        medicineRatingBarOrder = view.findViewById(R.id.ratingBar_order)
        medicineRatingTextOrder = view.findViewById(R.id.ratingText_order)
        medicineDescriptionOrder = view.findViewById(R.id.description_order)
        placeOrderBtn = view.findViewById(R.id.placeOrderBtn_order)

        medicineNameOrder.text = medicineName
        medicineDescriptionOrder.text = medicineDescription
        medicinePriceOrder.text = "₹$medicinePrice INR"
        medicineRatingTextOrder.text = medicineRating
        medicineRatingBarOrder.rating = medicineRating.toFloat()
        Glide.with(view.context).load(medicineImageUrl).into(medicineImageOrder)

        placeOrderBtn.setOnClickListener {
            showDialog(Integer.parseInt(medicinePrice), medicineName, medicineImageUrl, sellerID)
        }
        
        return view
    }

    private fun getUserData(user: FirebaseUser) {
        dbViewModel!!.getProfileData(user)
        dbViewModel!!.profileData.observe(viewLifecycleOwner) { dataList ->
            userType = dataList[0]!!
            userUID = user.uid
            userName = dataList[1]!!
            userPH = dataList[3]!!
        }
    }

    @SuppressLint("SetTextI18n")
    fun showDialog(price: Int, name: String, imageUrl: String, id: String) {
        val dialog = Dialog(requireContext())
        dialog.setCanceledOnTouchOutside(false)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.place_order_dialog)

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val quantity: TextView = dialog.findViewById(R.id.quantity_orderDialog)
        val plusBtn: ImageButton = dialog.findViewById(R.id.plus_orderDialog)
        val minusBtn: ImageButton = dialog.findViewById(R.id.minus_orderDialog)
        val address: TextInputEditText = dialog.findViewById(R.id.address_orderDialog)
        val updatedPrice: TextView = dialog.findViewById(R.id.updatedProductPrice_orderDialog)
        val totalPrice: TextView = dialog.findViewById(R.id.totalPrice_orderDialog)
        val orderPlace: CardView = dialog.findViewById(R.id.placeFinalOrderBtn_orderDialog)
        var count = 1
        var finalAmount = 0

        var temp1: Int = price * count
        var temp2 = temp1 + 30
        finalAmount = temp2

        updatedPrice.text = " Price :  ₹$temp1"
        totalPrice.text = "Total amount you have to pay : ₹$temp2 INR"

        plusBtn.setOnClickListener {
            if(count < 12) {
                count++
                quantity.text = "$count"
                temp1 = price * count
                temp2 = temp1 + 30
                finalAmount = temp2
                updatedPrice.text = " Price :  ₹$temp1"
                totalPrice.text = "Total amount you have to pay : ₹$temp2 INR"
            }else{
                Toast.makeText(requireContext(), "You can't order more than 12 items", Toast.LENGTH_SHORT).show()
            }
        }

        minusBtn.setOnClickListener {
            if(count > 1) {
                count--
                quantity.text = "$count"
                temp1 = price * count
                temp2 = temp1 + 30
                finalAmount = temp2
                updatedPrice.text = " Price :  ₹$temp1"
                totalPrice.text = "Total amount you have to pay : ₹$temp2 INR"
            }else{
                Toast.makeText(requireContext(), "Already minimum no. of item selected", Toast.LENGTH_SHORT).show()
            }
        }

        orderPlace.setOnClickListener {
            if(address.text.isNullOrEmpty()){
                address.error = "Enter your address before place order"
                Toast.makeText(requireContext(), "Enter your address before place order", Toast.LENGTH_SHORT).show()
            }else{
                dbViewModel!!.addOrder(id, userName, userPH, address.text.toString(),userUID, name, imageUrl, finalAmount.toString(), count)
                dialog.hide()
                Toast.makeText(requireContext(), "Order successfully placed", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

}