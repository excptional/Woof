package com.example.woof.UserActivities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.woof.R
import com.example.woof.UserActivities.adapters.BookingAdapter
import com.example.woof.UserActivities.adapters.OrdersAdapter
import com.example.woof.UserActivities.items.BookingItems
import com.example.woof.UserActivities.items.MyOrdersItems
import com.example.woof.UserActivities.items.OrderItems
import com.example.woof.repo.Response
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot

class Orders : Fragment() {

    private lateinit var ordersAdapter: OrdersAdapter
    private var orderItemsArray = arrayListOf<OrderItems>()
    private var appViewModel: AppViewModel? = null
    private var dbViewModel: DBViewModel? = null
    private lateinit var shimmerContainerOrder: ShimmerFrameLayout
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var noOrderText: TextView
    private lateinit var sellerUID: String
    private lateinit var title: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_orders, container, false)

        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]

        appViewModel!!.userdata.observe(viewLifecycleOwner) { user ->
            if (user != null)
                sellerUID = user.uid
        }

        val swipeRefreshLayout: SwipeRefreshLayout =
            view.findViewById(R.id.swipeRefreshLayout_order)

        orderRecyclerView = view.findViewById(R.id.recyclerView_order)
        noOrderText = view.findViewById(R.id.noOrders_order)
        title = view.findViewById(R.id.title_order)

        shimmerContainerOrder = view.findViewById(R.id.shimmer_view_order)
        shimmerContainerOrder.startShimmer()
        shimmerContainerOrder.visibility = View.VISIBLE
        orderRecyclerView.visibility = View.GONE

        ordersAdapter = OrdersAdapter(orderItemsArray)
        orderRecyclerView.layoutManager = LinearLayoutManager(view.context)
        orderRecyclerView.setHasFixedSize(true)
        orderRecyclerView.setItemViewCacheSize(20)
        orderRecyclerView.adapter = ordersAdapter

        swipeRefreshLayout.setOnRefreshListener {
            shimmerContainerOrder.startShimmer()
            shimmerContainerOrder.visibility = View.VISIBLE
            orderRecyclerView.visibility = View.GONE
            dbViewModel!!.getBookingRequest()
            dbViewModel!!.orderData.observe(viewLifecycleOwner) {
                fetchOrders(it)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        dbViewModel!!.fetchOrders()

        dbViewModel!!.dbLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    dbViewModel!!.orderData.observe(viewLifecycleOwner) {
                        fetchOrders(it)
                    }
                }
                is Response.Failure -> {
                    Toast.makeText(requireActivity(), it.errorMassage, Toast.LENGTH_SHORT).show()
                    shimmerContainerOrder.clearAnimation()
                    shimmerContainerOrder.visibility = View.GONE
                    orderRecyclerView.visibility = View.VISIBLE
                }
            }
        }
        
        return view
    }

    private fun fetchOrders(list: MutableList<DocumentSnapshot>) {
        orderItemsArray = arrayListOf()
        for (i in list) {
            if (i.getString("Seller ID") == sellerUID) {
                val order = OrderItems(
                    i.getString("Product Name"),
                    i.getString("Product Image Url"),
                    i.getString("Payable Amount"),
                    i.getString("Delivery Date"),
                    "Pending",
                    i.getLong("Quantity").toString(),
                    i.getString("User Name"),
                    i.getString("User Number"),
                    i.getString("User Address")
                )
                orderItemsArray.add(order)
            }
        }
        if (orderItemsArray.isEmpty()) {
            noOrderText.visibility = View.VISIBLE
            title.visibility = View.GONE
        } else {
            noOrderText.visibility = View.GONE
            title.visibility = View.VISIBLE
        }
        ordersAdapter.updateOrders(orderItemsArray)
        shimmerContainerOrder.clearAnimation()
        shimmerContainerOrder.visibility = View.GONE
        orderRecyclerView.visibility = View.VISIBLE
    }

}