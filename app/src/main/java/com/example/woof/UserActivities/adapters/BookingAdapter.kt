package com.example.woof.UserActivities.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.woof.R
import com.example.woof.UserActivities.items.BookingItems
import com.example.woof.viewmodel.DBViewModel
import de.hdodenhof.circleimageview.CircleImageView

class BookingAdapter(
    private val BookingItems: ArrayList<BookingItems>,
    private val owner: ViewModelStoreOwner
) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.booking_item, parent, false)
        return BookingViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {

        val dbViewModel: DBViewModel = ViewModelProvider(owner)[DBViewModel::class.java]

        val currentItem = BookingItems[position]

        if(currentItem.status == "Accepted"){
            holder.acceptBTNBookings.visibility = View.INVISIBLE
            holder.rejectBTNBookings.visibility = View.INVISIBLE
        }

        holder.ownerNameBookings.text = currentItem.ownerName
        Glide.with(holder.itemView.context).load(currentItem.ownerImageUrl).into(holder.ownerImageBookings)
        holder.petSpeciesBookings.text = "Species : ${currentItem.petSpecies}"
        holder.issueBookings.text = "Issue : ${currentItem.issue}"
        holder.timingBookings.text = "Time : ${currentItem.timing}"
        holder.dateBookings.text = "Date : ${currentItem.date}"
        holder.statusBookings.text = "Status : ${currentItem.status}"

        holder.acceptBTNBookings.setOnClickListener {
            dbViewModel.updateStatus(currentItem.id!!, "Accepted")
            holder.acceptBTNBookings.visibility = View.INVISIBLE
            holder.rejectBTNBookings.visibility = View.INVISIBLE
        }

        holder.rejectBTNBookings.setOnClickListener {
            dbViewModel.updateStatus(currentItem.id!!, "Rejected")
            holder.acceptBTNBookings.visibility = View.INVISIBLE
            holder.rejectBTNBookings.visibility = View.INVISIBLE
        }

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
        val timingBookings: TextView = itemView.findViewById(R.id.timings_bookings)
        val statusBookings: TextView = itemView.findViewById(R.id.status_bookings)
        val acceptBTNBookings: ImageButton = itemView.findViewById(R.id.acceptBTN_bookings)
        val rejectBTNBookings: ImageButton = itemView.findViewById(R.id.rejectBTN_bookings)
    }
}