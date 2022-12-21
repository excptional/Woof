package com.example.woof.UserActivities.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.woof.R
import com.example.woof.UserActivities.adapters.MyAppointmentAdapter
import com.example.woof.UserActivities.adapters.MyOrdersAdapter
import com.example.woof.UserActivities.items.MyAppointmentsItems
import com.example.woof.UserActivities.items.MyOrdersItems
import com.example.woof.repo.Response
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot

class HomeDoctor : Fragment() {
    private lateinit var feedBtn: CardView
    private lateinit var foodAccBtn: CardView
    private lateinit var trainGroomBtn: CardView
    private lateinit var petSopKennelBtn: CardView
    private lateinit var medicineBtn: CardView
    private lateinit var appointmentsBtn: CardView
    private var appViewModel: AppViewModel? = null
    private var dbViewModel: DBViewModel? = null
    private lateinit var myOrdersRecyclerView: RecyclerView
    private lateinit var myOrdersAdapter: MyOrdersAdapter
    private var mOItemsArray = arrayListOf<MyOrdersItems>()
    private lateinit var contentOrderLayoutHome: LinearLayout
    private lateinit var noOrdersHome: TextView
    private lateinit var myOrdersLayoutHome: LinearLayout
    private lateinit var myUser: FirebaseUser
    private lateinit var arrowOrderHome: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_doctor, container, false)

        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        appViewModel!!.userdata.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                myUser = user
            }
        }

        myOrdersRecyclerView = view.findViewById(R.id.myOrderRecyclerView_homeDoctor)
        myOrdersLayoutHome = view.findViewById(R.id.myOrdersLayout_homeDoctor)
        noOrdersHome = view.findViewById(R.id.noOrders_homeDoctor)
        contentOrderLayoutHome = view.findViewById(R.id.contentOrderLayout_homeDoctor)
        arrowOrderHome = view.findViewById(R.id.arrow_order_homeDoctor)

        myOrdersAdapter = MyOrdersAdapter(mOItemsArray)
        myOrdersRecyclerView.layoutManager = LinearLayoutManager(view.context)
        myOrdersRecyclerView.setHasFixedSize(true)
        myOrdersRecyclerView.setItemViewCacheSize(20)
        myOrdersRecyclerView.adapter = myOrdersAdapter

        dbViewModel!!.getBookingRequest()
        dbViewModel!!.fetchOrders()

        feedBtn = view.findViewById(R.id.feedBtn_homeDoctor)
        foodAccBtn = view.findViewById(R.id.foodAccBtn_homeDoctor)
        trainGroomBtn = view.findViewById(R.id.trainGroomBtn_homeDoctor)
        petSopKennelBtn = view.findViewById(R.id.petShopKennelBtn_homeDoctor)
        medicineBtn = view.findViewById(R.id.medicineBtn_homeDoctor)
        appointmentsBtn = view.findViewById(R.id.appointmentsBtn_homeDoctor)

        val imageList = ArrayList<SlideModel>() // Create image list

// imageList.add(SlideModel("String Url" or R.drawable)
// imageList.add(SlideModel("String Url" or R.drawable, "title") You can add title

        imageList.add(
            SlideModel(
                "https://bit.ly/2YoJ77H",
                "The animal population decreased by 58 percent in 42 years.",
                ScaleTypes.CENTER_CROP
            )
        )
        imageList.add(
            SlideModel(
                "https://bit.ly/2BteuF2",
                "Elephants and tigers may become extinct.",
                ScaleTypes.CENTER_CROP
            )
        )
        imageList.add(
            SlideModel(
                "https://bit.ly/3fLJf72",
                "And people do that.",
                ScaleTypes.CENTER_CROP
            )
        )

        val imageSlider = view.findViewById<ImageSlider>(R.id.image_slider_homeDoctor)
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

        medicineBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_medicines)
        }

        appointmentsBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_bookings)
        }

        dbViewModel!!.dbLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    dbViewModel!!.orderData.observe(viewLifecycleOwner) {
                        fetchOrders(it)
                    }
                }
                is Response.Failure -> {
                    Toast.makeText(requireContext(), it.errorMassage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        myOrdersLayoutHome.setOnClickListener {
            if (!contentOrderLayoutHome.isVisible) {
                arrowOrderHome.setImageResource(R.drawable.up_icon)
                contentOrderLayoutHome.visibility = View.VISIBLE

            } else {
                arrowOrderHome.setImageResource(R.drawable.down_icon)
                contentOrderLayoutHome.visibility = View.GONE
            }
        }
        return view
    }

    private fun fetchOrders(list: MutableList<DocumentSnapshot>) {
        mOItemsArray = arrayListOf()
        for (i in list) {
            if (i.getString("User UID") == myUser.uid) {
                val order = MyOrdersItems(
                    i.getString("Product Name"),
                    i.getString("Product Image Url"),
                    i.getString("Payable Amount"),
                    i.getString("Delivery Date"),
                    "Pending",
                    i.getLong("Quantity").toString()
                )
                mOItemsArray.add(order)
            }
        }
        if (mOItemsArray.isEmpty()) {
            noOrdersHome.visibility = View.VISIBLE
        } else {
            noOrdersHome.visibility = View.GONE
        }
        myOrdersAdapter.updateMyOrders(mOItemsArray)
    }

}