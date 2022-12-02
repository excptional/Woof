package com.example.woof.UserActivities.adapters


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.woof.R
import com.example.woof.UserActivities.items.PostItem
import com.example.woof.viewmodel.DBViewModel
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class FeedAdapter(
    private val postItems: ArrayList<PostItem>,
    private val user: FirebaseUser,
    owner: FragmentActivity
) :
    RecyclerView.Adapter<FeedAdapter.PostViewHolder>() {

    private val dbViewModel = ViewModelProvider(owner)[DBViewModel::class.java]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentItem = postItems[position]
        if (currentItem.description!!.isEmpty()) {
            holder.descriptionFeed.visibility = View.GONE
        }

        if (currentItem.content.isNullOrBlank()) {
            holder.contentFeed.visibility = View.GONE
        }

        if (currentItem.likes == "0") {
            holder.noOfLikes.visibility = View.GONE
        }

        if (currentItem.listOfReactors!!.contains(user.uid)) {
            holder.borderLikeFeed.visibility = View.GONE
            holder.normalLikeFeed.visibility = View.VISIBLE
        } else {
            holder.borderLikeFeed.visibility = View.VISIBLE
            holder.normalLikeFeed.visibility = View.GONE
        }

        holder.userNameFeed.text = currentItem.userName
        holder.dateFeed.text = currentItem.date
        holder.descriptionFeed.text = currentItem.description
        holder.noOfLikes.text = currentItem.likes + " people reacted on this post"
        Glide.with(holder.itemView.context).load(currentItem.userProfileImage)
            .into(holder.userProfileImageFeed)
        Glide.with(holder.itemView.context).load(currentItem.content).into(holder.contentFeed)

        val bundle = Bundle()
        bundle.putString("user name", currentItem.userName)
        bundle.putString("date", currentItem.date)
        bundle.putString("user profile image", currentItem.userProfileImage)
        bundle.putString("description", currentItem.description)
        bundle.putString("content url", currentItem.content)

        holder.contentFeed.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_feed_content_show, bundle)
        }

        holder.borderLikeFeed.setOnClickListener {
            holder.borderLikeFeed.visibility = View.GONE
            holder.normalLikeFeed.visibility = View.VISIBLE
            dbViewModel.giveLikes(user, currentItem.postID)

        }

        holder.normalLikeFeed.setOnClickListener {
            holder.borderLikeFeed.visibility = View.VISIBLE
            holder.normalLikeFeed.visibility = View.GONE
            dbViewModel.giveLikes(user, currentItem.postID)
        }
    }

    override fun getItemCount(): Int {
        return postItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateFeed(updatePostItems: ArrayList<PostItem>) {
        postItems.clear()
        postItems.addAll(updatePostItems)
        notifyDataSetChanged()
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val userNameFeed: TextView = itemView.findViewById(R.id.userName_feed)
        val userProfileImageFeed: CircleImageView =
            itemView.findViewById(R.id.userProfileImage_feed)
        val dateFeed: TextView = itemView.findViewById(R.id.date_feed)
        val descriptionFeed: TextView = itemView.findViewById(R.id.description_feed)
        val contentFeed: ImageView = itemView.findViewById(R.id.content_feed)
        val borderLikeFeed: ImageView = itemView.findViewById(R.id.likeImage_border_feed)
        val normalLikeFeed: ImageView = itemView.findViewById(R.id.likeImage_normal_feed)
        val noOfLikes: TextView = itemView.findViewById(R.id.noOfLikes_feed)
    }
}
