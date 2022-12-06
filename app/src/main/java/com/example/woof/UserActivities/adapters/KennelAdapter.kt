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
import com.example.woof.UserActivities.items.KennelItems

class KennelAdapter(
    private val KennelItems: ArrayList<KennelItems>
) :
    RecyclerView.Adapter<KennelAdapter.KennelViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KennelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.kennel_item, parent, false)
        return KennelViewHolder(view)
    }

    override fun onBindViewHolder(holder: KennelViewHolder, position: Int) {
        val currentItem = KennelItems[position]
        holder.nameKennel.text = currentItem.kennelName
        holder.locationKennel.text = currentItem.kennelLocation
        holder.ratingTextKennel.text = currentItem.kennelRating
        holder.ratingBarKennel.rating = currentItem.kennelRating!!.toFloat()

        val bundle = Bundle()
        bundle.putString("name", currentItem.kennelName)
        bundle.putString("number", currentItem.kennelNumber)
        bundle.putString("address", currentItem.kennelAddress)
        bundle.putString("website", currentItem.kennelWebsite)
        bundle.putString("ratings", currentItem.kennelRating.toFloat().toString())

        holder.itemLayoutKennel.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_object_map, bundle)
        }
    }

    override fun getItemCount(): Int {
        return KennelItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateKennels(updateKennelsItems: ArrayList<KennelItems>) {
        KennelItems.clear()
        KennelItems.addAll(updateKennelsItems)
        notifyDataSetChanged()
    }

    class KennelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameKennel: TextView = itemView.findViewById(R.id.name_kennel)
        val locationKennel: TextView = itemView.findViewById(R.id.location_kennel)
        val ratingBarKennel: RatingBar = itemView.findViewById(R.id.ratingBar_kennel)
        val ratingTextKennel: TextView = itemView.findViewById(R.id.ratingText_kennel)
        val itemLayoutKennel: CardView = itemView.findViewById(R.id.itemLayout_kennel)
    }
}