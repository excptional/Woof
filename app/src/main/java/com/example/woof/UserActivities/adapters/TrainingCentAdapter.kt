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
import com.example.woof.UserActivities.items.TrainingItems

class TrainingCentAdapter(
    private val TrainingCentItems: ArrayList<TrainingItems>
) :
    RecyclerView.Adapter<TrainingCentAdapter.TrainingCentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingCentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.training_center_item, parent, false)
        return TrainingCentViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrainingCentViewHolder, position: Int) {
        val currentItem = TrainingCentItems[position]
        holder.nameTrainingCent.text = currentItem.trainingCentName
        holder.locationTrainingCent.text = currentItem.trainingCentLocation
        holder.ratingTextTrainingCent.text = currentItem.trainingCentRating
        holder.ratingBarTrainingCent.rating = currentItem.trainingCentRating!!.toFloat()

        val bundle = Bundle()
        bundle.putString("name", currentItem.trainingCentName)
        bundle.putString("number", currentItem.trainingCentNumber)
        bundle.putString("address", currentItem.trainingCentAddress)
        bundle.putString("website", currentItem.trainingCentWebsite)
        bundle.putString("ratings", currentItem.trainingCentRating.toFloat().toString())

        holder.itemLayoutTrainingCent.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_object_map, bundle)
        }
    }

    override fun getItemCount(): Int {
        return TrainingCentItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTrainingCents(updateTrainingCentsItems: ArrayList<TrainingItems>) {
        TrainingCentItems.clear()
        TrainingCentItems.addAll(updateTrainingCentsItems)
        notifyDataSetChanged()
    }

    class TrainingCentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTrainingCent: TextView = itemView.findViewById(R.id.name_trainingCent)
        val locationTrainingCent: TextView = itemView.findViewById(R.id.location_trainingCent)
        val ratingBarTrainingCent: RatingBar = itemView.findViewById(R.id.ratingBar_trainingCent)
        val ratingTextTrainingCent: TextView = itemView.findViewById(R.id.ratingText_trainingCent)
        val itemLayoutTrainingCent: CardView = itemView.findViewById(R.id.itemLayout_trainingCent)
    }
}