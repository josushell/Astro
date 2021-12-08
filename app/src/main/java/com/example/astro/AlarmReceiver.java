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

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String channel_name="noti_channel_name";
    private static final String channel_id="noti_channel_id";
    NotificationManager notimgr;
    NotificationCompat.Builder builder;


    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        builder=null;
        notimgr=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            // API 26 이상에서의 channel 지정
            notimgr.createNotificationChannel(
                    new NotificationChannel(channel_id,channel_name,NotificationManager.IMPORTANCE_DEFAULT));
            builder=new NotificationCompat.Builder(context,channel_id);
        }
        else{
            // under 26
            builder=new NotificationCompat.Builder(context);
        }

        Intent newIntent=new Intent(context,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,101,newIntent,
                PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);

        Notification noti= builder.build();
        notimgr.notify(1,noti);
    }
}