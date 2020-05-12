package com.example.mediaplayer;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.io.FileDescriptor;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private List<Song> audios;
    private final Iinterface mClickListener;
    private Context context;

    public RecyclerViewAdapter(List<Song> audios , Iinterface clickListener, Context context) {

        this.audios = audios;
        this.mClickListener = clickListener;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_item, parent,false);
        ViewHolder mViewHolder = new ViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        String name = audios.get(position).getTitle();
        if(name.length() > 23)
            holder.audioName.setText(name.substring(0,22)+"...");
        else
            holder.audioName.setText(name);

        Bitmap b = getAlbumart(audios.get(position).getAlbum_id());
        if(b!=null) holder.audioImage.setImageBitmap(b);

        holder.artist.setText(audios.get(position).getArtist());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.clickListen(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return audios.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder  {

        TextView audioName;
        ImageView audioImage;
        TextView artist;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            audioName = itemView.findViewById(R.id.audio_name);
            audioImage = itemView.findViewById(R.id.audio_image);
            artist = itemView.findViewById(R.id.artist);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public Bitmap getAlbumart(int album_id)
    {
        Bitmap bm = null;
        try
        {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = context.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null)
            {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        return bm;
    }
}