package com.example.woof.UserActivities.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.woof.R
import com.example.woof.UserActivities.items.DoctorItems

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
        holder.specialityDoctor.text = currentItem.doctorSpeciality

        val bundle = Bundle()
        bundle.putString("name", currentItem.doctorName)
        bundle.putString("speciality", currentItem.doctorSpeciality)
        bundle.putString("image url", currentItem.doctorImage)
        bundle.putString("id", currentItem.doctorID)

        holder.itemLayoutDoctor.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_book_doctor, bundle)
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
        val specialityDoctor: TextView = itemView.findViewById(R.id.speciality_doctor)
        val itemLayoutDoctor: CardView = itemView.findViewById(R.id.itemLayout_doctor)
    }
}