package com.example.woof

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.woof.AuthFiles.AuthenticationActivity
import com.example.woof.UserActivities.DoctorActivity
import com.example.woof.UserActivities.NormalUser.NormalUserActivity
import com.example.woof.UserActivities.SellerActivity
import com.example.woof.viewmodel.AppViewModel

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private var appViewModel: AppViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]

        appViewModel!!.userdata.observe(this) { user ->
            if (user == null) {
                Handler().postDelayed({
                    startActivity(Intent(this, AuthenticationActivity::class.java))
                }, 2000)

            } else {
                appViewModel!!.getUserType(user)
                appViewModel!!.usertype.observe(this) { usertype ->
                    when (usertype) {
                        "Normal User" -> {
                            startActivity(
                                Intent(
                                    this@SplashScreen,
                                    NormalUserActivity::class.java
                                )
                            )
                        }
                        "Seller" -> {
                            startActivity(
                                Intent(
                                    this@SplashScreen,
                                    SellerActivity::class.java
                                )
                            )
                        }
                        "Doctor" -> {
                            startActivity(
                                Intent(
                                    this@SplashScreen,
                                    DoctorActivity::class.java
                                )
                            )
                        }
                        "Nothing" -> {
                            Toast.makeText(this@SplashScreen, "starting...", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this@SplashScreen, "Something wrong happened", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}