package com.example.woof

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import java.util.*

class ReminderReceiver : BroadcastReceiver() {

    private lateinit var mp: MediaPlayer

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {

        Toast.makeText(context, "Alarm working", Toast.LENGTH_SHORT).show()
        mp = MediaPlayer.create(context, R.raw.notification)
        mp.start()

        val massage: String = intent!!.getStringExtra("massage").toString()
        val id: Int = intent.getIntExtra("broadcastID", 0)

//        val drawable: Drawable? =
//            ResourcesCompat.getDrawable(Resources.getSystem(), R.drawable.bell, null)
//        val bmp: BitmapDrawable = drawable as BitmapDrawable
        val bmp = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.bell)

        val builder = NotificationCompat.Builder(context!!.applicationContext, "woof407")
            .setSmallIcon(R.drawable.wooficonpng)
            .setLargeIcon(bmp)
            .setContentTitle("Important reminder for your pet")
            .setContentText(massage)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(massage)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(context)) {
            notify(id, builder.build())
        }
    }
}
