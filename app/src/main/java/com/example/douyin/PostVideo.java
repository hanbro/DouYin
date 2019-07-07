package com.example.douyin;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.douyin.bean.PostVideoResponse;
import com.example.douyin.network.IMiniDouyinService;
import com.example.douyin.utils.ResourceUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostVideo extends AppCompatActivity {
    private static final String TAG = "PostVideo";
    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final int REQUEST_VIDEO_CAPTURE = 3;

    private Button postBtn;
    private Button selectBtn;
    private Button recordBtn;
    private VideoView videoView;
    public Uri mSelectedImage;
    private Uri mSelectedVideo;

    /*  测试是注意几点
     *  1.重复选择video
     *  2.没有选择video，然后点击post
     *  3.第一次post成功，第二次不选择video点击post
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_video);
    }

    public void initBtn(){
        //connect button in view  and method;
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(PostVideo.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    public void recordVideo(){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,REQUEST_VIDEO_CAPTURE);
        }
    }
    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select video"),PICK_VIDEO);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if (resultCode == RESULT_OK && null != data) {

            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                Log.d(TAG, "selectedImage = " + mSelectedImage);

            } else if (requestCode == PICK_VIDEO || requestCode == REQUEST_VIDEO_CAPTURE) {

                mSelectedVideo = data.getData();
                videoView.setVideoURI(mSelectedVideo);
                Log.d(TAG, "mSelectedVideo = " + mSelectedVideo);
                videoView.start();

            }
            else if(requestCode == REQUEST_VIDEO_CAPTURE){

            }
        }
    }
    private void postVideo() {
        if(mSelectedVideo == null || mSelectedImage == null){
            Toast.makeText(PostVideo.this,"please select a video to post",Toast.LENGTH_SHORT).show();
            return ;
        }
        postBtn.setText("POSTING...");
        /*if(ContextCompat.checkSelfPermission(this,"android.permission.READ_EXTERNAL_STORAGE")!=0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }*/
        postBtn.setEnabled(false);
        final MultipartBody.Part coverImage = getMultipartFromUri("cover_image",mSelectedImage);
        final MultipartBody.Part video = getMultipartFromUri("video",mSelectedVideo);

        new Thread() {
            @Override
            public void run() {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://test.androidcamp.bytedance.com/")
                        .addConverterFactory(GsonConverterFactory.create()).build();

                Response<PostVideoResponse> response = null;
                try {
                    response = retrofit.create(IMiniDouyinService.class)
                            .postVideo("16061198","electronic",
                                    coverImage,video).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mSelectedImage = null;
                mSelectedVideo = null;

                if (response.body().isSuccess() != false) {
                    /*mRv.post(new Runnable() {
                        public void run() {
                            postBtn.setText("Post");
                            postBtn.setEnabled(true);
                            Toast.makeText(PostVideo.this,"post success",Toast.LENGTH_SHORT).show();

                        }
                    });*/
                    postBtn.setText("Post");
                    postBtn.setEnabled(true);
                    Toast.makeText(PostVideo.this,"post success",Toast.LENGTH_SHORT).show();


                } else Log.d(TAG, "selected video is null");
            }
        }.start();

    }
}
