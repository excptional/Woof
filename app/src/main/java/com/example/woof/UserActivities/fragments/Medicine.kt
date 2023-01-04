package com.example.woof.UserActivities.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.woof.R
import com.example.woof.UserActivities.adapters.MedicineAdapter
import com.example.woof.UserActivities.items.MedicineItems
import com.example.woof.repo.Response
import com.example.woof.viewmodel.DBViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentSnapshot
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class Medicine : Fragment() {

    private lateinit var medicineAdapter: MedicineAdapter
    private var medicineItemsArray = arrayListOf<MedicineItems>()
    private var dbViewModel: DBViewModel? = null
    private lateinit var medicineRecyclerView: RecyclerView
    private lateinit var shimmerContainerMedicine: ShimmerFrameLayout
    private lateinit var fab: FloatingActionButton

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(10, 14)
                .setOutputCompressQuality(50)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_medicine, container, false)

        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        val swipeRefreshLayout: SwipeRefreshLayout =
            view.findViewById(R.id.swipeRefreshLayout_medicine)
        medicineRecyclerView = view.findViewById(R.id.recyclerView_medicine)
        fab = view.findViewById(R.id.fab_medicine)

        shimmerContainerMedicine = view.findViewById(R.id.shimmer_view_medicine)
        shimmerContainerMedicine.startShimmer()
        shimmerContainerMedicine.visibility = View.VISIBLE
        medicineRecyclerView.visibility = View.GONE

        medicineAdapter = MedicineAdapter(medicineItemsArray)
        medicineRecyclerView.layoutManager = GridLayoutManager(view.context, 2)
        medicineRecyclerView.setHasFixedSize(true)
        medicineRecyclerView.setItemViewCacheSize(20)
        medicineRecyclerView.adapter = medicineAdapter

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
            if (uri != null) {
                Toast.makeText(view.context, "Prescription successfully uploaded", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(view.context, "No image is selected", Toast.LENGTH_SHORT).show()
            }
        }

        fab.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }


        swipeRefreshLayout.setOnRefreshListener {
            shimmerContainerMedicine.visibility = View.VISIBLE
            medicineRecyclerView.visibility = View.GONE
            shimmerContainerMedicine.startShimmer()
            dbViewModel!!.fetchMedicine()
            dbViewModel!!.medicineData.observe(viewLifecycleOwner) {
                fetchMedicines(it)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        dbViewModel!!.fetchMedicine()

        dbViewModel!!.dbLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    dbViewModel!!.medicineData.observe(viewLifecycleOwner) {
                        fetchMedicines(it)
                    }
                }
                is Response.Failure -> {
                    Toast.makeText(requireActivity(), it.errorMassage, Toast.LENGTH_SHORT).show()
                    medicineRecyclerView.visibility = View.VISIBLE
                    shimmerContainerMedicine.clearAnimation()
                    shimmerContainerMedicine.visibility = View.GONE
                }
            }
        }
        return view
    }

    private fun fetchMedicines(list: MutableList<DocumentSnapshot>) {
        medicineItemsArray = arrayListOf()
        for (i in list) {
            val medicine = MedicineItems(
                i.getString("Name"),
                i.getString("Image Url"),
                i.getString("Description"),
                i.getString("Price"),
                i.getString("Ratings"),
                i.getString("Seller ID")
            )
            medicineItemsArray.add(medicine)
        }
        medicineAdapter.updateMedicine(medicineItemsArray)
        shimmerContainerMedicine.clearAnimation()
        shimmerContainerMedicine.visibility = View.GONE
        medicineRecyclerView.visibility = View.VISIBLE
    }
}