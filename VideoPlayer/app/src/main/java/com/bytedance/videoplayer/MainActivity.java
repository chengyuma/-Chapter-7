package com.bytedance.videoplayer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private Button buttonPlay;
    private Button buttonPause;
    private VideoView videoView;
    private TextView current_time;
    private TextView total_time;
    private SeekBar seekBar;
    private boolean is_start;
    String video_path;

    private static final int REQUEST_CODE = 101;


    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            if (videoView.isPlaying()) {
                int current = videoView.getCurrentPosition();
                seekBar.setProgress(current * 100 / videoView.getDuration());
                current_time.setText(time(videoView.getCurrentPosition()));
            }
            handler.postDelayed(runnable, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent=getIntent();
        is_start = false;

//        Intent intent=getIntent();
//        Bundle extras = intent.getExtras();
        Uri uri = getIntent().getData();
        boolean other_path = false;
        if (uri != null) {
            videoView = findViewById(R.id.videoView);
            if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                String[] projection = {MediaStore.Video.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();
                video_path=cursor.getString(0);
                videoView.setVideoPath(video_path);
                cursor.close();
                videoView.start();
                is_start = true;
                handler.postDelayed(runnable, 0);
                other_path = true;
            }
        }

        if (!other_path) {
            videoView = findViewById(R.id.videoView);
            video_path=getVideoPath(R.raw.bytedance);
            videoView.setVideoPath(video_path);
        }

        buttonPlay = findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.start();
                is_start = true;
                handler.postDelayed(runnable, 0);
            }
        });

        buttonPause = findViewById(R.id.buttonPause);
        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.pause();
                is_start = false;
            }
        });

        current_time = findViewById(R.id.current_time);
        current_time.setText(time(0));

        total_time = findViewById(R.id.total_time);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                total_time.setText(time(videoView.getDuration()));
            }
        });

        seekBar = findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    int duration = videoView.getDuration();
                    videoView.seekTo(duration * i / 100);
                    current_time.setText(time(videoView.getCurrentPosition()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                videoView.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (is_start)
                    videoView.start();
            }
        });
//        ImageView imageView = findViewById(R.id.imageView);
//        String url = "https://s3.pstatp.com/toutiao/static/img/logo.271e845.png";
//        Glide.with(this).load(url).into(imageView);
    }

    private String getVideoPath(int resId) {
        return "android.resource://" + this.getPackageName() + "/" + resId;
    }

    private String time(long msec) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(msec);
        return simpleDateFormat.format(c.getTime());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    public void OnRotate(View view) {
        Intent intent = new Intent(this, LandscapeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("current_position", videoView.getCurrentPosition());
        bundle.putString("video_path", video_path);
        if (is_start)
            bundle.putInt("is_start", 1);
        else
            bundle.putInt("is_start", 0);
        intent.putExtra("video", bundle);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == LandscapeActivity.AUTHOR_CODE) {
            if (data != null) {
                int current_position = data.getIntExtra("current_position", 0);
                videoView.seekTo(current_position);
                if (is_start)
                    videoView.start();
            }
        }
    }
}

