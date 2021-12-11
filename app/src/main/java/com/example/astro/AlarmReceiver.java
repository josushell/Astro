package com.example.astro;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String channel_name="noti_channel_name";
    private static final String channel_id="noti_channel_id";
    NotificationManager notimgr;
    NotificationCompat.Builder builder;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("alarmtest","AlarmReceiver onReceive()");

        Intent serviceIntent=new Intent(context,AstroAlarmService.class);

        serviceIntent.putExtra("title",intent.getStringExtra("title"));
        serviceIntent.putExtra("content",intent.getStringExtra("content"));

        context.startService(serviceIntent);
    }

}