package com.example.quenchquest;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

public class NotificationScheduler {
    public static void scheduleNotification(Context context) {
        createNotificationChannel(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        long intervalMillis = 15 * 60 * 1000; // 15 minutes in milliseconds
        long initialDelayMillis = SystemClock.elapsedRealtime() + intervalMillis; // Schedule the first alarm after 15 minutes
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, initialDelayMillis, intervalMillis, pendingIntent);
        Log.d("Tag", "Notification scheduled");
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "QuenchQuest";
            String description = "Water Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("123", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
