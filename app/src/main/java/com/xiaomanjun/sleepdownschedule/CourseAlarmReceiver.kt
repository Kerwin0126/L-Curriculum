package com.xiaomanjun.sleepdownschedule

import com.xiaomanjun.sleepdownschedule.feature.reminder.NotificationScheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class CourseAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationScheduler.withShortWakeLock(context, "course_alarm") {
            NotificationScheduler.createChannel(context)
            if (!NotificationScheduler.canPostNotifications(context)) {
                return@withShortWakeLock
            }
            val name = intent.getStringExtra("courseName") ?: "课程"
            val location = intent.getStringExtra("location").orEmpty()
            val notification = NotificationCompat.Builder(context, NotificationScheduler.channelId())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("快上课了：$name")
                .setContentText(if (location.isBlank()) "请准备上课" else "地点：$location")
                .setAutoCancel(true)
                .build()
            val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
            try {
                NotificationManagerCompat.from(context).notify(notificationId, notification)
            } catch (securityException: SecurityException) {
                Log.w("SleepDownCourseAlarm", "skip course notification: permission revoked", securityException)
            }
        }
    }
}
