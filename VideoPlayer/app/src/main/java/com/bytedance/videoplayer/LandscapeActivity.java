package com.bytedance.videoplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.VideoView;

public class LandscapeActivity extends AppCompatActivity {
    public static final int AUTHOR_CODE = 102;

    private VideoView videoView;
    private View view;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landscape);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Bundle video = extras.getBundle("video");
            if (video != null) {
                int current_position = video.getInt("current_position");
                String video_path = video.getString("video_path");
                int is_start = video.getInt("is_start");
                videoView = findViewById(R.id.videoView2);
                videoView.setVideoPath(video_path);
                videoView.seekTo(current_position);
                if (is_start == 1)
                    videoView.start();
            }
        }
    }

    @Override
    public void finish(){
        Intent data = new Intent();
        data.putExtra("current_position", videoView.getCurrentPosition());
        setResult(AUTHOR_CODE, data);
        super.finish();
    }
}
