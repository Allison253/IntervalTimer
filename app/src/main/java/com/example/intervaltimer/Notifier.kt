/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.intervaltimer

import android.app.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock.elapsedRealtime
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import java.util.jar.Manifest

/**
 * Utility class for posting notifications.
 * This class creates the notification channel (as necessary) and posts to it when requested.
 */
private const val n="myNotifier"
object Notifier {

    private const val channelId = "Default"

    fun init(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            val notificationManager =
                activity.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            val existingChannel = notificationManager.getNotificationChannel(channelId)
            if (existingChannel == null) {
                ActivityCompat.requestPermissions(activity,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)

                // Create the NotificationChannel
                val name = activity.getString(R.string.defaultChannel)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val mChannel = NotificationChannel(channelId, name, importance).apply{
                    description = activity.getString(R.string.notificationDescription)
                    enableVibration(false)
                    setShowBadge(false)
                }
                notificationManager.createNotificationChannel(mChannel)
            }
        }
    }

    fun postNotification(id: Int, context: Context, intent: PendingIntent, myBase: Long) {
        val builder = NotificationCompat.Builder(context, channelId)
        builder.setContentTitle("Interval Timer")
            .setSmallIcon(R.drawable.timer_icon)
            //.setTicker("Ticker Text")
        val text = context.getString(R.string.timerRunning)
        val notification = builder.setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setOngoing(false)
            //.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
            .setContentIntent(intent)
            .setWhen(System.currentTimeMillis()-(elapsedRealtime() - myBase))  // the time stamp, you will probably use System.currentTimeMillis() for most scenarios
            .setUsesChronometer(true)
            .build()
        val notificationManager = NotificationManagerCompat.from(context)
        // Remove prior notifications; only allow one at a time to edit the latest item
        notificationManager.cancelAll()
        //notification.flags= Notification.FLAG_ONGOING_EVENT, commented this out because if this exists, the notification will not go away on click
        notificationManager.notify(id, notification)
    }

    fun cancelNotification(context:Context){
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancelAll()
    }



}

