package com.example.woof.AuthFiles

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.woof.AuthFiles.fragments.SignIn
import com.example.woof.R

class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        this.supportFragmentManager.beginTransaction().replace(R.id.authFrameLayout, SignIn()).commit()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentById(R.id.authFrameLayout) == SignIn()) {
            finishAffinity()
            finish()
        }
    }
}