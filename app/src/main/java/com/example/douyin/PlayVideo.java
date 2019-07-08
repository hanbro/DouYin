package com.example.douyin;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

public class PlayVideo extends AppCompatActivity {
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        initBtn();

    }
    private void initBtn(){

    }

    private void playVideo(){
        String name =  getIntent().getStringExtra("user_name");
        String url =  getIntent().getStringExtra("video_url");
        videoView.setVideoURI(url);
        Log.d("jzh", "onActivityResult: " + url);
        videoView.start();
    }
}
