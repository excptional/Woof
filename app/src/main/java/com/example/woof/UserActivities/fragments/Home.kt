package com.example.woof.UserActivities.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
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


class Home : Fragment() {
    private lateinit var feedBtn: CardView
    private lateinit var foodAccBtn: CardView
    private lateinit var trainGroomBtn: CardView
    private lateinit var petSopKennelBtn: CardView
    private lateinit var hospitalClinicBtn: CardView
    private lateinit var medicineBtn: CardView
    private var appViewModel: AppViewModel? = null
    private var dbViewModel: DBViewModel? = null
    private lateinit var myAppointmentRecyclerView: RecyclerView
    private lateinit var myAppointmentAdapter: MyAppointmentAdapter
    private var mAItemsArray = arrayListOf<MyAppointmentsItems>()
    private lateinit var myOrdersRecyclerView: RecyclerView
    private lateinit var myOrdersAdapter: MyOrdersAdapter
    private var mOItemsArray = arrayListOf<MyOrdersItems>()
    private lateinit var contentOrderLayoutHome: LinearLayout
    private lateinit var contentAppointmentLayoutHome: LinearLayout
    private lateinit var noAppointmentsHome: TextView
    private lateinit var noOrdersHome: TextView
    private lateinit var myAppointmentLayoutHome: LinearLayout
    private lateinit var myOrdersLayoutHome: LinearLayout
    private lateinit var myUser: FirebaseUser
    private lateinit var arrowAppointmentHome: ImageView
    private lateinit var arrowOrderHome: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        appViewModel!!.userdata.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                myUser = user
            }
        }

        myOrdersRecyclerView = view.findViewById(R.id.myOrderRecyclerView)
        myAppointmentRecyclerView = view.findViewById(R.id.myAppointmentRecyclerView)
        myAppointmentLayoutHome = view.findViewById(R.id.myAppointmentLayout_home)
        myOrdersLayoutHome = view.findViewById(R.id.myOrdersLayout_home)
        noAppointmentsHome = view.findViewById(R.id.noAppointments_home)
        noOrdersHome = view.findViewById(R.id.noOrders_home)
        contentAppointmentLayoutHome = view.findViewById(R.id.contentAppointmentLayout_home)
        contentOrderLayoutHome = view.findViewById(R.id.contentOrderLayout_home)
        arrowAppointmentHome = view.findViewById(R.id.arrow_appointment_home)
        arrowOrderHome = view.findViewById(R.id.arrow_order_home)

        myAppointmentAdapter = MyAppointmentAdapter(mAItemsArray)
        myAppointmentRecyclerView.layoutManager = LinearLayoutManager(view.context)
        myAppointmentRecyclerView.setHasFixedSize(true)
        myAppointmentRecyclerView.setItemViewCacheSize(20)
        myAppointmentRecyclerView.adapter = myAppointmentAdapter

        myOrdersAdapter = MyOrdersAdapter(mOItemsArray)
        myOrdersRecyclerView.layoutManager = LinearLayoutManager(view.context)
        myOrdersRecyclerView.setHasFixedSize(true)
        myOrdersRecyclerView.setItemViewCacheSize(20)
        myOrdersRecyclerView.adapter = myOrdersAdapter

        dbViewModel!!.getBookingRequest()
        dbViewModel!!.fetchOrders()

        feedBtn = view.findViewById(R.id.feedBtn)
        foodAccBtn = view.findViewById(R.id.foodAccBtn)
        trainGroomBtn = view.findViewById(R.id.trainGroomBtn)
        petSopKennelBtn = view.findViewById(R.id.petShopKennelBtn)
        hospitalClinicBtn = view.findViewById(R.id.hospitalClinicBtn)
        medicineBtn = view.findViewById(R.id.medicinesBtn)

        val imageList = ArrayList<SlideModel>() // Create image list

// imageList.add(SlideModel("String Url" or R.drawable)
// imageList.add(SlideModel("String Url" or R.drawable, "title") You can add title

        imageList.add(
            SlideModel(
                "https://firebasestorage.googleapis.com/v0/b/woof-uit.appspot.com/o/Sliders%2F1.jpg?alt=media&token=4de88a3d-fd40-43b8-9efa-67a290b2f27f",
                ScaleTypes.FIT
            )
        )
        imageList.add(
            SlideModel(
                "https://firebasestorage.googleapis.com/v0/b/woof-uit.appspot.com/o/Sliders%2F2.jpg?alt=media&token=352b62cb-de51-4a59-91a6-f46b9e6db8ed",
                ScaleTypes.FIT
            )
        )

        val imageSlider = view.findViewById<ImageSlider>(R.id.image_slider)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)

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

        dbViewModel!!.dbLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    dbViewModel!!.bookingsData.observe(viewLifecycleOwner) {
                        fetchAppointments(it)
                    }
                    dbViewModel!!.orderData.observe(viewLifecycleOwner) {
                        fetchOrders(it)
                    }
                }
                is Response.Failure -> {
                    Toast.makeText(requireContext(), it.errorMassage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        myAppointmentLayoutHome.setOnClickListener {
            if (!contentAppointmentLayoutHome.isVisible) {
                arrowAppointmentHome.setImageResource(R.drawable.up_icon)
                contentAppointmentLayoutHome.visibility = View.VISIBLE

            } else {
                arrowAppointmentHome.setImageResource(R.drawable.down_icon)
                contentAppointmentLayoutHome.visibility = View.GONE
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

   private fun fetchAppointments(list: MutableList<DocumentSnapshot>) {
        mAItemsArray = arrayListOf()
        for (i in list) {
            if (i.getString("User Uid") == myUser.uid) {
                val appointment = MyAppointmentsItems(
                    i.getString("Doctor Name"),
                    i.getString("Doctor Image Url"),
                    i.getString("Doctor Speciality"),
                    i.getString("Date"),
                    i.getString("Timings"),
                    i.getString("Status")
                )
                mAItemsArray.add(appointment)
            }
        }
        if (mAItemsArray.isEmpty()) {
            noAppointmentsHome.visibility = View.VISIBLE
        } else {
            noAppointmentsHome.visibility = View.GONE
        }
        myAppointmentAdapter.updateMyAppointment(mAItemsArray)
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