package com.example.woof.UserActivities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.woof.R
import com.example.woof.UserActivities.adapters.BookingAdapter
import com.example.woof.UserActivities.items.BookingItems
import com.example.woof.repo.Response
import com.example.woof.viewmodel.DBViewModel
import com.google.firebase.firestore.DocumentSnapshot

class Bookings : Fragment() {

    private lateinit var bookingAdapter: BookingAdapter
    private var bookingItemsArray = arrayListOf<BookingItems>()
    private var dbViewModel: DBViewModel? = null
    private lateinit var progressbarBookings: LottieAnimationView
    private lateinit var bookingRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         val view = inflater.inflate(R.layout.fragment_bookings, container, false)

        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        val swipeRefreshLayout: SwipeRefreshLayout =
            view.findViewById(R.id.swipeRefreshLayout_bookings)

        bookingRecyclerView = view.findViewById(R.id.recyclerView_bookings)
        progressbarBookings = view.findViewById(R.id.progressbar_bookings)
        progressbarBookings.visibility = View.VISIBLE
        bookingRecyclerView.visibility = View.GONE

        bookingAdapter = BookingAdapter(bookingItemsArray)
        bookingRecyclerView.layoutManager = GridLayoutManager(view.context, 2)
        bookingRecyclerView.setHasFixedSize(true)
        bookingRecyclerView.setItemViewCacheSize(20)
        bookingRecyclerView.adapter = bookingAdapter


        swipeRefreshLayout.setOnRefreshListener {
            progressbarBookings.visibility = View.VISIBLE
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
                    progressbarBookings.visibility = View.GONE
                }
            }
        }

         return view
    }

    private fun fetchBookings(list: MutableList<DocumentSnapshot>) {
        bookingItemsArray = arrayListOf()
        for (i in list) {
            val bookingItem = BookingItems(
                i.getString("Owner Name"),
                i.getString("Owner Image Url"),
                i.getString("Pet Species"),
                i.getString("Issue"),
                i.getString("Date"),
                i.getString("Timing"),
                i.getString("Status")
            )
            bookingItemsArray.add(bookingItem)
        }
        bookingAdapter.updateBooking(bookingItemsArray)
        progressbarBookings.visibility = View.GONE
        bookingRecyclerView.visibility = View.VISIBLE
    }

}