package com.example.woof.UserActivities.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.woof.R
import com.example.woof.UserActivities.items.GroomingItems

class GroomingCentAdapter(
    private val groomingCentItems: ArrayList<GroomingItems>
) :
    RecyclerView.Adapter<GroomingCentAdapter.GroomingCentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroomingCentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.grooming_center_item, parent, false)
        return GroomingCentViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroomingCentViewHolder, position: Int) {
        val currentItem = groomingCentItems[position]
        holder.nameGroomingCent.text = currentItem.groomingCentName
        holder.locationGroomingCent.text = currentItem.groomingCentLocation
        holder.ratingTextGroomingCent.text = currentItem.groomingCentRating
        holder.ratingBarGroomingCent.rating = currentItem.groomingCentRating!!.toFloat()

        val bundle = Bundle()
        bundle.putString("name", currentItem.groomingCentName)
        bundle.putString("number", currentItem.groomingCentNumber)
        bundle.putString("address", currentItem.groomingCentAddress)
        bundle.putString("website", currentItem.groomingCentWebsite)
        bundle.putString("ratings", currentItem.groomingCentRating.toFloat().toString())

        holder.itemLayoutGroomingCent.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_object_map, bundle)
        }
    }

    override fun getItemCount(): Int {
        return groomingCentItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateGroomingCents(updateGroomingCentsItems: ArrayList<GroomingItems>) {
        groomingCentItems.clear()
        groomingCentItems.addAll(updateGroomingCentsItems)
        notifyDataSetChanged()
    }

    class GroomingCentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameGroomingCent: TextView = itemView.findViewById(R.id.name_groomingCent)
        val locationGroomingCent: TextView = itemView.findViewById(R.id.location_groomingCent)
        val ratingBarGroomingCent: RatingBar = itemView.findViewById(R.id.ratingBar_groomingCent)
        val ratingTextGroomingCent: TextView = itemView.findViewById(R.id.ratingText_groomingCent)
        val itemLayoutGroomingCent: CardView = itemView.findViewById(R.id.itemLayout_groomingCent)
    }
}