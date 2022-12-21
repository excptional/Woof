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
import com.example.woof.UserActivities.items.OrderItems

class OrdersAdapter(
    private val ordersItems: ArrayList<OrderItems>
) :
    RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_items, parent, false)
        return OrdersViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val currentItem = ordersItems[position]
        holder.productName.text = currentItem.productName
        Glide.with(holder.itemView.context).load(currentItem.productImage).into(holder.productImage)
        holder.price.text = "₹${currentItem.amount} INR"
        holder.date.text = "Delivery Date : ${currentItem.deliveryDate}"
        holder.statusText.text = currentItem.deliveryStatus
        holder.quantity.text = "Quantity : ${currentItem.quantity}"
        holder.buyerName.text = "Name : ${currentItem.buyerName}"
        holder.buyerAddress.text = "Address : ${currentItem.buyerAddress}"
        holder.buyerNumber.text = "Contact No : ${currentItem.buyerNumber}"
    }

    override fun getItemCount(): Int {
        return ordersItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateOrders(updateOrdersItems: ArrayList<OrderItems>) {
        ordersItems.clear()
        ordersItems.addAll(updateOrdersItems)
        notifyDataSetChanged()
    }

    class OrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName_order)
        val productImage: ImageView = itemView.findViewById(R.id.productImage_order)
        val price: TextView = itemView.findViewById(R.id.productPrice_order)
        val date: TextView = itemView.findViewById(R.id.deliveryDate_order)
        val statusText: TextView = itemView.findViewById(R.id.status_order)
        val quantity: TextView = itemView.findViewById(R.id.quantity_order)
        val buyerName: TextView = itemView.findViewById(R.id.buyerName_order)
        val buyerNumber: TextView = itemView.findViewById(R.id.number_order)
        val buyerAddress: TextView = itemView.findViewById(R.id.address_order)
    }
}