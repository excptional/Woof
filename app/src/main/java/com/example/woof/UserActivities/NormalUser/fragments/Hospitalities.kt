package com.example.woof.UserActivities.NormalUser.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.woof.R
import com.example.woof.UserActivities.NormalUser.adapters.DoctorAdapter
import com.example.woof.UserActivities.NormalUser.adapters.HospitalAdapter
import com.example.woof.UserActivities.NormalUser.items.DoctorItems
import com.example.woof.UserActivities.NormalUser.items.HospitalItem
import com.example.woof.repo.Response
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

class Hospitalities : Fragment() {

    private lateinit var hospitalAdapter: HospitalAdapter
    private lateinit var doctorAdapter: DoctorAdapter
    private var hospitalItemsArray = arrayListOf<HospitalItem>()
    private var doctorItemsArray = arrayListOf<DoctorItems>()
    private var dbViewModel: DBViewModel? = null
    private var appViewModel: AppViewModel? = null
    private lateinit var myUser: FirebaseUser
    private lateinit var progressbarHospital: LottieAnimationView
    private lateinit var mainlayouthospital: LinearLayout

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
        val doctorRecyclerView: RecyclerView = view.findViewById(R.id.doctorRecyclerView)
        mainlayouthospital = view.findViewById(R.id.mainLayout_hospital)
        progressbarHospital = view.findViewById(R.id.progressbar_hospital)
        progressbarHospital.visibility = View.VISIBLE
        mainlayouthospital.visibility = View.GONE

        dbViewModel!!.fetchHospitals()
        appViewModel!!.userdata.observe(viewLifecycleOwner) {
            myUser = it!!
        }

        hospitalAdapter = HospitalAdapter(hospitalItemsArray)
        hospitalRecyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        hospitalRecyclerView.setHasFixedSize(true)
        hospitalRecyclerView.adapter = hospitalAdapter

        doctorAdapter = DoctorAdapter(doctorItemsArray)
        doctorRecyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        doctorRecyclerView.setHasFixedSize(true)
        doctorRecyclerView.adapter = doctorAdapter

        swipeRefreshLayout.setOnRefreshListener {
            progressbarHospital.visibility = View.VISIBLE
            mainlayouthospital.visibility = View.GONE
            dbViewModel!!.fetchPost()
            dbViewModel!!.hospitalData.observe(viewLifecycleOwner) {
                fetchData(it)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        val searchView: SearchView = view.findViewById(R.id.searchbar_hospital)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
//                city = searchView.query
//                fetchData(city)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })

        dbViewModel!!.dbLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    dbViewModel!!.hospitalData.observe(viewLifecycleOwner) {
                        fetchData(it)
                    }
                }
                is Response.Failure -> {
                    Toast.makeText(requireActivity(), it.errorMassage, Toast.LENGTH_SHORT).show()
                    mainlayouthospital.visibility = View.VISIBLE
                    progressbarHospital.visibility = View.GONE
                }
            }
        }

        return view
    }

    private fun fetchData(list: MutableList<DocumentSnapshot>) {
        hospitalItemsArray = arrayListOf()
        doctorItemsArray = arrayListOf()
        for (i in list) {
            val hospital = HospitalItem(
                i.getString("Name"),
                i.getString("Number"),
                i.getString("Address"),
                i.getString("City"),
                i.getString("Website"),
                i.getString("Ratings")
            )
            val doctor = DoctorItems(
                "Doctor",
                "Location"
            )
            hospitalItemsArray.add(hospital)
            doctorItemsArray.add(doctor)
        }
        hospitalAdapter.updateHospitals(hospitalItemsArray)
        doctorAdapter.updateDoctors(doctorItemsArray)
        progressbarHospital.visibility = View.GONE
        mainlayouthospital.visibility = View.VISIBLE
    }
}