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
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.woof.R
import com.example.woof.UserActivities.adapters.KennelAdapter
import com.example.woof.UserActivities.adapters.PetShopAdapter
import com.example.woof.UserActivities.items.KennelItems
import com.example.woof.UserActivities.items.PetShopItems
import com.example.woof.repo.Response
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot

class Shop : Fragment() {

    private lateinit var kennelAdapter: KennelAdapter
    private lateinit var petShopAdapter: PetShopAdapter
    private var kennelItemsArray = arrayListOf<KennelItems>()
    private var petShopItemsArray = arrayListOf<PetShopItems>()
    private var dbViewModel: DBViewModel? = null
    private var appViewModel: AppViewModel? = null
    private lateinit var progressbarkennel: LottieAnimationView
    private lateinit var mainlayoutkennel: LinearLayout
 
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        
        val view = inflater.inflate(R.layout.fragment_shop, container, false)

        val imageList = ArrayList<SlideModel>()

        imageList.add(SlideModel("https://firebasestorage.googleapis.com/v0/b/woof-uit.appspot.com/o/Sliders%2FKennel1.jpg?alt=media&token=fd1cac63-65bc-4bf6-9453-2c10be8777e0", ScaleTypes.FIT))
        imageList.add(SlideModel("https://firebasestorage.googleapis.com/v0/b/woof-uit.appspot.com/o/Sliders%2Fpetshop1.png?alt=media&token=8e2fac68-61b8-4a44-9d0c-fdc2454c1bad", ScaleTypes.FIT))
        imageList.add(SlideModel("https://firebasestorage.googleapis.com/v0/b/woof-uit.appspot.com/o/Sliders%2Fpetshop2.png?alt=media&token=4aeb0f5d-ebdb-42a0-9ae6-80f166885a19", ScaleTypes.FIT))

        val imageSlider = view.findViewById<ImageSlider>(R.id.shop_slider)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)

        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        val swipeRefreshLayout: SwipeRefreshLayout =
            view.findViewById(R.id.swipeRefreshLayout_kennelAndPetShop)
        val kennelRecyclerView: RecyclerView = view.findViewById(R.id.recyclerView_kennel)
        val petShopRecyclerView: RecyclerView = view.findViewById(R.id.recyclerView_petShops)
        mainlayoutkennel = view.findViewById(R.id.mainLayout_kennelAndPetShop)
        progressbarkennel = view.findViewById(R.id.progressbar_kennel)
        progressbarkennel.visibility = View.VISIBLE
        mainlayoutkennel.visibility = View.GONE

        kennelAdapter = KennelAdapter(kennelItemsArray)
        kennelRecyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        kennelRecyclerView.setHasFixedSize(true)
        kennelRecyclerView.setItemViewCacheSize(20)
        kennelRecyclerView.adapter = kennelAdapter

        petShopAdapter = PetShopAdapter(petShopItemsArray)
        petShopRecyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        petShopRecyclerView.setHasFixedSize(true)
        petShopRecyclerView.setItemViewCacheSize(20)
        petShopRecyclerView.adapter = petShopAdapter

        swipeRefreshLayout.setOnRefreshListener {
            progressbarkennel.visibility = View.VISIBLE
            mainlayoutkennel.visibility = View.GONE
            dbViewModel!!.fetchPetShop()
            dbViewModel!!.fetchKennels()
            dbViewModel!!.kennelData.observe(viewLifecycleOwner) {
                fetchKennelData(it)
                swipeRefreshLayout.isRefreshing = false
            }
            dbViewModel!!.petShopData.observe(viewLifecycleOwner) {
                fetchPetShopData(it)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        dbViewModel!!.fetchPetShop()
        dbViewModel!!.fetchKennels()

        dbViewModel!!.dbLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    dbViewModel!!.petShopData.observe(viewLifecycleOwner) {
                        fetchPetShopData(it)
                    }
                    dbViewModel!!.kennelData.observe(viewLifecycleOwner){
                        fetchKennelData(it)
                    }
                }
                is Response.Failure -> {
                    Toast.makeText(requireActivity(), it.errorMassage, Toast.LENGTH_SHORT).show()
                    mainlayoutkennel.visibility = View.VISIBLE
                    progressbarkennel.visibility = View.GONE
                }
            }
        }
        return view
    }

    private fun fetchKennelData(list: MutableList<DocumentSnapshot>) {
        kennelItemsArray = arrayListOf()
        for (i in list) {
            val kennel = KennelItems(
                i.getString("Name"),
                i.getString("Phone Number"),
                i.getString("Address"),
                i.getString("City"),
                i.getString("Website"),
                i.getString("Ratings")
            )
            kennelItemsArray.add(kennel)
        }
        kennelAdapter.updateKennels(kennelItemsArray)
        progressbarkennel.visibility = View.GONE
        mainlayoutkennel.visibility = View.VISIBLE
    }

    private fun fetchPetShopData(list: MutableList<DocumentSnapshot>) {
        petShopItemsArray = arrayListOf()
        for (i in list) {
            val petShop = PetShopItems(
                i.getString("Name"),
                i.getString("Phone Number"),
                i.getString("Address"),
                i.getString("City"),
                i.getString("Website"),
                i.getString("Ratings")
            )
            petShopItemsArray.add(petShop)
        }
        petShopAdapter.updatePetShops(petShopItemsArray)
        progressbarkennel.visibility = View.GONE
        mainlayoutkennel.visibility = View.VISIBLE
    }
}