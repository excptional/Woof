package com.example.woof.UserActivities.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.woof.R
import com.example.woof.UserActivities.adapters.AccAdapter
import com.example.woof.UserActivities.adapters.KennelAdapter
import com.example.woof.UserActivities.adapters.PetShopAdapter
import com.example.woof.UserActivities.items.AccItems
import com.example.woof.UserActivities.items.KennelItems
import com.example.woof.UserActivities.items.PetShopItems
import com.example.woof.repo.Response
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.DocumentSnapshot

class Accessories : Fragment() {

    private lateinit var accAdapter: AccAdapter
    private var accItemsArray = arrayListOf<AccItems>()
    private var dbViewModel: DBViewModel? = null
    private lateinit var accRecyclerView: RecyclerView
    private lateinit var shimmerContainerAcc: ShimmerFrameLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val  view = inflater.inflate(R.layout.fragment_accessories, container, false)

        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        val swipeRefreshLayout: SwipeRefreshLayout =
            view.findViewById(R.id.swipeRefreshLayout_accessories)
        accRecyclerView = view.findViewById(R.id.recyclerView_accessories)
        
        shimmerContainerAcc = view.findViewById(R.id.shimmer_view_accessories)
        shimmerContainerAcc.startShimmer()
        shimmerContainerAcc.visibility = View.VISIBLE
        accRecyclerView.visibility = View.GONE

        accAdapter = AccAdapter(accItemsArray)
        accRecyclerView.layoutManager = GridLayoutManager(view.context, 2)
        accRecyclerView.setHasFixedSize(true)
        accRecyclerView.setItemViewCacheSize(20)
        accRecyclerView.adapter = accAdapter


        swipeRefreshLayout.setOnRefreshListener {
            shimmerContainerAcc.startShimmer()
            shimmerContainerAcc.visibility = View.VISIBLE
            accRecyclerView.visibility = View.GONE
            dbViewModel!!.fetchFoodAndAcc()
            dbViewModel!!.foodAndAccData.observe(viewLifecycleOwner) {
                fetchFoodAccessories(it)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        dbViewModel!!.fetchFoodAndAcc()

        dbViewModel!!.dbLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    dbViewModel!!.foodAndAccData.observe(viewLifecycleOwner) {
                        fetchFoodAccessories(it)
                    }
                }
                is Response.Failure -> {
                    Toast.makeText(requireActivity(), it.errorMassage, Toast.LENGTH_SHORT).show()
                    accRecyclerView.visibility = View.VISIBLE
                    shimmerContainerAcc.clearAnimation()
                    shimmerContainerAcc.visibility = View.GONE
                }
            }
        }
        return view
    }

    private fun fetchFoodAccessories(list: MutableList<DocumentSnapshot>) {
        accItemsArray = arrayListOf()
        for (i in list) {
            val acc = AccItems(
                i.getString("Product Name"),
                i.getString("Product Image"),
                i.getString("Product Price"),
                i.getString("Product Rating"),
                i.getString("Seller ID")
            )
            accItemsArray.add(acc)
        }
        accAdapter.updateAcc(accItemsArray)
        shimmerContainerAcc.clearAnimation()
        shimmerContainerAcc.visibility = View.GONE
        accRecyclerView.visibility = View.VISIBLE
    }

}