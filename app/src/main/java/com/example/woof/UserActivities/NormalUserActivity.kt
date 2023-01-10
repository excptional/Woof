package com.example.woof.UserActivities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.woof.R
import com.example.woof.databinding.ActivityNormalUserBinding
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView

class NormalUserActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityNormalUserBinding
    private var dbViewModel: DBViewModel? = null
    private var appViewModel: AppViewModel? = null
    private lateinit var name: TextView
    private lateinit var img: CircleImageView
    private lateinit var viewProfile: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNormalUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appBarNormalUser.toolbar.title = ""
        setSupportActionBar(binding.appBarNormalUser.toolbar)

        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        val drawerLayout: DrawerLayout = binding.drawerLayoutNormalUser
        val navView: NavigationView = binding.navViewNormalUser
        val navController = findNavController(R.id.nav_host_fragment_content_normal_user)

        img = navView.getHeaderView(0).findViewById(R.id.profileImage)
        name = navView.getHeaderView(0).findViewById(R.id.profileName)
        viewProfile = navView.getHeaderView(0).findViewById(R.id.viewProfile)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_feed,
                R.id.nav_food_and_accessories,
                R.id.nav_training_and_grooming,
                R.id.nav_pet_shop_and_kennel,
                R.id.nav_hospitals_and_clinics,
                R.id.nav_medicines,
                R.id.nav_about_us,
                R.id.nav_contact_us,
                R.id.nav_future_scope
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        appViewModel!!.userdata.observe(this) { user ->
            if (user != null) {
                getDataFromDatabase(user)
            }
        }

        viewProfile.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

    }

    private fun getDataFromDatabase(user: FirebaseUser) {
        dbViewModel!!.getProfileData(user)
        dbViewModel!!.profileData.observe(this) { dataList ->
            name.text = dataList[1]
            Glide.with(this).load(dataList[2]).into(img)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_normal_user)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}