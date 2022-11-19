package com.example.woof.UserActivities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.woof.R
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel

class DoctorActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor)

        var count = 0
        val text: TextView = findViewById(R.id.text_doctor)
        val addBtn: Button = findViewById(R.id.addBtn_doctor)
        val dbViewModel: DBViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        addBtn.setOnClickListener{
            count++
            text.text = "$count"
            dbViewModel.addKennels(count)
        }
    }
}