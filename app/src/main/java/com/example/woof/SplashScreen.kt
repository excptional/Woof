package com.example.woof

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.google.android.material.progressindicator.LinearProgressIndicator

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    var timerValue = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        val progressbar: LinearProgressIndicator = findViewById(R.id.progressbarSplashScreen)
        progressbar.progress = timerValue
        object : CountDownTimer(3000, 1000) {
            override fun onTick(progress: Long) {
                timerValue++
                progressbar.progress = timerValue*100/3
            }

            override fun onFinish() {
                progressbar.progress = 3000
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
            }
        }.start()
    }
}