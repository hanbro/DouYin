package com.example.douyin;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;

import java.net.URI;

public class PlayVideo extends AppCompatActivity {
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        initBtn();
        playVideo();

    }
    private void initBtn(){
        videoView = findViewById(R.id.video_view);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoView.isPlaying()){
                    videoView.pause();
                }
                else videoView.start();
            }
        });
    }

    private void playVideo(){

        String name =  getIntent().getStringExtra("user_name");
        String url =  getIntent().getStringExtra("video_url");
        videoView.setVideoURI(Uri.parse(url));

        Log.d("jzh", "onActivityResult: " + url);
        videoView.start();
    }


}
