package com.example.woof.UserActivities.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.woof.R
import com.example.woof.UserActivities.adapters.GroomingCentAdapter
import com.example.woof.UserActivities.adapters.TrainingCentAdapter
import com.example.woof.UserActivities.items.GroomingItems
import com.example.woof.UserActivities.items.TrainingItems
import com.example.woof.repo.Response
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.google.firebase.firestore.DocumentSnapshot

class TrainingAndGrooming : Fragment() {

    private lateinit var trainingCentAdapter: TrainingCentAdapter
    private lateinit var groomingCentAdapter: GroomingCentAdapter
    private var trainingCentItemsArray = arrayListOf<TrainingItems>()
    private var groomingCentItemsArray = arrayListOf<GroomingItems>()
    private var dbViewModel: DBViewModel? = null
    private var appViewModel: AppViewModel? = null
    private lateinit var progressbarTrainingAndGroomingCent: LottieAnimationView
    private lateinit var mainLayoutTrainingAndGroomingCent: LinearLayout


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_training_and_grooming, container, false)

        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        val swipeRefreshLayout: SwipeRefreshLayout =
            view.findViewById(R.id.swipeRefreshLayout_trainingAndGroomingCent)
        val recyclerViewTrainingCent: RecyclerView = view.findViewById(R.id.recyclerView_trainingCent)
        val recyclerViewGroomingCent: RecyclerView = view.findViewById(R.id.recyclerView_groomingCent)
        mainLayoutTrainingAndGroomingCent = view.findViewById(R.id.mainLayout_trainingAndGroomingLayout)
        progressbarTrainingAndGroomingCent = view.findViewById(R.id.progressbar_trainingAndGroomingCent)
        progressbarTrainingAndGroomingCent.visibility = View.VISIBLE
        mainLayoutTrainingAndGroomingCent.visibility = View.GONE

        trainingCentAdapter = TrainingCentAdapter(trainingCentItemsArray)
        recyclerViewTrainingCent.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        recyclerViewTrainingCent.setHasFixedSize(true)
        recyclerViewTrainingCent.setItemViewCacheSize(20)
        recyclerViewTrainingCent.adapter = trainingCentAdapter

        groomingCentAdapter = GroomingCentAdapter(groomingCentItemsArray)
        recyclerViewGroomingCent.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        recyclerViewGroomingCent.setHasFixedSize(true)
        recyclerViewGroomingCent.setItemViewCacheSize(20)
        recyclerViewGroomingCent.adapter = groomingCentAdapter

        swipeRefreshLayout.setOnRefreshListener {
            progressbarTrainingAndGroomingCent.visibility = View.VISIBLE
            mainLayoutTrainingAndGroomingCent.visibility = View.GONE
            dbViewModel!!.fetchGroomingCenter()
            dbViewModel!!.fetchTrainingCenter()
            dbViewModel!!.trainingCenterData.observe(viewLifecycleOwner) {
                fetchTrainingCentData(it)
                swipeRefreshLayout.isRefreshing = false
            }
            dbViewModel!!.groomingCenterData.observe(viewLifecycleOwner) {
                fetchPetGroomingCentData(it)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        dbViewModel!!.fetchTrainingCenter()
        dbViewModel!!.fetchGroomingCenter()

        dbViewModel!!.dbLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    dbViewModel!!.trainingCenterData.observe(viewLifecycleOwner) {
                        fetchTrainingCentData(it)
                    }
                    dbViewModel!!.groomingCenterData.observe(viewLifecycleOwner){
                        fetchPetGroomingCentData(it)
                    }
                }
                is Response.Failure -> {
                    Toast.makeText(requireActivity(), it.errorMassage, Toast.LENGTH_SHORT).show()
                    mainLayoutTrainingAndGroomingCent.visibility = View.VISIBLE
                    progressbarTrainingAndGroomingCent.visibility = View.GONE
                }
            }
        }

        return view
    }

    private fun fetchTrainingCentData(list: MutableList<DocumentSnapshot>) {
        trainingCentItemsArray = arrayListOf()
        for (i in list) {
            val trainingItem = TrainingItems(
                i.getString("Name"),
                i.getString("Phone Number"),
                i.getString("Address"),
                i.getString("City"),
                i.getString("Website"),
                i.getString("Ratings")
            )
            trainingCentItemsArray.add(trainingItem)
        }
        trainingCentAdapter.updateTrainingCents(trainingCentItemsArray)
        progressbarTrainingAndGroomingCent.visibility = View.GONE
        mainLayoutTrainingAndGroomingCent.visibility = View.VISIBLE
    }

    private fun fetchPetGroomingCentData(list: MutableList<DocumentSnapshot>) {
        groomingCentItemsArray = arrayListOf()
        for (i in list) {
            val groomingItem = GroomingItems(
                i.getString("Name"),
                i.getString("Phone Number"),
                i.getString("Address"),
                i.getString("City"),
                i.getString("Website"),
                i.getString("Ratings")
            )
            groomingCentItemsArray.add(groomingItem)
        }
        groomingCentAdapter.updateGroomingCents(groomingCentItemsArray)
        progressbarTrainingAndGroomingCent.visibility = View.GONE
        mainLayoutTrainingAndGroomingCent.visibility = View.VISIBLE
    }

}