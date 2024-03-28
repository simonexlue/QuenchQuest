package com.example.quenchquest;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    public static void showNotification(Context context) {
        // Build and show the notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "123")
                .setSmallIcon(R.drawable.waterglass)
                .setContentTitle("QuenchQuest Notification")
                .setContentText("Don't forget to stay hydrated!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
        notificationManager.notify(notificationId, builder.build());
        Log.d("Tag", "Notification showed");
    }
}
