package com.example.woof.UserActivities.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.woof.R
import com.example.woof.UserActivities.items.BookingItems
import de.hdodenhof.circleimageview.CircleImageView

class BookingAdapter(
    private val BookingItems: ArrayList<BookingItems>
) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.booking_item, parent, false)
        return BookingViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val currentItem = BookingItems[position]
        holder.ownerNameBookings.text = currentItem.ownerName
        Glide.with(holder.itemView.context).load(currentItem.ownerImageUrl).into(holder.ownerImageBookings)
        holder.petSpeciesBookings.text = currentItem.petSpecies
        holder.issueBookings.text = currentItem.issue
        holder.dateBookings.text = currentItem.date
        holder.timingBookings.text = currentItem.timing
        holder.statusBookings.text = currentItem.status

    }

    override fun getItemCount(): Int {
        return BookingItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateBooking(updateBookingItems: ArrayList<BookingItems>) {
        BookingItems.clear()
        BookingItems.addAll(updateBookingItems)
        notifyDataSetChanged()
    }

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ownerNameBookings : TextView = itemView.findViewById(R.id.ownerName_bookings)
        val ownerImageBookings : CircleImageView = itemView.findViewById(R.id.ownerImage_bookings)
        val petSpeciesBookings: TextView = itemView.findViewById(R.id.species_bookings)
        val issueBookings: TextView = itemView.findViewById(R.id.issue_bookings)
        val dateBookings: TextView = itemView.findViewById(R.id.date_bookings)
        val timingBookings: TextView = itemView.findViewById(R.id.date_bookings)
        val statusBookings: TextView = itemView.findViewById(R.id.status_bookings)
        val acceptBTNBookings: ImageButton = itemView.findViewById(R.id.acceptBTN_bookings)
        val rejectBTNBookings: ImageButton = itemView.findViewById(R.id.rejectBTN_bookings)
    }
}