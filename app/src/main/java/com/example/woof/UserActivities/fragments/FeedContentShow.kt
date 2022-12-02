package com.example.woof.UserActivities.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.woof.R

class FeedContentShow : Fragment() {

    private lateinit var profileNameContentShow: TextView
    private lateinit var profileImageContentShow: ImageView
    private lateinit var dateContentShow: TextView
    private lateinit var descriptionContentShow: TextView
    private lateinit var contentContentShow: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feed_content_show, container, false)


        profileImageContentShow = view.findViewById(R.id.userProfileImage_contentShow)
        profileNameContentShow = view.findViewById(R.id.userProfileName_contentShow)
        dateContentShow = view.findViewById(R.id.date_contentShow)
        descriptionContentShow = view.findViewById(R.id.description_contentShow)
        contentContentShow = view.findViewById(R.id.content_contentShow)

        profileNameContentShow.text = requireArguments().getString("user name")
        Glide.with(requireContext()).load(requireArguments().getString("user profile image")).into(profileImageContentShow)
        dateContentShow.text = requireArguments().getString("date")
        descriptionContentShow.text = requireArguments().getString("description")
        Glide.with(requireContext()).load(requireArguments().getString("content url")).into(contentContentShow)

        return view
    }

}