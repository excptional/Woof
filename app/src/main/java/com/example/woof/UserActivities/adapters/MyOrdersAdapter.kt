package com.example.woof.UserActivities.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.woof.R
import com.example.woof.UserActivities.items.MyOrdersItems
import de.hdodenhof.circleimageview.CircleImageView

class MyOrdersAdapter(
    private val myOrdersItems: ArrayList<MyOrdersItems>
) :
    RecyclerView.Adapter<MyOrdersAdapter.MyOrdersViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrdersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_order_items, parent, false)
        return MyOrdersViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyOrdersViewHolder, position: Int) {
        val currentItem = myOrdersItems[position]
        holder.productName.text = currentItem.productName
        Glide.with(holder.itemView.context).load(currentItem.productImage).into(holder.productImage)
        holder.price.text = "₹${currentItem.amount} INR"
        holder.date.text = "Delivery Date : ${currentItem.deliveryDate}"
        holder.statusText.text = currentItem.deliveryStatus
        holder.quantity.text = "Quantity: ${currentItem.quantity}"
    }

    override fun getItemCount(): Int {
        return myOrdersItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMyOrders(updateMyOrdersItems: ArrayList<MyOrdersItems>) {
        myOrdersItems.clear()
        myOrdersItems.addAll(updateMyOrdersItems)
        notifyDataSetChanged()
    }

    class MyOrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName_myOrders)
        val productImage: ImageView = itemView.findViewById(R.id.productImage_myOrders)
        val price: TextView = itemView.findViewById(R.id.productPrice_myOrders)
        val date: TextView = itemView.findViewById(R.id.date_myOrders)
        val statusText: TextView = itemView.findViewById(R.id.status_myOrders)
        val quantity: TextView = itemView.findViewById(R.id.quantity_myOrders)
    }
}