package com.example.douyin;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
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

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Images.Thumbnails.MINI_KIND;

public class PostVideo extends AppCompatActivity {
    private static final String TAG = "PostVideo";
    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final int REQUEST_VIDEO_CAPTURE = 3;

    private ImageButton cancelBtn;
    private ImageButton postBtn;
    private ImageButton selectBtn;
    private ImageButton recordBtn;
    private VideoView videoView;
    private Uri mSelectedImage;
    private Uri mSelectedVideo;

    /*  测试是注意几点
     *  1.重复选择video
     *  2.没有选择video，然后点击post
     *  3.第一次post成功，第二次不选择video点击post
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("上传视频");
        setContentView(R.layout.activity_post_video);
        initBtn();
    }

    public void initBtn(){
        //connect button in view  and method;
        videoView = findViewById(R.id.img);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedVideo!=null){
                    if(videoView.isPlaying()){
                        videoView.pause();
                    }
                    else {
                        videoView.start();
                    }
                }
            }
        });
        selectBtn = findViewById(R.id.btn_album);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVideo();
            }
        });
        recordBtn = findViewById(R.id.btn_record);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordVideo();
            }
        });
        postBtn = findViewById(R.id.btn_upload);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postData_prepare();
            }
        });
        cancelBtn = findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(cancelBtn.getContext()).setTitle("提示")//设置对话框标题
                        .setMessage("当前选择视频图片都将被取消，是否继续取消上传？")//设置显示的内容
                        .setPositiveButton("继续残忍取消", new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                mSelectedImage = null;
                                mSelectedVideo = null;
                                finish();
                            }
                        }).setNegativeButton("不了，我在想想", new DialogInterface.OnClickListener() {//添加返回按钮
                            @Override
                             public void onClick(DialogInterface dialog, int which) {//响应事件

                            }
                }).show();//在按键响应事件中显示此对话框
            }
        });
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
    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,PICK_IMAGE);
        /*imgFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if(imgFile!=null){
            Uri fileUri = FileProvider.getUriForFile(this,"com.bytedance.camera.demo",imgFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);

        }*/

    }
    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select picture"),PICK_IMAGE);

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
                Log.d(TAG, "mSelectedVideo = " + mSelectedVideo.getLastPathSegment());
                videoView.start();
            }

        }
    }
    public Bitmap getVideoThumb(Uri uri) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(this,uri);
        return  media.getFrameAtTime();

    }

    public void postData_prepare(){
        if(mSelectedVideo == null){
            mSelectedImage = null;
            Toast.makeText(PostVideo.this,"please select a video to post",Toast.LENGTH_SHORT).show();
            return ;
        }
        else if(mSelectedImage == null){
            new AlertDialog.Builder(this).setTitle("提示")//设置对话框标题
                    .setMessage("当前未选择封面图片，是否进入相册中选择图片？")//设置显示的内容
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                            chooseImage();
                            if(mSelectedImage == null){
                                mSelectedImage = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), getVideoThumb(mSelectedVideo), null,null));
                            }
                            postVideo();
                        }
                    }).setNegativeButton("使用系统生成图片", new DialogInterface.OnClickListener() {//添加返回按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {//响应事件
                    mSelectedImage = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), getVideoThumb(mSelectedVideo), null,null));
                    postVideo();
                }
            }).setNeutralButton("现在拍一张",new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    takePicture();
                    if(mSelectedImage == null){
                        mSelectedImage = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), getVideoThumb(mSelectedVideo), null,null));
                    }
                    postVideo();
                }
            }).show();//在按键响应事件中显示此对话框


        }
    }
    private void postVideo() {
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
                    videoView.post(new Runnable() {
                        public void run() {
                            Toast.makeText(PostVideo.this,"post success",Toast.LENGTH_SHORT).show();

                        }
                    });



                } else Log.d(TAG, "selected video is null");
            }
        }.start();

    }
}
