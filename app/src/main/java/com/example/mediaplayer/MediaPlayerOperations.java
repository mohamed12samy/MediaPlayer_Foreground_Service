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

import static com.example.mediaplayer.AudioPlayService.AudiosS;
import static com.example.mediaplayer.AudioPlayService.NOTIFICATION_ID;
import static com.example.mediaplayer.AudioPlayService.manager;
import static com.example.mediaplayer.AudioPlayService.notification;
import static com.example.mediaplayer.AudioPlayService.notificationLayoutExpanded;
import static com.example.mediaplayer.AudioPlayService.position;
import static com.example.mediaplayer.MainActivity.audioLisT;
import static com.example.mediaplayer.MainActivity.currentSongPosition;

public class MediaPlayerOperations extends Application {

    private static MediaPlayerOperations mMyApplication;
    public static MediaPlayer mp;
    private SharedPreferences sharedPreferences;

    private Iinterface main;

    public static synchronized MediaPlayerOperations getInstance() {
        return mMyApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMyApplication = this;
        this.main = new MainActivity();
        sharedPreferences = getSharedPreferences("my_music", Context.MODE_PRIVATE);

    }

    public void stop() {
        if (mp != null) {
            mp.pause();
            main.updateButtonUI(false);
        }
    }

    public void play_pause() {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.pause();
                notificationLayoutExpanded.setImageViewResource(R.id.play_pause_button, R.drawable.ic_play_arrow_white_24dp);
                manager.notify(NOTIFICATION_ID, notification);

                main.updateButtonUI(false);
            } else if (!mp.isPlaying()) {
                mp.start();
                notificationLayoutExpanded.setImageViewResource(R.id.play_pause_button, R.drawable.ic_pause_white_24dp);
                manager.notify(NOTIFICATION_ID, notification);

                main.updateButtonUI(true);

                if (!isServiceRunning()) {
                    Intent serviceIntent = new Intent(mMyApplication, AudioPlayService.class);
                    serviceIntent.putExtra("current", currentSongPosition);
                    serviceIntent.putParcelableArrayListExtra("audioss", /*audioList*/audioLisT);
                    //serviceIntent.putExtra("path", paths);
                    serviceIntent.putExtra("resume", true);
                    ContextCompat.startForegroundService(mMyApplication, serviceIntent);
                }
            }
        }
        if (!isServiceRunning() && mp == null) {
            Intent serviceIntent = new Intent(mMyApplication, AudioPlayService.class);
            serviceIntent.putExtra("current", currentSongPosition);
            serviceIntent.putParcelableArrayListExtra("audioss", /*audioList*/audioLisT);
            //serviceIntent.putExtra("path", paths);
            serviceIntent.putExtra("start", true);
            ContextCompat.startForegroundService(mMyApplication, serviceIntent);
        }
    }

    public void start(String path) {
        if (mp != null) {
            mp.stop();
            mp.release();
        }
        mp = MediaPlayer.create(mMyApplication, Uri.parse(/*hashMap.get(audios.get(position)))*/AudiosS.get(position).getPath()));
        mp.start();
        //String d = Duration();
        setProgress();
        onCompletion();
        main.updateTitlesUI(position, AudiosS.get(position).getDuration());
        main.updateButtonUI(true);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("song_name",/*audios.get(position)*/ AudiosS.get(position).getTitle()).commit();
        editor.putString("duration",Duration()).commit();



    }

    public void playNext() {
        main.updateButtonUI(false);
       /* notificationLayoutExpanded.setImageViewResource(R.id.play_pause_button, R.drawable.ic_play_arrow_white_24dp);
        manager.notify(NOTIFICATION_ID, notification);
*/
        if (/*audios*/ AudiosS != null && mp != null) {

            mp.stop();
            mp.release();

            if (position == /*audios*/AudiosS.size() - 1)
                position = -1;
            mp = MediaPlayer.create(mMyApplication, Uri.parse(AudiosS.get(++position).getPath()/*hashMap.get(audios.get(++position)))*/));
            mp.start();
            //String d = Duration();

            setProgress();
            onCompletion();

            notificationLayoutExpanded.setImageViewResource(R.id.play_pause_button, R.drawable.ic_pause_white_24dp);
            notificationLayoutExpanded.setTextViewText(R.id.song_name, AudiosS.get(position).getTitle()/*audios.get(position)*/);
            manager.notify(NOTIFICATION_ID, notification);

            main.updateTitlesUI(position, AudiosS.get(position).getDuration());
            main.updateButtonUI(true);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("song_name", /*audios*/AudiosS.get(position).getTitle()).commit();
            editor.putString("duration",Duration()).commit();

            if (!isServiceRunning()) {
                Intent serviceIntent = new Intent(mMyApplication, AudioPlayService.class);
                serviceIntent.putExtra("current", currentSongPosition);
                serviceIntent.putParcelableArrayListExtra("audioss", /*audioList*/audioLisT);
                serviceIntent.putExtra("resume", true);
                ContextCompat.startForegroundService(mMyApplication, serviceIntent);
            }
        }



        if (!isServiceRunning() && mp == null && currentSongPosition < /*audioList*/audioLisT.size() - 1) {
            Intent serviceIntent = new Intent(mMyApplication, AudioPlayService.class);
            serviceIntent.putExtra("current", ++currentSongPosition);
            serviceIntent.putParcelableArrayListExtra("audioss", /*audioList*/audioLisT);
            serviceIntent.putExtra("start", true);
            ContextCompat.startForegroundService(mMyApplication, serviceIntent);
        } else return;
    }

    public void playPrevious() {
        main.updateButtonUI(false);
        if (/*audios*/AudiosS != null && mp != null) {

            mp.stop();
            mp.release();

            if (position == 0)
                position = /*audioList*/audioLisT.size();
            mp = MediaPlayer.create(mMyApplication, Uri.parse(AudiosS.get(--position).getPath()/*hashMap.get(audios.get(--position)))*/));
            mp.start();
            //String d = Duration();

            setProgress();
            onCompletion();

            notificationLayoutExpanded.setImageViewResource(R.id.play_pause_button, R.drawable.ic_pause_white_24dp);
            notificationLayoutExpanded.setTextViewText(R.id.song_name, /*audios*/AudiosS.get(position).getTitle());
            manager.notify(NOTIFICATION_ID, notification);

            main.updateTitlesUI(position, AudiosS.get(position).getDuration());
            main.updateButtonUI(true);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("song_name", /*audios*/AudiosS.get(position).getTitle()).commit();
            editor.putString("duration",Duration()).commit();


            if (!isServiceRunning()) {
                Intent serviceIntent = new Intent(mMyApplication, AudioPlayService.class);
                serviceIntent.putExtra("current", currentSongPosition);
                serviceIntent.putParcelableArrayListExtra("audioss", /*audioList*/audioLisT);
                serviceIntent.putExtra("resume", true);
                ContextCompat.startForegroundService(mMyApplication, serviceIntent);
            }
        } else {
            main.updateButtonUI(true);
        }

        if (!isServiceRunning() && mp == null && currentSongPosition > 0) {
            Intent serviceIntent = new Intent(mMyApplication, AudioPlayService.class);
            serviceIntent.putExtra("current", --currentSongPosition);
            serviceIntent.putParcelableArrayListExtra("audioss", /*audioList*/audioLisT);
            serviceIntent.putExtra("start", true);
            ContextCompat.startForegroundService(mMyApplication, serviceIntent);
        }
    }

    public void seek(int mesc) {
        if (mp != null) {
            mp.seekTo((mesc * 1000));
        }
    }

    private String Duration() {
        int minutes = ((mp.getDuration() % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (((mp.getDuration() % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        String d = minutes + ":" + seconds;
        return d;
    }

    private void onCompletion() {
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                notificationLayoutExpanded.setImageViewResource(R.id.play_pause_button, R.drawable.ic_play_arrow_white_24dp);
                manager.notify(NOTIFICATION_ID, notification);
                main.updateButtonUI(false);
                playNext();
            }
        });
    }

    private void setProgress() {
        main.setMax((mp.getDuration() / 1000));
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mp != null)
                    try {
                        int x = mp.getCurrentPosition();

                        main.setProgres(x / 1000);
                        main.updateDuration(x,position);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("progress",x / 1000).commit();

                    } catch (Exception e) {
                    }
            }
        }, 0, 1000);

    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (AudioPlayService.class.getName().equals(service.service.getClassName())) {
                Log.d("asasas", AudioPlayService.class.getName());
                return true;
            }
        }
        return false;
    }

}
