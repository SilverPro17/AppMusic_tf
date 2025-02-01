package com.example.m_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;


public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyHolder> {

    private Context mContext;
    static ArrayList<MusicFiles> albumFiles;
    View view;
    static byte[] passAlbumImage;
    static boolean checkAlbumPass;

    public AlbumDetailsAdapter(Context mContext, ArrayList<MusicFiles> albumFiles) {
        this.mContext = mContext;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @NotNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.album_music_items, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyHolder holder, final int position) {
        holder.album_name.setText(albumFiles.get(position).getTitle());
        holder.artist_name.setText(albumFiles.get(position).getArtist());
        int duration = Integer.parseInt(albumFiles.get(position).getDuration()) / 1000;
        holder.alb_duration.setText(formattedTime(duration));
        byte[] image = null;
        try {
            image = getAlbumArt(albumFiles.get(position).getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (image != null) {
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album_image);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.musicicon)
                    .into(holder.album_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(mContext, PlayerActivity.class);
                    intent.putExtra("sender", "albumDetails");
                    intent.putExtra("positionAlbum", currentPosition);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    private String formattedTime(int albCurrentPosition) {
        String totalout = "";
        String totalNew = "";
        String seconds = String.valueOf(albCurrentPosition % 60);
        String minutes = String.valueOf(albCurrentPosition / 60);
        totalout = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1){
            return totalNew;
        }
        else{
            return totalout;
        }
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        ImageView album_image;
        TextView album_name, artist_name, alb_duration;
        public MyHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            album_image = itemView.findViewById(R.id.music_img);
            album_name = itemView.findViewById(R.id.music_file_name);
            artist_name = itemView.findViewById(R.id.artis_name);
            alb_duration = itemView.findViewById(R.id.alb_duration);
        }
    }

    private byte[] getAlbumArt(String uri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}
