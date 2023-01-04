package com.example.woof.UserActivities.adapters

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.woof.R
import com.example.woof.UserActivities.items.ReminderItems
import java.text.DecimalFormat
import java.text.NumberFormat

class ReminderAdapter(
    private val ReminderItems: ArrayList<ReminderItems>,
    private val context: Context
) :
    RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    val alarmManager = (context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reminder_items, parent, false)
        return ReminderViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val currentItem = ReminderItems[position]

        holder.time.text = currentItem.time
        holder.massage.text = currentItem.massage
        holder.status.text = currentItem.type

        holder.deleteBtn.setOnClickListener {
            Toast.makeText(context, "Feature will be implemented soon", Toast.LENGTH_SHORT).show()
        }

        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                Toast.makeText(context, "Feature will be implemented soon", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return ReminderItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateReminder(updateReminderItems: ArrayList<ReminderItems>) {
        ReminderItems.clear()
        ReminderItems.addAll(updateReminderItems)
        notifyDataSetChanged()
    }

    class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val status: TextView = itemView.findViewById(R.id.status_reminder)
        val time: TextView = itemView.findViewById(R.id.time_reminder)
        val massage: TextView = itemView.findViewById(R.id.massage_reminder)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteBtn_reminder)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val switch: Switch = itemView.findViewById(R.id.switch_reminder)
        val timer: TextView = itemView.findViewById(R.id.timer_reminder)
    }
}