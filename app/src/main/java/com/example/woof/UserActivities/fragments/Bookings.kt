package com.example.woof.UserActivities.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.woof.R
import com.example.woof.UserActivities.adapters.BookingAdapter
import com.example.woof.UserActivities.items.BookingItems
import com.example.woof.repo.Response
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.DocumentSnapshot

class Bookings : Fragment() {

    private lateinit var bookingAdapter: BookingAdapter
    private var bookingItemsArray = arrayListOf<BookingItems>()
    private var appViewModel: AppViewModel? = null
    private var dbViewModel: DBViewModel? = null
    private lateinit var shimmerContainerBookings: ShimmerFrameLayout
    private lateinit var bookingRecyclerView: RecyclerView
    private lateinit var noAppointmentBookings: TextView
    private lateinit var title: TextView
    private lateinit var doctorUID: String

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         val view = inflater.inflate(R.layout.fragment_bookings, container, false)

        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]

        appViewModel!!.userdata.observe(viewLifecycleOwner) { user ->
            if (user != null)
                doctorUID = user.uid
        }

        val swipeRefreshLayout: SwipeRefreshLayout =
            view.findViewById(R.id.swipeRefreshLayout_bookings)

        bookingRecyclerView = view.findViewById(R.id.recyclerView_bookings)
        noAppointmentBookings = view.findViewById(R.id.noAppointment_bookings)
        title = view.findViewById(R.id.title)

        shimmerContainerBookings = view.findViewById(R.id.shimmer_view_bookings)
        shimmerContainerBookings.startShimmer()
        shimmerContainerBookings.visibility = View.VISIBLE
        bookingRecyclerView.visibility = View.GONE

        bookingAdapter = BookingAdapter(bookingItemsArray, this)
        bookingRecyclerView.layoutManager = LinearLayoutManager(view.context)
        bookingRecyclerView.setHasFixedSize(true)
        bookingRecyclerView.setItemViewCacheSize(20)
        bookingRecyclerView.adapter = bookingAdapter

        swipeRefreshLayout.setOnRefreshListener {
            shimmerContainerBookings.startShimmer()
            shimmerContainerBookings.visibility = View.VISIBLE
            bookingRecyclerView.visibility = View.GONE
            dbViewModel!!.getBookingRequest()
            dbViewModel!!.bookingsData.observe(viewLifecycleOwner) {
                fetchBookings(it)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        dbViewModel!!.getBookingRequest()

        dbViewModel!!.dbLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    dbViewModel!!.bookingsData.observe(viewLifecycleOwner) {
                        fetchBookings(it)
                    }
                }
                is Response.Failure -> {
                    Toast.makeText(requireActivity(), it.errorMassage, Toast.LENGTH_SHORT).show()
                    bookingRecyclerView.visibility = View.VISIBLE
                    shimmerContainerBookings.clearAnimation()
                    shimmerContainerBookings.visibility = View.GONE
                    bookingRecyclerView.visibility = View.VISIBLE
                }
            }
        }

         return view
    }

    private fun fetchBookings(list: MutableList<DocumentSnapshot>) {
        bookingItemsArray = arrayListOf()
        for (i in list) {
            if(i.getString("Status") != "Rejected" && i.getString("Doctor UID") == doctorUID) {
                val bookingItem = BookingItems(
                    i.getString("User Name"),
                    i.getString("User Image Url"),
                    i.getString("Species"),
                    i.getString("Issue"),
                    i.getString("Date"),
                    i.getString("Timings"),
                    i.getString("Status"),
                    i.getString("ID")
                )
                bookingItemsArray.add(bookingItem)
            }
        }
        if(bookingItemsArray.isEmpty()) {
            noAppointmentBookings.visibility = View.VISIBLE
            title.visibility = View.GONE
        }else{
            noAppointmentBookings.visibility = View.GONE
            title.visibility = View.VISIBLE
        }
        bookingAdapter.updateBooking(bookingItemsArray)
        bookingRecyclerView.visibility = View.VISIBLE
        shimmerContainerBookings.clearAnimation()
        shimmerContainerBookings.visibility = View.GONE
        bookingRecyclerView.visibility = View.VISIBLE
    }

}