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
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.woof.R
import com.example.woof.UserActivities.items.MedicineItems

class MedicineAdapter(
    private val MedicineItems: ArrayList<MedicineItems>
) :
    RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.medicine_item, parent, false)
        return MedicineViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val currentItem = MedicineItems[position]
        holder.medicineNameMedicine.text = currentItem.medicineName
        Glide.with(holder.itemView.context).load(currentItem.medicineImageUrl).into(holder.medicineImageMedicine)
        holder.medicinePrice.text = "₹" + currentItem.medicinePrice
        holder.productRatingTextMedicine.text = currentItem.medicineRating
        holder.productRatingBarMedicine.rating = currentItem.medicineRating!!.toFloat()

        val bundle = Bundle()
        bundle.putString("name", currentItem.medicineName)
        bundle.putString("image", currentItem.medicineImageUrl)
        bundle.putString("price", currentItem.medicinePrice)
        bundle.putString("description", currentItem.medicineDescription)
        bundle.putString("rating", currentItem.medicineRating)
        bundle.putString("sellerId", currentItem.sellerID)

        holder.itemLayoutMedicine.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_medicines_order, bundle)
        }
    }

    override fun getItemCount(): Int {
        return MedicineItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMedicine(updateMedicineItems: ArrayList<MedicineItems>) {
        MedicineItems.clear()
        MedicineItems.addAll(updateMedicineItems)
        notifyDataSetChanged()
    }

    class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicineNameMedicine: TextView = itemView.findViewById(R.id.productName_medicine)
        val medicineImageMedicine: ImageView = itemView.findViewById(R.id.productImage_medicine)
        val medicinePrice: TextView = itemView.findViewById(R.id.productPrice_medicine)
        val productRatingBarMedicine: RatingBar = itemView.findViewById(R.id.ratingBar_medicine)
        val productRatingTextMedicine: TextView = itemView.findViewById(R.id.ratingText_medicine)
        val itemLayoutMedicine: CardView = itemView.findViewById(R.id.itemLayout_medicine)
    }
}