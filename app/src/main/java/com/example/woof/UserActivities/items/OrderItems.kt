package com.example.woof.UserActivities.items

data class OrderItems(
    val productName: String? = null,
    val productImage: String? = null,
    val amount: String? = null,
    val deliveryDate: String? = null,
    val deliveryStatus: String? = null,
    val quantity: String? = null,
    val buyerName: String? = null,
    val buyerNumber: String? = null,
    val buyerAddress: String? = null
)