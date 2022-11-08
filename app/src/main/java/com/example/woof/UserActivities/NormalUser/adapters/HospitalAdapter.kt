package com.example.woof.UserActivities.NormalUser.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.woof.R
import com.example.woof.UserActivities.NormalUser.items.HospitalItem
import com.example.woof.UserActivities.NormalUser.items.PostItem

class HospitalAdapter (
    private val hospitalItems: ArrayList<HospitalItem>
) :
    RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hospital_items, parent, false)
        return HospitalViewHolder(view)
    }

    override fun onBindViewHolder(holder: HospitalViewHolder, position: Int) {
        val currentItem = hospitalItems[position]
        holder.nameHospital.text = currentItem.hospitalName
        holder.locationHospital.text = currentItem.hospitalLocation
        holder.ratingTextHospital.text = currentItem.rating
        holder.ratingBarHospital.rating = currentItem.rating!!.toFloat()
    }

    override fun getItemCount(): Int {
        return hospitalItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateHospitals(updateHospitalsItems: ArrayList<HospitalItem>) {
        hospitalItems.clear()
        hospitalItems.addAll(updateHospitalsItems)
        notifyDataSetChanged()
    }

    class HospitalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameHospital: TextView = itemView.findViewById(R.id.name_hospital)
        val locationHospital: TextView = itemView.findViewById(R.id.location_hospital)
        val ratingBarHospital: RatingBar = itemView.findViewById(R.id.ratingBar_hospital)
        val ratingTextHospital: TextView = itemView.findViewById(R.id.ratingText_hospital)
    }
}