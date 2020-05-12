package com.example.mediaplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;

public class MainActivity extends AppCompatActivity implements Iinterface {

    static ArrayList<Song> audioLisT = new ArrayList<>();

    static int currentSongPosition = 0;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter = new RecyclerViewAdapter(audioLisT, this, this);

    static TextView title_expanded;
    static TextView title_top;
    static TextView title_collapsed;
    static TextView duration;

    static ImageButton play_pause_collapsed;
    static ImageButton play_pause;
    ImageButton prev;
    ImageButton next;

    private static SeekBar mSeekBar;
    private static SeekBar mSeekBar_volume;
    private BottomSheetBehavior mBottomSheetBehavior;

    SharedPreferences sharedPreferences;
    AudioManager audioManager ;
    SettingsContentObserver mSettingsContentObserver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title_collapsed = findViewById(R.id.audio_name);
        title_expanded = findViewById(R.id.audio_name_bottom_expanded);
        title_top = findViewById(R.id.song_name);
        duration = findViewById(R.id.Duration);

        play_pause_collapsed = findViewById(R.id.play_pause_button_collapsed);
        play_pause = findViewById(R.id.play_pause_button_expanded);
        prev = findViewById(R.id.previous);
        next = findViewById(R.id.next);

        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        mSeekBar = findViewById(R.id.seekBar);
        mSeekBar_volume = findViewById(R.id.seekBar_volume);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mSeekBar_volume.setMax(maxVolume);
        mSeekBar_volume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //GETTING THE PLAYLIST FROM DEVICE
        audioLisT.clear();
        getPlayList();
        bottomSheet();

        if(isServiceRunning() && MediaPlayerOperations.getInstance().mp.isPlaying() ){
            updateButtonUI(true);
        }



        play_pause_collapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayerOperations.getInstance().play_pause();
            }
        });
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayerOperations.getInstance().play_pause();
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayerOperations.getInstance().playPrevious();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayerOperations.getInstance().playNext();            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b)
                    MediaPlayerOperations.getInstance().seek(i);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekBar_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void fun_fun(){
        sharedPreferences = getSharedPreferences("my_music", Context.MODE_PRIVATE);
        final String song_name;
        String duration;
        if(sharedPreferences.contains("song_name") && sharedPreferences.getString("song_name","") != null)
        {
            song_name = sharedPreferences.getString("song_name","");
            duration = sharedPreferences.getString("duration","");

            mSeekBar.setMax(sharedPreferences.getInt("max",0));
            mSeekBar.setProgress(sharedPreferences.getInt("progress",0));

            currentSongPosition = index_of_song(song_name);
            updateTitlesUI(currentSongPosition,duration);
        }else{
            currentSongPosition = 0;
            updateTitlesUI(currentSongPosition,"00:00");
        }

    }

    private int index_of_song(String title){
        for(int i=0; i<audioLisT.size(); i++){
            if(audioLisT.get(i).getTitle().equals(title))
                return i;
        }
        return -1;
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
    public void startService() {
        Intent serviceIntent = new Intent(this, AudioPlayService.class);
        serviceIntent.putExtra("current", currentSongPosition);
        //serviceIntent.putExtra("audios", audioList);
        serviceIntent.putParcelableArrayListExtra("audioss",audioLisT);
        //serviceIntent.putExtra("path", paths);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    @Override
    public void clickListen(int position) {
        currentSongPosition = position;
        startService();

        String title =  audioLisT.get(position).getTitle();/*audioList.get(position);*/
        title_top.setText(title);
        title_collapsed.setText(title);
        title_expanded.setText(title);

    }

    private void getPlayList() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                return;
            }
        }
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri videoUri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        //iterate over results if valid
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int data = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int artist_id = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
            int album_id_column = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

            do {
                String thisTitle = musicCursor.getString(titleColumn);
                String thisdata = musicCursor.getString(data);
                String thisduration = musicCursor.getString(durationColumn);
                String artist = musicCursor.getString(artistColumn);
                String artistID = musicCursor.getString(artist_id);
                String albumID = musicCursor.getString(album_id_column);

                audioLisT.add(new Song(thisTitle,thisdata, thisduration,Integer.parseInt(albumID), artist ));
            }
            while (musicCursor.moveToNext());

            recyclerView.setAdapter(adapter);
            fun_fun();
        }

    }

    private void bottomSheet() {

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        findViewById(R.id.collapsed_layout).setVisibility(View.VISIBLE);
                        findViewById(R.id.expanded_layout).setVisibility(View.GONE);
                        findViewById(R.id.layput101).setVisibility(View.VISIBLE);
                        findViewById(R.id.relayout).setBackgroundColor(getResources().getColor(R.color.offwhite));
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        findViewById(R.id.collapsed_layout).setVisibility(View.GONE);
                        findViewById(R.id.expanded_layout).setVisibility(View.VISIBLE);
                        findViewById(R.id.layput101).setVisibility(View.GONE);
                        findViewById(R.id.relayout).setBackgroundColor(getResources().getColor(R.color.white));
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        findViewById(R.id.collapsed_layout).setVisibility(View.GONE);
                        findViewById(R.id.expanded_layout).setVisibility(View.VISIBLE);
                        findViewById(R.id.layput101).setVisibility(View.GONE);
                        findViewById(R.id.relayout).setBackgroundColor(getResources().getColor(R.color.white));
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        findViewById(R.id.collapsed_layout).setVisibility(View.GONE);
                        findViewById(R.id.expanded_layout).setVisibility(View.VISIBLE);
                        findViewById(R.id.layput101).setVisibility(View.GONE);
                        findViewById(R.id.relayout).setBackgroundColor(getResources().getColor(R.color.white));
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        mBottomSheetBehavior.setState(STATE_COLLAPSED);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
            }
        });
    }

    @Override
    public void updateTitlesUI(int position,String Duration) {
        String Title = audioLisT.get(position).getTitle();/*audioList.get(position)*/
        title_top.setText(Title);
        title_collapsed.setText(Title);
        duration.setText(Duration);
        title_expanded.setText(Title);
    }

    @Override
    public void updateButtonUI(boolean playing) {
        if(playing){
            play_pause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
            play_pause_collapsed.setImageResource(R.drawable.ic_pause_black2_24dp);
        }
        else{
            play_pause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
            play_pause_collapsed.setImageResource(R.drawable.ic_play_arrow_black2_24dp);
        }
    }
    @Override
    public void setMax(int m){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("max",m).commit();

        mSeekBar.setMax(m);
    }
    @Override
    public void setProgres(int progres){
        mSeekBar.setProgress(progres);
    }

    @Override
    public void updateDuration(int progress, int position){
        String d = audioLisT.get(position).getDuration();
        progress = Integer.parseInt(d) - progress;
        int minutes = ((progress % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (((progress % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        duration.setText(minutes+":"+seconds);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSettingsContentObserver = new SettingsContentObserver( new Handler() );
        this.getApplicationContext().getContentResolver().registerContentObserver(
                android.provider.Settings.System.CONTENT_URI, true,
                mSettingsContentObserver );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.getApplicationContext().getContentResolver().unregisterContentObserver(mSettingsContentObserver);
    }

    public class SettingsContentObserver extends ContentObserver {

        public SettingsContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.v("ASDR", "Settings change detected");
            mSeekBar_volume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            getPlayList();
                        }
                    }
                }
                break;
        }
    }
}
