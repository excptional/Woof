package com.example.woof.UserActivities.NormalUser

import android.net.Uri

data class PostItem(
    val userName: String? = null,
    val userProfileImage: String? = null,
    val date: String? = null,
    val description: String? = null,
    val content: String? = null,
    val likes: Int? = null,
    val isLike: Boolean? = null
)
