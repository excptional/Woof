package com.example.woof.UserActivities.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.woof.R
import com.example.woof.UserActivities.adapters.AccAdapter
import com.example.woof.UserActivities.adapters.ReminderAdapter
import com.example.woof.UserActivities.items.AccItems
import com.example.woof.UserActivities.items.ReminderItems
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot

class Reminder : Fragment() {

    private lateinit var appViewModel: AppViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var reminderAdapter: ReminderAdapter
    private var reminderItemsArray = arrayListOf<ReminderItems>()
    private lateinit var noAlarmText: TextView
    private lateinit var reminderRecyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var shimmerContainerReminder:  ShimmerFrameLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var myUser: FirebaseUser

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reminder, container, false)

        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        noAlarmText = view.findViewById(R.id.noAlarmSet_reminder)
        reminderRecyclerView = view.findViewById(R.id.recyclerView_reminder)
        fab = view.findViewById(R.id.fab_reminder)

        reminderAdapter = ReminderAdapter(reminderItemsArray, requireContext())
        reminderRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        reminderRecyclerView.setItemViewCacheSize(20)
        reminderRecyclerView.setHasFixedSize(true)
        reminderRecyclerView.adapter = reminderAdapter
        shimmerContainerReminder = view.findViewById(R.id.shimmer_view_reminder)
        swipeRefreshLayout = view.findViewById(R.id.refreshLayout_reminder)
        shimmerContainerReminder.animate()

        appViewModel.userdata.observe(viewLifecycleOwner) {
            myUser = it!!
            dbViewModel.fetchReminder(it.uid)
            dbViewModel.reminderData.observe(viewLifecycleOwner) {
                fetchReminder(it)
            }
        }

        swipeRefreshLayout.setOnRefreshListener {
            shimmerContainerReminder.visibility = View.VISIBLE
            reminderRecyclerView.visibility = View.GONE
            shimmerContainerReminder.startShimmer()
            dbViewModel.fetchReminder(myUser.uid)
            dbViewModel.reminderData.observe(viewLifecycleOwner) {
                fetchReminder(it)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        fab.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_set_reminder)
        }

        return view
    }

    private fun fetchReminder(list: MutableList<DocumentSnapshot>) {
        reminderItemsArray = arrayListOf()
        for (i in list) {
            val reminder = ReminderItems(
                i.getString("type"),
                i.getString("time"),
                i.get("time in millis") as Long,
                i.getString("date"),
                i.getString("massage"),
                i.get("reminder ID") as Long
            )
            reminderItemsArray.add(reminder)
        }
        if(reminderItemsArray.isEmpty()) {
            noAlarmText.visibility = View.VISIBLE
        } else {
            noAlarmText.visibility = View.GONE
        }
        reminderAdapter.updateReminder(reminderItemsArray)
        shimmerContainerReminder.clearAnimation()
        shimmerContainerReminder.visibility = View.GONE
        reminderRecyclerView.visibility = View.VISIBLE
    }
}