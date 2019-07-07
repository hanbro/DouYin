package com.example.douyin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.douyin.bean.GetVideoResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private Adapter adapter;
    private Button video, upload, transcribe;
    List<GetVideoResponse> GVRList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForPermission();
        initBtn();
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter = new Adapter(this));
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        private Context lContext;

        public Adapter(Context context){
            lContext = context;
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(lContext).inflate(R.layout.activity_get_video,parent,false);
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
            String url = GVRList.get(position).getUrl();
            String user = GVRList.get(position).getId();
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
        transcribe = findViewById(R.id.transcribe);
        upload = findViewById(R.id.upload);
    }

    public void checkForPermission(){

    }

    public void requestData(View view) throws IOException {
        mBtn.setText("requesting...");
        mBtn.setEnabled(false);

        // TODO-C1 (3) Send request for 5 random cats here, don't forget to use {@link retrofit2.Call#enqueue}
        // Call restoreBtn() and loadPics(response.body()) if success
        // Call restoreBtn() if failure
        new Thread(){
            @Override
            public void run(){
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.thecatapi.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Response<List<Cat>> response = null;
                try{
                    response = retrofit.create(ICatService.class).randomCat().execute();
                } catch (IOException e){
                    e.printStackTrace();
                }

                if (response.body()!=null){
                    final Response<List<Cat>> finalResponse = response;
                    mRv.post(new Runnable() {
                        @Override
                        public void run() {
                            loadPics(finalResponse.body());
                        }
                    });
                }
                Log.d(TAG, "run: 日志");
            }
        }.start();
        restoreBtn();
    }

    private void loadPics(List<Cat> cats) {
        mCats = cats;
        mRv.getAdapter().notifyDataSetChanged();
    }

    private void restoreBtn() {
        mBtn.setText("Refresh");
        mBtn.setEnabled(true);
    }

}
