package com.example.woof.UserActivities.NormalUser


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.woof.R
import de.hdodenhof.circleimageview.CircleImageView

class FeedAdapter(
    private val postItems: ArrayList<PostItem>
) :
    RecyclerView.Adapter<FeedAdapter.PostViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentItem = postItems[position]

        holder.userNameFeed.text = currentItem.userName
        holder.dateFeed.text = currentItem.date
        holder.descriptionFeed.text = currentItem.description
        Glide.with(holder.itemView.context).load(currentItem.userProfileImage).into(holder.userProfileImageFeed)
        Glide.with(holder.itemView.context).load(currentItem.content).into(holder.contentFeed)
        holder.contentFeed.setOnClickListener {}

    }

    override fun getItemCount(): Int {
        return postItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateHomeFeed(updatePostItems: ArrayList<PostItem>) {
        postItems.clear()
        postItems.addAll(updatePostItems)
        notifyDataSetChanged()
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val userNameFeed: TextView = itemView.findViewById(R.id.userName_feed)
        val userProfileImageFeed: CircleImageView = itemView.findViewById(R.id.userProfileImage_feed)
        val dateFeed: TextView = itemView.findViewById(R.id.date_feed)
        val descriptionFeed: TextView = itemView.findViewById(R.id.description_feed)
        val contentFeed: ImageView = itemView.findViewById(R.id.content_feed)
    }
}
