package com.example.woof.UserActivities.fragments

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.woof.R
import com.example.woof.ReminderReceiver
import com.example.woof.viewmodel.AppViewModel
import com.example.woof.viewmodel.DBViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class SetReminder : Fragment(), AdapterView.OnItemSelectedListener {

    private val typeList = mutableListOf(
        "Select reminder type", "Daily", "Coming soon"
    )

    private lateinit var appViewModel: AppViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var typeSpinner: Spinner
    private lateinit var setTimeLayout: RelativeLayout
    private lateinit var selectedTimeText: TextView
    private lateinit var setReminderBtn: CardView
    private lateinit var massageEditText: TextInputEditText
    private lateinit var alarmManager: AlarmManager
    private lateinit var pi: PendingIntent
    private lateinit var selectedType: String
    private var selectedDate: String = ""
    private var nID: Long = 0
    private lateinit var uid: String
    private val calender = Calendar.getInstance()
    private lateinit var timePicker: MaterialTimePicker

    @SuppressLint("MissingInflatedId", "UnspecifiedImmutableFlag")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_set_reminder, container, false)

        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        appViewModel.userdata.observe(viewLifecycleOwner) {
            uid = it!!.uid
        }

        typeSpinner = view.findViewById(R.id.reminderTypeSpinner_setReminder)
        setTimeLayout = view.findViewById(R.id.selectTimeLayout_setReminder)
        selectedTimeText = view.findViewById(R.id.selectedTimeText_setReminder)
        setReminderBtn = view.findViewById(R.id.setBtn_setReminder)
        massageEditText = view.findViewById(R.id.massage_setReminder)

        val aa = ArrayAdapter(
            requireActivity().applicationContext,
            android.R.layout.simple_spinner_item,
            typeList
        )
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        with(typeSpinner)
        {
            adapter = aa
            setSelection(0, true)
            onItemSelectedListener = this@SetReminder
            gravity = Gravity.CENTER
            setPopupBackgroundResource(R.color.white)
        }

        setTimeLayout.setOnClickListener {
            if(selectedType.isEmpty() || (selectedType == "Select reminder type") || (selectedType == "Coming soon")){
                Toast.makeText(requireContext(), "Select valid reminder type", Toast.LENGTH_SHORT).show()
            } else showTimePicker()
        }

        setReminderBtn.setOnClickListener {
            setReminder(it)
        }

        return view
    }

    @SuppressLint("SimpleDateFormat")
    private fun showTimePicker() {
        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Reminder Time")
            .build()
        timePicker.show(requireActivity().supportFragmentManager, "abcd")
        timePicker.addOnPositiveButtonClickListener {

            calender[Calendar.HOUR_OF_DAY] = timePicker.hour
            calender[Calendar.MINUTE] = timePicker.minute
            calender[Calendar.SECOND] = 0
            calender[Calendar.MILLISECOND] = 0

//            if(selectedType == "once") {
//                val dateFormat = SimpleDateFormat("dd MMM, yyyy")
//                selectedDate = dateFormat.format(Date(calender.timeInMillis))
//                Toast.makeText(requireContext(), selectedDate, Toast.LENGTH_SHORT).show()
//            }

            val sdf = SimpleDateFormat("HH:mm")
            selectedTimeText.text = sdf.format(Date(calender.timeInMillis))
        }

    }

    private fun setReminder(view: View) {
        var allRight = true
        val massage = massageEditText.text.toString()
        val selectedTime = selectedTimeText.text

        if (massage.isEmpty()) {
            massageEditText.error = "Write a massage for reminder"
            Toast.makeText(requireContext(), "Write a massage for reminder", Toast.LENGTH_SHORT)
                .show()
            allRight = false
        }

        if (selectedTime.isEmpty()) {
            Toast.makeText(requireContext(), "Select time first", Toast.LENGTH_SHORT).show()
            allRight = false
        }
        if (!allRight) {
            Toast.makeText(requireContext(), "Select valid details", Toast.LENGTH_SHORT).show()
        } else {
            createNotificationChannel(massage)
            setAlarm(selectedType, view)
            dbViewModel.setReminder(
                selectedType, selectedTimeText.text.toString(), calender.timeInMillis, selectedDate,
                massage, uid, nID
            )
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun setAlarm(str: String, view: View) {
        if(str == "daily") {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calender.timeInMillis, (24 * 3600000), pi
            )
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calender.timeInMillis, pi)
        }
        Toast.makeText(
            requireContext(),
            "Alarm set at ${selectedTimeText.text}",
            Toast.LENGTH_SHORT
        ).show()
        requireFragmentManager().popBackStack()
        Navigation.findNavController(view).navigate(R.id.nav_reminder)
    }

    private fun createNotificationChannel(msg: String) {
        alarmManager = (requireContext().getSystemService(Context.ALARM_SERVICE) as? AlarmManager)!!
        nID =  abs((0..999999999999).random())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(requireContext(), ReminderReceiver::class.java)
            intent.putExtra("massage", msg)
            intent.putExtra("broadcastId", nID.toInt())
            pi = PendingIntent.getBroadcast(
                requireContext(), nID.toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MyReminderNotification"
            val descriptionText = "Notification for reminder"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("woof407", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {
                selectedType = typeList[position]
                Toast.makeText(requireContext(), "Nothing selected!", Toast.LENGTH_SHORT).show()
            }
            1 -> {
                selectedType = typeList[position]
            }
            2 -> {
                selectedType = typeList[position]
                Toast.makeText(requireContext(), "Feature will be implemented soon", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(requireContext(), "Nothing selected!", Toast.LENGTH_SHORT).show()
    }

    private fun getMonth(m: Int): String {
        return when (m) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> {
                ""
            }
        }
    }

}