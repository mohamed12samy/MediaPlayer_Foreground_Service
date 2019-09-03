package com.example.mediaplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.HashMap;


public class AudioPlayService extends Service {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final int NOTIFICATION_ID = 1;
    static RemoteViews notificationLayoutExpanded;
    static RemoteViews notificationLayoutCollapsed;
    static NotificationManager manager;
    static Notification notification;
    static int position;
    static ArrayList<String> audios;
    static HashMap<String, String> hashMap;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_layout);
        notificationLayoutCollapsed = new RemoteViews(getPackageName(), R.layout.notification_mini_layout);

        position = intent.getIntExtra("current", 0);
        hashMap = (HashMap<String, String>) intent.getSerializableExtra("path");
        audios = intent.getStringArrayListExtra("audios");


        if (!intent.getBooleanExtra("resume", false))
            MediaPlayerOperations.getInstance().start("");

        notificationLayoutExpanded.setImageViewResource(R.id.play_pause_button, R.drawable.ic_pause_white_24dp);


        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);


        Intent close = new Intent(this, switchButtonListener.class);
        close.setAction("mediaplayeraction");
        close.putExtra("action", "CLOSE");
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 0, close, 0);
        notificationLayoutExpanded.setOnClickPendingIntent(R.id.close_button, closePendingIntent);
        notificationLayoutCollapsed.setOnClickPendingIntent(R.id.close_button, closePendingIntent);

        Intent pause = new Intent(this, switchButtonListener.class);
        pause.setAction("mediaplayeraction");
        pause.putExtra("action", "PLAY_PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(this, 1, pause, 0);
        notificationLayoutExpanded.setOnClickPendingIntent(R.id.play_pause_button, pausePendingIntent);

        Intent prev = new Intent(this, switchButtonListener.class);
        prev.setAction("mediaplayeraction");
        prev.putExtra("action", "PREV");
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 2, prev, 0);
        notificationLayoutExpanded.setOnClickPendingIntent(R.id.prev_button, prevPendingIntent);

        Intent next = new Intent(this, switchButtonListener.class);
        next.setAction("mediaplayeraction");
        next.putExtra("action", "NEXT");
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 3, next, 0);
        notificationLayoutExpanded.setOnClickPendingIntent(R.id.next_button, nextPendingIntent);

        notificationLayoutExpanded.setTextViewText(R.id.song_name, audios.get(position));
        notificationLayoutCollapsed.setTextViewText(R.id.song_name, audios.get(position));


        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                .setContentIntent(pendingIntent)
                .setCustomContentView(notificationLayoutCollapsed)
                .setCustomBigContentView(notificationLayoutExpanded)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );

            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
