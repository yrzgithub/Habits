package com.example.habits;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    Notification notification;
    NotificationManagerCompat managerCompat;

    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    public void onReceive(Context context, Intent intent) {

        managerCompat = NotificationManagerCompat.from(context);

        Intent intent_main_activity = new Intent(context,MainActivity.class);
        intent_main_activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent_main_activity, 0);

        notification = new NotificationCompat.Builder(context,"Habits")
                .setContentTitle("Habits")
                .setContentText("What about today?")
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        managerCompat.notify(1,notification);

    }
}
