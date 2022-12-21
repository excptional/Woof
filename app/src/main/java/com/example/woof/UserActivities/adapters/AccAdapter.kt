package com.example.woof.UserActivities.adapters

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.woof.R
import com.example.woof.UserActivities.items.AccItems

class AccAdapter(
private val AccItems: ArrayList<AccItems>
) :
RecyclerView.Adapter<AccAdapter.AccViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.accessories_item, parent, false)
        return AccViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AccViewHolder, position: Int) {
        val currentItem = AccItems[position]
        holder.productNameAcc.text = currentItem.productName
        Glide.with(holder.itemView.context).load(currentItem.productImageUrl).into(holder.productImageAcc)
        holder.productPrice.text = "₹" + currentItem.productPrice
        holder.productRatingTextAcc.text = currentItem.productRating
        holder.productRatingBarAcc.rating = currentItem.productRating!!.toFloat()

        val bundle = Bundle()
        bundle.putString("productName", currentItem.productName)
        bundle.putString("productImage", currentItem.productImageUrl)
        bundle.putString("productPrice", currentItem.productPrice)
        bundle.putString("productRating", currentItem.productRating)
        bundle.putString("sellerId", currentItem.sellerID)

        holder.itemLayoutAcc.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_order_place, bundle)
        }
    }

    override fun getItemCount(): Int {
        return AccItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAcc(updateAccItems: ArrayList<AccItems>) {
        AccItems.clear()
        AccItems.addAll(updateAccItems)
        notifyDataSetChanged()
    }

    class AccViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameAcc: TextView = itemView.findViewById(R.id.productName_accessories)
        val productImageAcc: ImageView = itemView.findViewById(R.id.productImage_accessories)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice_accessories)
        val productRatingBarAcc: RatingBar = itemView.findViewById(R.id.ratingBar_accessories)
        val productRatingTextAcc: TextView = itemView.findViewById(R.id.ratingText_accessories)
        val itemLayoutAcc:  CardView = itemView.findViewById(R.id.itemLayout_accessories)
    }
}