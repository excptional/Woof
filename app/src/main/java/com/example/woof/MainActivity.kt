package com.example.woof

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.woof.UserActivities.NormalUserActivity
import com.example.woof.repo.Response
import com.example.woof.repo.Usertype
import com.example.woof.viewmodel.AppViewModel
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private var appViewModel: AppViewModel? = null
    private var user: String? = null
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        val mainProgressBar: LottieAnimationView = findViewById(R.id.mainProgressbar)

        appViewModel!!.userdata.observe(this) {
            if (it == null) {
                startActivity(Intent(this, AuthenticationActivity::class.java))
            }else{
                appViewModel!!.getUserType(it)
                appViewModel!!.usertype.observe(this){
                    when(it){
                        is Usertype.Success -> {
                            when(it.str){
                                "Normal User" -> {startActivity(Intent(this@MainActivity, NormalUserActivity::class.java))}
                                "Seller" -> {}
                                "Doctor" -> {}
                            }
                        }
                        is Usertype.Failure -> Toast.makeText(this@MainActivity, it.str, Toast.LENGTH_LONG).show()
                    }
                    mainProgressBar.visibility = View.GONE
                }
            }
        }

    }
}