package com.example.woof.UserActivities.NormalUser.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.woof.R
import com.example.woof.UserActivities.NormalUser.adapters.HospitalAdapter
import com.example.woof.UserActivities.NormalUser.items.HospitalItem
import com.example.woof.repo.Response
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

class Hospitalities : Fragment() {

    private lateinit var hospitalAdapter: HospitalAdapter
    private var hospitalItemsArray = arrayListOf<HospitalItem>()
    private var dbViewModel: DBViewModel? = null
    private var appViewModel: AppViewModel? = null
    private lateinit var myUser: FirebaseUser
    private lateinit var progressbarHospital: LottieAnimationView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hospitalities, container, false)

        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        val swipeRefreshLayout: SwipeRefreshLayout =
            view.findViewById(R.id.swipeRefreshLayout_hospital)
        val hospitalRecyclerView: RecyclerView = view.findViewById(R.id.hospitalRecyclerView)
        progressbarHospital = view.findViewById(R.id.progressbar_hospital)
        progressbarHospital.visibility = View.VISIBLE

        dbViewModel!!.fetchHospitals()
        appViewModel!!.userdata.observe(viewLifecycleOwner) {
            myUser = it!!
        }

        hospitalAdapter = HospitalAdapter(hospitalItemsArray)
        hospitalRecyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        hospitalRecyclerView.setHasFixedSize(true)
        hospitalRecyclerView.adapter = hospitalAdapter

        swipeRefreshLayout.setOnRefreshListener {
            progressbarHospital.visibility = View.VISIBLE
            dbViewModel!!.fetchPost()
            dbViewModel!!.hospitalData.observe(viewLifecycleOwner) {
                fetchData(it)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        dbViewModel!!.dbLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    dbViewModel!!.hospitalData.observe(viewLifecycleOwner) {
                        fetchData(it)
                    }
                }
                is Response.Failure -> {
                    Toast.makeText(requireActivity(), it.errorMassage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    private fun fetchData(list: MutableList<DocumentSnapshot>) {
        hospitalItemsArray = arrayListOf()
        for (i in list) {
            val hospital = HospitalItem(
                i.getString("Name"),
                i.getString("City"),
                i.getString("Ratings")
            )
            hospitalItemsArray.add(hospital)
        }
        hospitalAdapter.updateHospitals(hospitalItemsArray)
        progressbarHospital.visibility = View.GONE
    }
}