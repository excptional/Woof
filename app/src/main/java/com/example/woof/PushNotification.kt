package com.example.woof

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder

class PushNotification(val activity: Activity) {

    val CHANNEL_ID = "channelID"
    val CHANNEL_NAME = "channelName"
    val NOTIF_ID = 0


    fun sendNotification(titleMsg: String, textMsg: String){
        val intent= Intent(activity.applicationContext, activity::class.java)
        val pendingIntent = TaskStackBuilder.create(activity.applicationContext).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        createNotifyChannel()

        val notification = NotificationCompat.Builder(activity.applicationContext, CHANNEL_ID)
            .setContentTitle(titleMsg)
            .setContentText(textMsg)
            .setSmallIcon(R.drawable.wooficonpng)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val notifyManger = NotificationManagerCompat.from(activity.applicationContext)

        notifyManger.notify(NOTIF_ID, notification)
    }

    private fun createNotifyChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }
            val manager = activity.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

}