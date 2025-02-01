package com.example.m_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.palette.graphics.Palette;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.Layout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static com.example.m_app.AlbumDetailsAdapter.albumFiles;
import static com.example.m_app.MainActivity.repeatBoolean;
import static com.example.m_app.MainActivity.shuffleBoolean;
import static com.example.m_app.MusicAdapter.mFiles;

public class PlayerActivity extends AppCompatActivity implements ActionPlaying, ServiceConnection {

    TextView song_name, artist_name, duration_played, duration_total, album_name, textNowplaying;
    ImageView cover_art, nextBtn, prevBtn, backBtn, shuffleBtn, repeatBtn;
    static byte[] artist_image;
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;

    int position = -1;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    static Uri uri;
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;
    MusicService musicService;
    static MusicService passMusicService;
    static GradientDrawable gradientDrawableBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("playerActivitypass", true);
        editor.commit();
        NowPlayingFragmentBottom.setLayoutVisible();
        initViews();
        getIntenMethod();
        if (shuffleBoolean && !repeatBoolean){
            shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
        }else if (!shuffleBoolean && repeatBoolean){
            repeatBtn.setImageResource(R.drawable.ic_repeat_on);
        }else if (shuffleBoolean && repeatBoolean){
            shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
            repeatBtn.setImageResource(R.drawable.ic_repeat_on);
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicService != null && fromUser){
                    musicService.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicService != null){
                    int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffleBoolean)
                {
                    shuffleBoolean = false;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_off);
                }else {
                    shuffleBoolean = true;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatBoolean)
                {
                    repeatBoolean = false;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_off);

                }else {
                    repeatBoolean = true;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_on);
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void prevThreadBtn() {
        prevThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            prevBtnClicked();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                });
            }
        };
        prevThread.start();
    }

    public void prevBtnClicked() throws IOException {
        if (musicService.isPlaying()){
            musicService.stop();
            musicService.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() - 1);
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position -1));
            }else if (!shuffleBoolean && repeatBoolean){
                repeatBtn.setImageResource(R.drawable.ic_repeat_on);
            }else if (shuffleBoolean && repeatBoolean){
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
                repeatBtn.setImageResource(R.drawable.ic_repeat_on);
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            album_name.setText(listSongs.get(position).getAlbum());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            musicService.showNotification(R.drawable.ic_pause);
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause);
            musicService.start();
            passMusicService = musicService;

            byte[] art = artist_image;
            if (art != null){
                Glide.with(getBaseContext()).load(art)
                        .into(NowPlayingFragmentBottom.albumArt);
            }else{
            }
            NowPlayingFragmentBottom.songName.setText(listSongs.get(position).getTitle());
            NowPlayingFragmentBottom.artist.setText(listSongs.get(position).getArtist());
        }
        else {
            musicService.stop();
            musicService.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() - 1);
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position -1));
            }else if (!shuffleBoolean && repeatBoolean){
                repeatBtn.setImageResource(R.drawable.ic_repeat_on);
            }else if (shuffleBoolean && repeatBoolean){
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
                repeatBtn.setImageResource(R.drawable.ic_repeat_on);
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            album_name.setText(listSongs.get(position).getAlbum());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            musicService.showNotification(R.drawable.ic_play);
            playPauseBtn.setBackgroundResource(R.drawable.ic_play);
            passMusicService = musicService;

            byte[] art = artist_image;
            if (art != null){
                Glide.with(getBaseContext()).load(art)
                        .into(NowPlayingFragmentBottom.albumArt);
            }else{
            }
            NowPlayingFragmentBottom.songName.setText(listSongs.get(position).getTitle());
            NowPlayingFragmentBottom.artist.setText(listSongs.get(position).getArtist());
        }
    }

    private void nextThreadBtn() {
        nextThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            nextBtnClicked();
                        } catch (IOException e) {


                        }

                    }
                });
            }
        };
        nextThread.start();
    }

    public void nextBtnClicked() throws IOException {
        if (musicService.isPlaying()){
            musicService.stop();
            musicService.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() - 1);
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % listSongs.size());
            }else if (!shuffleBoolean && repeatBoolean){
                repeatBtn.setImageResource(R.drawable.ic_repeat_on);
            }else if (shuffleBoolean && repeatBoolean){
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
                repeatBtn.setImageResource(R.drawable.ic_repeat_on);
            }
            //else position will be position
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            album_name.setText(listSongs.get(position).getAlbum());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            musicService.showNotification(R.drawable.ic_pause);
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause);
            musicService.start();
            passMusicService = musicService;

            byte[] art = artist_image;
            if (art != null){
                Glide.with(getBaseContext()).load(art)
                        .into(NowPlayingFragmentBottom.albumArt);
            }else{
            }
            NowPlayingFragmentBottom.songName.setText(listSongs.get(position).getTitle());
            NowPlayingFragmentBottom.artist.setText(listSongs.get(position).getArtist());
        }
        else {
            musicService.stop();
            musicService.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() - 1);
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % listSongs.size());
            }else if (!shuffleBoolean && repeatBoolean){
                repeatBtn.setImageResource(R.drawable.ic_repeat_on);
            }else if (shuffleBoolean && repeatBoolean){
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
                repeatBtn.setImageResource(R.drawable.ic_repeat_on);
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            album_name.setText(listSongs.get(position).getAlbum());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            musicService.showNotification(R.drawable.ic_play);
            playPauseBtn.setBackgroundResource(R.drawable.ic_play);
            passMusicService = musicService;

            byte[] art = artist_image;
            if (art != null){
                Glide.with(getBaseContext()).load(art)
                        .into(NowPlayingFragmentBottom.albumArt);
            }else{
            }
            NowPlayingFragmentBottom.songName.setText(listSongs.get(position).getTitle());
            NowPlayingFragmentBottom.artist.setText(listSongs.get(position).getArtist());
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    private void playThreadBtn() {
        playThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            playPauseBtnClicked();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                });
            }
        };
        playThread.start();
    }

    public void playPauseBtnClicked() throws IOException {
        if (musicService.isPlaying()){
            playPauseBtn.setImageResource(R.drawable.ic_play);
            musicService.showNotification(R.drawable.ic_play);
            NowPlayingFragmentBottom.playPauseBtn.setImageResource(R.drawable.ic_play);
            musicService.pause();
            if (shuffleBoolean && !repeatBoolean){
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
            }else if (!shuffleBoolean && repeatBoolean){
                repeatBtn.setImageResource(R.drawable.ic_repeat_on);
            }else if (shuffleBoolean && repeatBoolean){
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
                repeatBtn.setImageResource(R.drawable.ic_repeat_on);
            }
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            passMusicService = musicService;
        }
        else{
            musicService.showNotification(R.drawable.ic_pause);
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            NowPlayingFragmentBottom.playPauseBtn.setImageResource(R.drawable.ic_pause);
            musicService.start();
            if (shuffleBoolean && !repeatBoolean){
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
            }else if (!shuffleBoolean && repeatBoolean){
                repeatBtn.setImageResource(R.drawable.ic_repeat_on);
            }else if (shuffleBoolean && repeatBoolean){
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
                repeatBtn.setImageResource(R.drawable.ic_repeat_on);
            }
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            passMusicService = musicService;
        }
    }

    private String formattedTime(int mCurrentPosition) {
        String totalout = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalout = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1){
            return totalNew;
        }
        else{
            return totalout;
        }
    }

    private void getIntenMethod() {
        String sender = getIntent().getStringExtra("sender");
        String musicAdapt = getIntent().getStringExtra("musicAdapter");
        if (sender != null && sender.equals("albumDetails")){
            position = getIntent().getIntExtra("positionAlbum",-1);
            listSongs = albumFiles;
        }
        else{
            position = getIntent().getIntExtra("positionMfiles",-1);
            listSongs = mFiles;
        }
        if(listSongs != null)
        {
            NowPlayingFragmentBottom.playPauseBtn.setImageResource(R.drawable.ic_pause);
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }


        if (musicService != null){
            musicService.stop();
            musicService.release();
        }

        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("servicePosition", position);
        startService(intent);
    }

    private void initViews(){
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.song_artist);
        album_name = findViewById(R.id.song_album);
        duration_played = findViewById(R.id.durationPlayed);
        duration_total = findViewById(R.id.durationTotal);
        cover_art = findViewById(R.id.cover_art);
        nextBtn = findViewById(R.id.id_next);
        prevBtn = findViewById(R.id.id_prev);
        backBtn = findViewById(R.id.back_btn);
        shuffleBtn = findViewById(R.id.id_shuffle);
        repeatBtn = findViewById(R.id.id_repeat);
        playPauseBtn = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekBar);
        textNowplaying = findViewById(R.id.nowplaing);
    }
    public boolean isColorDark(int color){
        double darkness = 1-(0.299*Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
        if(darkness<0.5){
            return false;
        }else{
            return true;
        }
    }
    private void metaData(Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(listSongs.get(position).getDuration()) / 1000;
        duration_total.setText (formattedTime(durationTotal));
        byte[] art = retriever.getEmbeddedPicture();
        artist_image = art;
        Bitmap bitmap;
        if (art != null){
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            ImageAnimation(this, cover_art, bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable @org.jetbrains.annotations.Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch != null)
                    {
                        ImageView gredient = findViewById(R.id.imageViewGredient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gredient.setImageResource(R.drawable.gredient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), 0x00000000});
                        gredient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0x44444444, swatch.getRgb()});
                        GradientDrawable mContainer_gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0x44444444, swatch.getRgb()});
                        NowPlayingFragmentBottom.bottom_bac_frag.setBackground(gradientDrawableBg);
                        mContainer.setBackground(mContainer_gradientDrawableBg);

                        song_name.setTextColor(swatch.getTitleTextColor());
                        artist_name.setTextColor(swatch.getBodyTextColor());
                        album_name.setTextColor(swatch.getTitleTextColor());
                        textNowplaying.setTextColor(swatch.getBodyTextColor());
                        NowPlayingFragmentBottom.songName.setTextColor(swatch.getBodyTextColor());
                        NowPlayingFragmentBottom.artist.setTextColor(swatch.getTitleTextColor());
                        if (isColorDark(swatch.getRgb())== true){
                            int ColorValue = Color.parseColor("#E8DFDF");
                            ImageViewCompat.setImageTintList(playPauseBtn, ColorStateList.valueOf(ColorValue));
                            ImageViewCompat.setImageTintList(NowPlayingFragmentBottom.playPauseBtn, ColorStateList.valueOf(ColorValue));
                        }else{
                            int ColorValue = Color.parseColor("#353131");
                            ImageViewCompat.setImageTintList(playPauseBtn, ColorStateList.valueOf(ColorValue));
                            ImageViewCompat.setImageTintList(NowPlayingFragmentBottom.playPauseBtn, ColorStateList.valueOf(ColorValue));
                        }
                        playPauseBtn.setBackgroundTintList(ColorStateList.valueOf(swatch.getRgb()));
                        NowPlayingFragmentBottom.playPauseBtn.setBackgroundTintList(ColorStateList.valueOf(swatch.getRgb()));

                    }
                    else {
                        ImageView gredient = findViewById(R.id.imageViewGredient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gredient.setImageResource(R.drawable.gredient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, 0x00000000});
                        gredient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg_mContainer = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, 0xff000000});
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, 0xff000000});
                        NowPlayingFragmentBottom.bottom_bac_frag.setBackground(gradientDrawableBg);
                        mContainer.setBackground(gradientDrawableBg_mContainer);
                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.DKGRAY);
                        album_name.setTextColor(Color.WHITE);
                    }
                }

            });
        }
        else{
            if (isValidContextForGlide(this)){
                Glide.with(this)
                        .asBitmap()
                        .load(R.drawable.musicicon)
                        .into(cover_art);
            }

            ImageView gredient = findViewById(R.id.imageViewGredient);
            RelativeLayout mContainer = findViewById(R.id.mContainer);
            gredient.setImageResource(R.drawable.gredient_bg);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            song_name.setTextColor(Color.WHITE);
            artist_name.setTextColor(Color.DKGRAY);
            album_name.setTextColor(Color.WHITE);
        }
    }

    public void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap)
    {
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service)  {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
        musicService.setCallBack(this);
        seekBar.setMax(musicService.getDuration() / 1000);
        metaData(uri);
        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
        album_name.setText(listSongs.get(position).getAlbum());
        musicService.OnCompleted();
        try {
            musicService.showNotification(R.drawable.ic_pause);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        passMusicService = musicService;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }

}