package com.example.woof.UserActivities.NormalUser.fragments

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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.woof.R
import com.example.woof.UserActivities.NormalUser.FeedAdapter
import com.example.woof.UserActivities.NormalUser.PostItem
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseUser
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

class Feed : Fragment() {

    private lateinit var feedAdapter: FeedAdapter
    private var postItemsArray = arrayListOf<PostItem>()
    private var dbViewModel: DBViewModel? = null
    private var appViewModel: AppViewModel? = null
    private lateinit var myUser: FirebaseUser
    private lateinit var userName:String
    private lateinit var userImageUrl: String
    private var contentUri: Uri? = null

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(16, 11)
                .setOutputCompressQuality(50)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)
        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        appViewModel!!.userdata.observe(requireActivity()) { user ->
            if (user != null) {
                myUser = user
                dbViewModel!!.getProfileData(user)
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
        val uploadAnotherContent: ImageView = dialog.findViewById(R.id.upload_another_content_post)
        val clearContent: ImageView = dialog.findViewById(R.id.clear_content_post)
        val sendBtn: ImageView = dialog.findViewById(R.id.send_post)

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

        uploadAnotherContent.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }

        sendBtn.setOnClickListener {
            if(contentUri == null){
                dbViewModel!!.uploadPostWithOutImage(myUser, userName, userImageUrl, postDescription.text.toString())
            }else{
                dbViewModel!!.uploadPostWithImage(myUser, userName, userImageUrl, postDescription.text.toString(), contentUri)
            }
            postDescription.text = null
            postContentImage.setImageURI(null)
            dialog.hide()
            Toast.makeText(requireContext(), "Uploading...", Toast.LENGTH_SHORT).show()
        }

        val homeRecyclerView: RecyclerView = view.findViewById(R.id.homeRecyclerView)
        val fab: FloatingActionButton = view.findViewById(R.id.fab_feed)

        fab.setOnClickListener {
            dbViewModel!!.profileData.observe(requireActivity()) { dataList ->
                postProfileName.text = dataList[1]!!
                Glide.with(requireActivity()).load(dataList[2]!!).into(postProfileImage)
                userName = dataList[1]!!
                userImageUrl = dataList[2]!!
            }
            dialog.show()
        }

        homeRecyclerView.layoutManager = LinearLayoutManager(view.context)
        homeRecyclerView.setHasFixedSize(true)
        feedAdapter = FeedAdapter(postItemsArray)
        fetchData()
        homeRecyclerView.adapter = feedAdapter

        return view
    }

    fun getTimeAgo(date: String) :  String?{
        val date2 = SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.getDefault()).format(Date())
        val prettyTime = PrettyTime(Locale.getDefault())
        return prettyTime.format(Date(date2))
    }

    private fun fetchData() {
        postItemsArray = arrayListOf()
        for (i in 1 until 51) {
            val post = PostItem(
                "UserName$i",
                null,
                "date$i",
                "description$i",
                null
            )
            postItemsArray.add(post)
        }
        feedAdapter.updateHomeFeed(postItemsArray)
    }
}