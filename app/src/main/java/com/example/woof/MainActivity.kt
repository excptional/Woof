package com.example.woof

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

//    private var appViewModel: AppViewModel? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
//        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
//        val mainProgressBar: LottieAnimationView = findViewById(R.id.mainProgressbar)

//        appViewModel!!.userdata.observe(this) { user ->
//            if (user == null) {
//                startActivity(Intent(this, AuthenticationActivity::class.java))
//            } else {
//                appViewModel!!.getUserType(user)
//                appViewModel!!.usertype.observe(this) { usertype ->
//                    when (usertype) {
//                        "Normal User" -> {
//                            startActivity(
//                                Intent(
//                                    this@MainActivity,
//                                    NormalUserActivity::class.java
//                                )
//                            )
//                            mainProgressBar.visibility = View.GONE
//                        }
//                        "Seller" -> {
//                            startActivity(
//                                Intent(
//                                    this@MainActivity,
//                                    SellerActivity::class.java
//                                )
//                            )
//                            mainProgressBar.visibility = View.GONE
//                        }
//                        "Doctor" -> {
//                            startActivity(
//                                Intent(
//                                    this@MainActivity,
//                                    DoctorActivity::class.java
//                                )
//                            )
//                            mainProgressBar.visibility = View.GONE
//                        }
//                        "Nothing" -> {
//                            Toast.makeText(this@MainActivity, "loading...", Toast.LENGTH_LONG).show()
//                        }
//                        else -> {
//                            Toast.makeText(this@MainActivity, "Something wrong happened", Toast.LENGTH_LONG).show()
//                            mainProgressBar.visibility = View.GONE
//                        }
//                    }
//                }
//            }
//        }
    }
}