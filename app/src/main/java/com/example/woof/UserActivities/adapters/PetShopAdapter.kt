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
import com.example.woof.UserActivities.items.PetShopItems

class PetShopAdapter(
    private val PetShopItems: ArrayList<PetShopItems>
) :
    RecyclerView.Adapter<PetShopAdapter.PetShopViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetShopViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.petshop_item, parent, false)
        return PetShopViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetShopViewHolder, position: Int) {
        val currentItem = PetShopItems[position]
        holder.namePetShop.text = currentItem.petShopName
        holder.locationPetShop.text = currentItem.petShopLocation
        holder.ratingTextPetShop.text = currentItem.petShopRating
        holder.ratingBarPetShop.rating = currentItem.petShopRating!!.toFloat()

        val bundle = Bundle()
        bundle.putString("name", currentItem.petShopName)
        bundle.putString("number", currentItem.petShopNumber)
        bundle.putString("address", currentItem.petShopAddress)
        bundle.putString("website", currentItem.petShopWebsite)
        bundle.putString("ratings", currentItem.petShopRating.toFloat().toString())

        holder.itemLayoutPetShop.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_object_map, bundle)
        }
    }

    override fun getItemCount(): Int {
        return PetShopItems.size
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updatePetShops(updatePetShopsItems: ArrayList<PetShopItems>) {
        PetShopItems.clear()
        PetShopItems.addAll(updatePetShopsItems)
        notifyDataSetChanged()
    }

    class PetShopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namePetShop: TextView = itemView.findViewById(R.id.name_petShop)
        val locationPetShop: TextView = itemView.findViewById(R.id.location_petShop)
        val ratingBarPetShop: RatingBar = itemView.findViewById(R.id.ratingBar_petShop)
        val ratingTextPetShop: TextView = itemView.findViewById(R.id.ratingText_petShop)
        val itemLayoutPetShop: CardView = itemView.findViewById(R.id.itemLayout_petShop)
    }
}