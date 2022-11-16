package com.example.woof.UserActivities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.woof.AuthFiles.AuthenticationActivity
import com.example.woof.R
import com.example.woof.repo.Response
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseUser
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView

class UserProfileActivity : AppCompatActivity() {

    private var dbViewModel: DBViewModel? = null
    private var appViewModel: AppViewModel? = null
    private lateinit var myUser: FirebaseUser
    private val profileName: TextView
        get() = findViewById(R.id.yourNameText)
    private val profileImage: CircleImageView
        get() = findViewById(R.id.profileImage)
    private val phoneNo: TextView
        get() = findViewById(R.id.phoneNumberText)
    private val uploadImage: ImageView
        get() = findViewById(R.id.setImageBtn)
    private val progressBar: LottieAnimationView
        get() = findViewById(R.id.progressbarProfile)
    private val profileMainLayout: LinearLayout
        get() = findViewById(R.id.profileLayout)
    private val petName: TextView
        get() = findViewById(R.id.petNameText)
    private val regNo: TextView
        get() = findViewById(R.id.regNoText)
    private val tradeNo: TextView
        get() = findViewById(R.id.tradeLicenseText)
    private val userNameLayout: LinearLayout
        get() = findViewById(R.id.nameEditLayout)
    private val petNameLayout: LinearLayout
        get() = findViewById(R.id.petNameEditLayout)
    private val regNoLayout: LinearLayout
        get() = findViewById(R.id.regNoLayout)
    private val tradeNoLayout: LinearLayout
        get() = findViewById(R.id.tradeLicenseEditLayout)
    private val signOutBtn: CardView
        get() = findViewById(R.id.sign_out_btn)
    private val whiteLayout: LinearLayout
        get() = findViewById(R.id.signUpWhiteLayoutProfile)

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(4, 4)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setOutputCompressQuality(30)
                .getIntent(this@UserProfileActivity)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        setSupportActionBar(this.findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)


        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
            if (uri != null) {
                progressBar.visibility = View.VISIBLE
                whiteLayout.visibility = View.VISIBLE
                uploadImage.isClickable = false
                userNameLayout.isClickable = false
                petNameLayout.isClickable = false
                imageUploadToStorage(uri)
            } else {
                Toast.makeText(this, "No image is uploaded", Toast.LENGTH_SHORT).show()
            }
        }

        uploadImage.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }

        appViewModel!!.userdata.observe(this) { user ->
            if (user != null) {
                myUser = user
                getDataFromDatabase(user)
            } else {
                progressBar.visibility = View.GONE
                whiteLayout.visibility = View.GONE
            }
        }

        userNameLayout.setOnClickListener {
            showDialog("Name")
        }

        petNameLayout.setOnClickListener {
            showDialog("Pet Name")
        }

        signOutBtn.setOnClickListener {
            appViewModel!!.logOut()
            startActivity(Intent(this, AuthenticationActivity::class.java))
            finish()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun showDialog(str: String) {
        val dialog = Dialog(this@UserProfileActivity)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_edit_data)
        val dialogEditText: TextInputEditText = dialog.findViewById(R.id.editText_dialog)
        val dialogTitle: TextView = dialog.findViewById(R.id.dialog_title)
        val dialogDoneBtn: TextView = dialog.findViewById(R.id.dialog_done_btn)
        val dialogCancelBtn: TextView = dialog.findViewById(R.id.dialog_cancel_btn)
        val dialogIcon: ImageView = dialog.findViewById(R.id.dialog_icon)

        if (str == "Name") {
            dialogTitle.text = "Enter your name"
            dialogIcon.setImageResource(R.drawable.profile)
            dialogEditText.setText(profileName.text)
        } else {
            dialogTitle.text = "Enter your pet name"
            dialogIcon.setImageResource(R.drawable.pet_profile_icon)
            dialogEditText.setText(petName.text)
        }

        dialogDoneBtn.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            whiteLayout.visibility = View.VISIBLE
            uploadImage.isClickable = false
            userNameLayout.isClickable = false
            petNameLayout.isClickable = false
            if (dialogEditText.text.toString().isEmpty()) {
                Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show()
            } else {
                val editedName = dialogEditText.text.toString()

                dbViewModel!!.changeProfileData(myUser, str, editedName)
                dbViewModel!!.dbLiveData.observe(this@UserProfileActivity){
                    when(it){
                        is Response.Success -> {
                            getDataFromDatabase(myUser)
                            Toast.makeText(this@UserProfileActivity, "Your profile data updated successfully", Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                            whiteLayout.visibility = View.GONE
                        }
                        is Response.Failure -> {
                            Toast.makeText(this@UserProfileActivity, it.errorMassage, Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                            whiteLayout.visibility = View.GONE
                        }
                    }
                }
                dialog.hide()
            }
        }
        dialogCancelBtn.setOnClickListener {
            dialog.hide()
        }
        dialog.show()
    }


    private fun imageUploadToStorage(imageUri: Uri) {
        dbViewModel!!.uploadImageToStorage(imageUri, myUser)
        dbViewModel!!.dbLiveData.observe(this) {
            when (it) {
                is Response.Success -> {
                    progressBar.visibility = View.GONE
                    whiteLayout.visibility = View.GONE
                    getDataFromDatabase(myUser)
                    Toast.makeText(this, "Image changed successfully", Toast.LENGTH_SHORT).show()
                }
                is Response.Failure -> {
                    progressBar.visibility = View.GONE
                    whiteLayout.visibility = View.GONE
                    Toast.makeText(this, it.errorMassage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getDataFromDatabase(user: FirebaseUser) {
        dbViewModel!!.getProfileData(user)
        dbViewModel!!.profileData.observe(this) { dataList ->
            val userType = dataList[0]
            profileName.text = dataList[1]
            Glide.with(this).load(dataList[2]).into(profileImage)
            phoneNo.text = dataList[3]

            when (userType) {
                "Normal User" -> {
                    petNameLayout.visibility = View.VISIBLE
                    petName.text = dataList[4]
                }
                "Seller" -> {
                    tradeNoLayout.visibility = View.VISIBLE
                    tradeNo.text = dataList[4]
                }
                "Doctor" -> {
                    regNoLayout.visibility = View.VISIBLE
                    regNo.text = dataList[4]
                }
            }
            progressBar.visibility = View.GONE
            whiteLayout.visibility = View.GONE
            profileMainLayout.visibility = View.VISIBLE
            signOutBtn.visibility = View.VISIBLE
            uploadImage.isClickable = true
            userNameLayout.isClickable = true
            petNameLayout.isClickable = true
        }
    }
}