package com.example.mediaplayer;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.mediaplayer.AudioPlayService.NOTIFICATION_ID;
import static com.example.mediaplayer.AudioPlayService.audios;
import static com.example.mediaplayer.AudioPlayService.hashMap;
import static com.example.mediaplayer.AudioPlayService.manager;
import static com.example.mediaplayer.AudioPlayService.notification;
import static com.example.mediaplayer.AudioPlayService.notificationLayoutExpanded;
import static com.example.mediaplayer.AudioPlayService.position;
import static com.example.mediaplayer.MainActivity.audioList;
import static com.example.mediaplayer.MainActivity.currentSongPosition;
import static com.example.mediaplayer.MainActivity.paths;

public class MediaPlayerOperations extends Application {

    private static MediaPlayerOperations mMyApplication;
    private static MediaPlayer mp;
    private SharedPreferences sharedPreferences;
    public static synchronized MediaPlayerOperations getInstance(){
        return mMyApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMyApplication = this;
        sharedPreferences = getSharedPreferences("my_music", Context.MODE_PRIVATE);

    }

    public void stop(){
        if(mp!=null) {
            mp.pause();
            MainActivity.updateButtonUI(false);
        }
    }

    public void play_pause(){
        if(mp!=null) {
            if (mp.isPlaying()) {
                mp.pause();
                notificationLayoutExpanded.setImageViewResource(R.id.play_pause_button, R.drawable.ic_play_arrow_white_24dp);
                manager.notify(NOTIFICATION_ID, notification);

                MainActivity.updateButtonUI(false);
            } else if (!mp.isPlaying()) {
                mp.start();
                notificationLayoutExpanded.setImageViewResource(R.id.play_pause_button, R.drawable.ic_pause_white_24dp);
                manager.notify(NOTIFICATION_ID, notification);

                MainActivity.updateButtonUI(true);

                if(!isServiceRunning()){
                    Log.d("rtrtrt","yyyy");
                    Intent serviceIntent = new Intent(mMyApplication, AudioPlayService.class);
                    serviceIntent.putExtra("current", currentSongPosition);
                    serviceIntent.putExtra("audios", audioList);
                    serviceIntent.putExtra("path", paths);
                    serviceIntent.putExtra("resume", true);
                    ContextCompat.startForegroundService(mMyApplication, serviceIntent);
                }
            }
        }
        if(!isServiceRunning() && mp == null){
            Log.d("rtrtrt","yyyy");
            Intent serviceIntent = new Intent(mMyApplication, AudioPlayService.class);
            serviceIntent.putExtra("current", currentSongPosition);
            serviceIntent.putExtra("audios", audioList);
            serviceIntent.putExtra("path", paths);
            serviceIntent.putExtra("start", true);
            ContextCompat.startForegroundService(mMyApplication, serviceIntent);
        }
    }


    public void start(String path){
        if(mp!=null){
            mp.stop();
            mp.release();
        }
        mp = MediaPlayer.create(mMyApplication, Uri.parse(hashMap.get(audios.get(position))));
        mp.start();
        String d = Duration();
        MainActivity.updateTitlesUI(position,d);
        MainActivity.updateButtonUI(true);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("song_name",audios.get(position)).commit();
    }

