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
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AstroAlarmService extends Service {
    private static final String channel_name="noti_channel_name";
    private static final String channel_id="noti_channel_id";
    NotificationManager notimgr;
    NotificationCompat.Builder builder;

    private String title;
    private String content;

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
        Log.d("alarmtest","AstroAlarmService onStartCommand()");

        title=intent.getStringExtra("title")+"  🪐";
        content=intent.getStringExtra("content")+"  🔭";

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

        stopSelf();
        return super.onStartCommand(intent, flags, startId);

    }

    private void buildMSG(PendingIntent pendingIntent, Intent intent){
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
    }

    @Override
    public void onDestroy() {
        Log.d("alarmtest","AstroAlarmService onDestroy()");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}