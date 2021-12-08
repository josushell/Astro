package com.example.astro;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class AstroAlarmService extends Service {
    private static final String channel_name="noti_channel_name";
    private static final String channel_id="noti_channel_id";
    NotificationManager notimgr;
    NotificationCompat.Builder builder;

    @Override
    public void onCreate() {
        AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        builder=null;
        notimgr=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public AstroAlarmService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            // API 26 이상에서의 channel 지정
            notimgr.createNotificationChannel(
                    new NotificationChannel(channel_id,channel_name,NotificationManager.IMPORTANCE_DEFAULT));
            builder=new NotificationCompat.Builder(this,channel_id);
        }
        else{
            // under 26
            builder=new NotificationCompat.Builder(this);
        }

        Intent newIntent=new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,101,newIntent,
                PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);

        buildMSG(pendingIntent,intent);

        Notification noti= builder.build();
        notimgr.notify(1,noti);
        return super.onStartCommand(intent, flags, startId);
    }

    private void buildMSG(PendingIntent pendingIntent, Intent intent){
        String notiTitle=intent.getStringExtra("title")+"  🪐";
        String noticontent=intent.getStringExtra("content")+"  🔭";

        builder.setContentTitle(notiTitle);
        builder.setContentText(noticontent);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}