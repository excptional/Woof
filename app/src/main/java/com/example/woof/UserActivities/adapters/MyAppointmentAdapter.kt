package com.example.woof.UserActivities.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.woof.R
import com.example.woof.UserActivities.items.MyAppointmentsItems
import com.google.firebase.database.collection.LLRBNode.Color
import de.hdodenhof.circleimageview.CircleImageView

class MyAppointmentAdapter(
    private val myAppointmentItems: ArrayList<MyAppointmentsItems>
) :
    RecyclerView.Adapter<MyAppointmentAdapter.MyAppointmentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_appointments_items, parent, false)
        return MyAppointmentViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyAppointmentViewHolder, position: Int) {
        val currentItem = myAppointmentItems[position]
        holder.docName.text = currentItem.doctorName
        Glide.with(holder.itemView.context).load(currentItem.doctorImage).into(holder.docImage)
        holder.speciality.text = currentItem.speciality
        holder.date.text = "Date : ${currentItem.date}"
        holder.time.text = "Time : ${currentItem.time}"
        holder.statusText.text = currentItem.status
        Glide.with(holder.itemView.context).load(currentItem.doctorImage).into(holder.docImage)
        if(currentItem.status == "Rejected"){
            holder.statusBox.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
        }else{
            holder.statusBox.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
        }

    }

    override fun getItemCount(): Int {
        return myAppointmentItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMyAppointment(updateMyAppointmentItems: ArrayList<MyAppointmentsItems>) {
        myAppointmentItems.clear()
        myAppointmentItems.addAll(updateMyAppointmentItems)
        notifyDataSetChanged()
    }

    class MyAppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val docName: TextView = itemView.findViewById(R.id.name_appointment)
        val docImage: CircleImageView = itemView.findViewById(R.id.image_appointment)
        val speciality: TextView = itemView.findViewById(R.id.speciality_appointment)
        val date: TextView = itemView.findViewById(R.id.date_appointment)
        val time: TextView = itemView.findViewById(R.id.time_appointment)
        val statusText: TextView = itemView.findViewById(R.id.status_appointment)
        val statusBox: CardView = itemView.findViewById(R.id.statusBox_appointment)
    }
}