    public void playNext(){
        if (audios != null && position < audios.size() - 1) {

            if(mp!=null) {
                mp.stop();
                mp.release();

                mp = MediaPlayer.create(mMyApplication, Uri.parse(hashMap.get(audios.get(++position))));
                mp.start();
                String d = Duration();
                notificationLayoutExpanded.setImageViewResource(R.id.play_pause_button, R.drawable.ic_pause_white_24dp);
                notificationLayoutExpanded.setTextViewText(R.id.song_name, audios.get(position));
                manager.notify(NOTIFICATION_ID, notification);

                MainActivity.updateTitlesUI(position,d);
                MainActivity.updateButtonUI(true);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("song_name",audios.get(position)).commit();

                if(!isServiceRunning()){
                    Log.d("rtrtrt","yyyy");
                    Intent serviceIntent = new Intent(mMyApplication, AudioPlayService.class);
                    serviceIntent.putExtra("current", currentSongPosition);
                    serviceIntent.putExtra("audios", audioList);
                    serviceIntent.putExtra("path", paths);
                    serviceIntent.putExtra("resume", true);
                    ContextCompat.startForegroundService(mMyApplication, serviceIntent);
                }
            }
        }
        if(!isServiceRunning() && mp == null && currentSongPosition < audioList.size()-1){
            Log.d("rtrtrt","yyyy");
            Intent serviceIntent = new Intent(mMyApplication, AudioPlayService.class);
            serviceIntent.putExtra("current", ++currentSongPosition);
            serviceIntent.putExtra("audios", audioList);
            serviceIntent.putExtra("path", paths);
            serviceIntent.putExtra("start", true);
            ContextCompat.startForegroundService(mMyApplication, serviceIntent);
        }
        else return;
    }

    public void playPrevious(){
        if (audios != null && position > 0) {
            if(mp != null) {
                mp.stop();
                mp.release();

                mp = MediaPlayer.create(mMyApplication, Uri.parse(hashMap.get(audios.get(--position))));
                mp.start();
                String d = Duration();
                notificationLayoutExpanded.setImageViewResource(R.id.play_pause_button, R.drawable.ic_pause_white_24dp);
                notificationLayoutExpanded.setTextViewText(R.id.song_name, audios.get(position));
                manager.notify(NOTIFICATION_ID, notification);

                MainActivity.updateTitlesUI(position,d);
                MainActivity.updateButtonUI(true);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("song_name",audios.get(position)).commit();

                if(!isServiceRunning()){
                    Log.d("rtrtrt","yyyy");
                    Intent serviceIntent = new Intent(mMyApplication, AudioPlayService.class);
                    serviceIntent.putExtra("current", currentSongPosition);
                    serviceIntent.putExtra("audios", audioList);
                    serviceIntent.putExtra("path", paths);
                    serviceIntent.putExtra("resume", true);
                    ContextCompat.startForegroundService(mMyApplication, serviceIntent);
                }
            }else {
                MainActivity.updateButtonUI(true);
            }
            }
        if(!isServiceRunning() && mp == null && currentSongPosition>0){
            Log.d("rtrtrt","yyyy");
            Intent serviceIntent = new Intent(mMyApplication, AudioPlayService.class);
            serviceIntent.putExtra("current", --currentSongPosition);
            serviceIntent.putExtra("audios", audioList);
            serviceIntent.putExtra("path", paths);
            serviceIntent.putExtra("start", true);
            ContextCompat.startForegroundService(mMyApplication, serviceIntent);
        }
    }

    public void seek(int mesc){
        if(mp!=null ) {
            mp.seekTo((mesc*1000));
        }
    }
    private String Duration(){
        MainActivity.setMax((mp.getDuration()/1000));
        int minutes = ((mp.getDuration() % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (((mp.getDuration() % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        String d = minutes+":"+seconds;

        setProgress();
        onCompletion();
        Log.d("TIME",d);
        return d;
    }

    public void onCompletion(){
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                notificationLayoutExpanded.setImageViewResource(R.id.play_pause_button, R.drawable.ic_play_arrow_white_24dp);
                MainActivity.updateButtonUI(false);
                playNext();
            }
        });
    }
    public void setProgress(){

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mp != null)
                    try {
                        MainActivity.setProgres(mp.getCurrentPosition() / 1000);
                    }catch (Exception e){
                    }
            }
        }, 0, 500);

    }
    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(AudioPlayService.class.getName().equals(service.service.getClassName())) {
                Log.d("asasas",AudioPlayService.class.getName());
                return true;
            }
        }
        return false;
    }

}
