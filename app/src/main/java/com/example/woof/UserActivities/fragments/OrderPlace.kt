package com.example.woof.UserActivities.fragments

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.woof.PushNotification
import com.example.woof.R
import com.example.woof.repo.Response
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseUser

class OrderPlace : Fragment() {

    private lateinit var productNameOrder: TextView
    private lateinit var productImageOrder: ImageView
    private lateinit var productPriceOrder: TextView
    private lateinit var productRatingTextOrder: TextView
    private lateinit var productRatingBarOrder: RatingBar
    private lateinit var placeOrderBtn: CardView
    private var appViewModel: AppViewModel? = null
    private var dbViewModel: DBViewModel? = null
    private lateinit var userName: String
    private lateinit var userPH: String
    private lateinit var userUID: String
    private lateinit var userType: String

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_place, container, false)

        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        appViewModel!!.userdata.observe(viewLifecycleOwner){
            getUserData(it!!)
        }

        val productName: String = requireArguments().getString("productName")!!
        val productImageUrl: String = requireArguments().getString("productImage")!!
        val productPrice: String = requireArguments().getString("productPrice")!!
        val productRating: String = requireArguments().getString("productRating")!!
        val sellerID: String = requireArguments().getString("sellerId")!!

        productNameOrder = view.findViewById(R.id.productName_order)
        productImageOrder = view.findViewById(R.id.productImage_order)
        productPriceOrder = view.findViewById(R.id.productPrice_order)
        productRatingBarOrder = view.findViewById(R.id.ratingBar_order)
        productRatingTextOrder = view.findViewById(R.id.ratingText_order)
        placeOrderBtn = view.findViewById(R.id.placeOrderBtn_order)

        productNameOrder.text = productName
        productPriceOrder.text = "₹$productPrice INR"
        productRatingTextOrder.text = productRating
        productRatingBarOrder.rating = productRating.toFloat()
        Glide.with(view.context).load(productImageUrl).into(productImageOrder)

        placeOrderBtn.setOnClickListener {
            showDialog(Integer.parseInt(productPrice), productName, productImageUrl, sellerID)
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
        val updatedProductPrice: TextView = dialog.findViewById(R.id.updatedProductPrice_orderDialog)
        val totalPrice: TextView = dialog.findViewById(R.id.totalPrice_orderDialog)
        val orderPlace: CardView = dialog.findViewById(R.id.placeFinalOrderBtn_orderDialog)
        var count = 1
        var finalAmount = 0

        var temp1: Int = price * count
        var temp2 = temp1 + 30
        finalAmount = temp2

        updatedProductPrice.text = "Product Price :  ₹$temp1"
        totalPrice.text = "Total amount you have to pay : ₹$temp2 INR"

        plusBtn.setOnClickListener {
            if(count < 12) {
                count++
                quantity.text = "$count"
                temp1 = price * count
                temp2 = temp1 + 30
                finalAmount = temp2
                updatedProductPrice.text = "Product Price :  ₹$temp1"
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
                updatedProductPrice.text = "Product Price :  ₹$temp1"
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