package com.example.mediaplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private List<String> audios;
    private final Iinterface mClickListener;

    public RecyclerViewAdapter(List<String> audios , Iinterface clickListener) {

        this.audios = audios;
        this.mClickListener = clickListener;
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

        holder.audioName.setText(audios.get(position));

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

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            audioName = itemView.findViewById(R.id.audio_name);

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
}