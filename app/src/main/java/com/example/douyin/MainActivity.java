package com.example.douyin;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.douyin.bean.Feed;
import com.example.douyin.bean.FeedResponse;
import com.example.douyin.network.IMiniDouyinService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private Adapter adapter;
    private Button video, upload;
    List<Feed> GVRList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForPermission();
        initBtn();

    }

    public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        private Context lContext;

        public Adapter(Context context){
            lContext = context;
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(lContext).inflate(R.layout.video_item,parent,false);
            MyViewHolder holder = new MyViewHolder(view);
            Log.i("adapter", "onCreateViewHolder: ");
            return holder;
        }
        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position){

//            holder.title.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Exercises3.this,ChatRoom.class);
//                    intent.putExtra("description",msgList.get(position).getDescription());
//                    intent.putExtra("time",msgList.get(position).getTime());
//                    intent.putExtra("title",msgList.get(position).getTitle());
//                    intent.putExtra("position",position);
//                    startActivity(intent);
//                }
//            });
            String url = GVRList.get(position).getImage_url();
            String user = GVRList.get(position).getUser_Name();
            String create_time = GVRList.get(position).getCreateAt();
            holder.tv_user.setText(user);
            Glide.with(holder.video_img.getContext()).load(url).into(holder.video_img);
        }
        @Override
        public int getItemCount(){return GVRList.size();}



        class MyViewHolder extends RecyclerView.ViewHolder{
            private TextView tv_user;
            private ImageView video_img;
            public MyViewHolder(View v){
                super(v);
                tv_user = v.findViewById(R.id.tv_user);
                video_img = v.findViewById(R.id.video_img);
            }
        }
    }

    public void initBtn(){
        video = findViewById(R.id.video);
        upload = findViewById(R.id.upload);
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter = new Adapter(this));
    }

    public void checkForPermission(){

    }

    public void requestData(View view)  {
       // mBtn.setText("requesting...");
       // mBtn.setEnabled(false);


        new Thread(){
            @Override
            public void run(){
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://test.androidcamp.bytedance.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Response<FeedResponse> response = null;
                try{
                    response = retrofit.create(IMiniDouyinService.class).getVideo().execute();
                } catch (IOException e){
                    e.printStackTrace();
                }

                if (response.body()!=null){
                    final Response<FeedResponse> finalResponse = response;
                    rv.post(new Runnable() {
                        @Override
                        public void run() {
                            loadPics(finalResponse.body());
                        }
                    });
                }

            }
        }.start();
        restoreBtn();
    }

    private void loadPics(FeedResponse feedResponse) {
        GVRList = feedResponse.getFeeds();
        rv.getAdapter().notifyDataSetChanged();
    }

    private void restoreBtn() {
       // mBtn.setText("Refresh");
       // mBtn.setEnabled(true);
    }

}
