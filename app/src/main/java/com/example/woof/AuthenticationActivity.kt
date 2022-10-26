package com.example.woof

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.woof.fragments.SignIn
import com.example.woof.fragments.SignUp
import com.example.woof.repo.Usertype
import com.example.woof.viewmodel.AppViewModel
import com.google.firebase.auth.FirebaseUser

class AuthenticationActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        this.supportFragmentManager.beginTransaction().replace(R.id.authFrameLayout, SignIn()).commit()
    }
}