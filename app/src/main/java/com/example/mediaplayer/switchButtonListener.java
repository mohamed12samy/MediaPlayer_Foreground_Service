package com.example.mediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import static com.example.mediaplayer.AudioPlayService.NOTIFICATION_ID;
import static com.example.mediaplayer.AudioPlayService.audios;
import static com.example.mediaplayer.AudioPlayService.hashMap;
import static com.example.mediaplayer.AudioPlayService.manager;
import static com.example.mediaplayer.AudioPlayService.notification;
import static com.example.mediaplayer.AudioPlayService.notificationLayoutExpanded;
import static com.example.mediaplayer.AudioPlayService.position;

public class switchButtonListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getStringExtra("action");

        if (action.equals("CLOSE")) {
            Log.d("Here", "I am here  " + action);
            context.stopService(new Intent(context, AudioPlayService.class));
            MediaPlayerOperations.getInstance().stop();
        }
        else if (action.equals("PLAY_PAUSE")) {
            Log.d("Here", "I am here  " + action);
            MediaPlayerOperations.getInstance().play_pause();

        }
        else if (action.equals("PREV")) {
            Log.d("Here", "I am here  " + action + "  " + position);
            MediaPlayerOperations.getInstance().playPrevious();

        }
        else if (action.equals("NEXT")) {
            Log.d("Here", "I am here  " + action + "  " + position);
            MediaPlayerOperations.getInstance().playNext();
           }
    }
}
