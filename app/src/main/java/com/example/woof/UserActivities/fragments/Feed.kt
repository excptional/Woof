package com.example.woof.UserActivities.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.woof.R
import com.example.woof.UserActivities.adapters.FeedAdapter
import com.example.woof.UserActivities.items.PostItem
import com.example.woof.repo.Response
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

class Feed : Fragment() {

    private lateinit var feedAdapter: FeedAdapter
    private var postItemsArray = arrayListOf<PostItem>()
    private var dbViewModel: DBViewModel? = null
    private var appViewModel: AppViewModel? = null
    private lateinit var myUser: FirebaseUser
    private lateinit var userName: String
    private lateinit var userImageUrl: String
    private var contentUri: Uri? = null
    private lateinit var shimmerContainerFeed: ShimmerFrameLayout

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(12, 10)
                .setOutputCompressQuality(50)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)
        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_feed)
        val homeRecyclerView: RecyclerView = view.findViewById(R.id.feedRecyclerView)
        val fab: FloatingActionButton = view.findViewById(R.id.fab_feed)
        shimmerContainerFeed = view.findViewById(R.id.shimmer_view_feed)

        appViewModel!!.userdata.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                myUser = user
                feedAdapter =
                    FeedAdapter(postItemsArray, user, requireActivity())
                homeRecyclerView.layoutManager = LinearLayoutManager(view.context)
                homeRecyclerView.setHasFixedSize(true)
                homeRecyclerView.adapter = feedAdapter
                dbViewModel!!.fetchPost()
                dbViewModel!!.getProfileData(user)
            }
        }

        swipeRefreshLayout.setOnRefreshListener {
            shimmerContainerFeed.startShimmer()
            shimmerContainerFeed.visibility = View.VISIBLE
            dbViewModel!!.fetchPost()
            dbViewModel!!.postData.observe(viewLifecycleOwner) {
                fetchData(it)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        val dialog = Dialog(requireContext())
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_post)
        val postProfileName: TextView = dialog.findViewById(R.id.userName_post)
        val postProfileImage: CircleImageView = dialog.findViewById(R.id.userImage_post)
        val postDescription: TextInputEditText = dialog.findViewById(R.id.description_post)
        val postContentImage: ImageView = dialog.findViewById(R.id.content_post)
        val postContentLayout: RelativeLayout = dialog.findViewById(R.id.post_content_layout)
        val emptyContentLayout: LinearLayout = dialog.findViewById(R.id.empty_content_layout_post)
        val uploadContent: ImageView = dialog.findViewById(R.id.upload_content_post)
        val clearContent: ImageView = dialog.findViewById(R.id.clear_content_post)
        val sendBtn: CardView = dialog.findViewById(R.id.send_post)
        val cancelBtn: CardView = dialog.findViewById(R.id.cancel_post)

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
            if (uri != null) {
                postContentImage.setImageURI(uri)
                contentUri = uri
                emptyContentLayout.visibility = View.GONE
                postContentLayout.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireContext(), "No image is selected", Toast.LENGTH_SHORT).show()
            }
        }

        uploadContent.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }

        clearContent.setOnClickListener {
            emptyContentLayout.visibility = View.VISIBLE
            postContentLayout.visibility = View.GONE
            postContentImage.setImageURI(null)
        }

        cancelBtn.setOnClickListener {
            postContentImage.setImageURI(null)
            postDescription.text = null
            dialog.hide()
            Toast.makeText(requireContext(), " Post cancel", Toast.LENGTH_SHORT).show()
        }

        sendBtn.setOnClickListener {
            if ((contentUri == null) && postDescription.text.isNullOrBlank()) {
                Toast.makeText(
                    requireContext(),
                    "You can't upload a empty post",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (contentUri == null) {
                dbViewModel!!.uploadPostWithOutImage(
                    myUser,
                    userName,
                    userImageUrl,
                    postDescription.text.toString()
                )
                Toast.makeText (requireContext(),
                    "Your post uploaded",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                dbViewModel!!.uploadPostWithImage(
                    myUser,
                    userName,
                    userImageUrl,
                    postDescription.text.toString(),
                    contentUri
                )
                Toast . makeText (requireContext(),
                    "Your post uploaded",
                    Toast.LENGTH_SHORT
                ).show()
            }
            postDescription.text = null
            postContentImage.setImageURI(null)
            dialog.hide()

        }

        fab.setOnClickListener {
            dbViewModel!!.profileData.observe(viewLifecycleOwner) { dataList ->
                postProfileName.text = dataList[1]!!
                Glide.with(requireActivity()).load(dataList[2]!!).into(postProfileImage)
                userName = dataList[1]!!
                userImageUrl = dataList[2]!!
            }
            dialog.show()
        }

        dbViewModel!!.dbLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    dbViewModel!!.postData.observe(viewLifecycleOwner) {
                        fetchData(it)
                    }
                }
                is Response.Failure -> {
                    Toast.makeText(requireActivity(), it.errorMassage, Toast.LENGTH_SHORT).show()
                }
            }
        }
        return view
    }

    private fun fetchData(list: MutableList<DocumentSnapshot>) {
        postItemsArray = arrayListOf()
        for (i in list) {
            val prettyTime = PrettyTime(Locale.getDefault())
            val ago: String = prettyTime.format(Date(i.getString("Upload Date")))
            val post = PostItem(
                i.getString("Profile Name"),
                i.getString("Profile Image Url"),
                ago,
                i.getString("Description"),
                i.getString("Content Url"),
                i.getString("No Of Likes"),
                i.getString("List Of Reactors"),
                i.getString("Post ID")
            )
            postItemsArray.add(post)
        }
        feedAdapter.updateFeed(postItemsArray)
        shimmerContainerFeed.clearAnimation()
        shimmerContainerFeed.visibility = View.GONE
    }
}