package com.example.woof.UserActivities.NormalUser.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.woof.R
import com.example.woof.UserActivities.NormalUser.items.DoctorItems
import com.example.woof.UserActivities.NormalUser.items.HospitalItem

class DoctorAdapter(
    private val DoctorItems: ArrayList<DoctorItems>
) :
    RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.doctor_items, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val currentItem = DoctorItems[position]
        holder.nameDoctor.text = currentItem.doctorName
        holder.locationDoctor.text = currentItem.doctorLocation
//        holder.ratingTextDoctor.text = currentItem.rating
//        holder.ratingBarDoctor.rating = currentItem.rating!!.toFloat()
        holder.itemLayoutDoctor.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return DoctorItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDoctors(updateDoctorsItems: ArrayList<DoctorItems>) {
        DoctorItems.clear()
        DoctorItems.addAll(updateDoctorsItems)
        notifyDataSetChanged()
    }

    class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameDoctor: TextView = itemView.findViewById(R.id.name_doctor)
        val locationDoctor: TextView = itemView.findViewById(R.id.location_doctor)
//        val ratingBarDoctor: RatingBar = itemView.findViewById(R.id.ratingBar_Doctor)
//        val ratingTextDoctor: TextView = itemView.findViewById(R.id.ratingText_Doctor)
        val itemLayoutDoctor: CardView = itemView.findViewById(R.id.itemLayout_doctor)
    }
}